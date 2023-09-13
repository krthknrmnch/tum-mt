package com.tum.in.cm.connectorservice.component;

import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.model.task.Task;
import com.tum.in.cm.connectorservice.service.ProbeService;
import com.tum.in.cm.connectorservice.service.TaskService;
import com.tum.in.cm.connectorservice.util.Constants;
import com.tum.in.cm.connectorservice.web.rest.dto.request.ArbitraryMeasurementSpecification;
import com.tum.in.cm.connectorservice.web.ws.dto.request.InternalMeasurementRequestObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class OnDemandMeasurementExecutionTask {

    @Autowired
    private Environment environment;
    @Autowired
    private ProbeService probeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * On-Demand scheduling algorithm - FCFS, queue based, provided probe is available. No time guaranteed execution.
     * This is a scheduled method that runs every 2 minutes.
     * It checks for the next measurement tasks to be executed by probes connected to this service instance.
     * It forwards these tasks to the probes based on timestamps and probe availability.
     */
    @Scheduled(fixedDelay = 120000)
    public void onDemandScheduledMeasurementExecutionTask() {
        String schedulingAlgorithm = environment.getProperty("connector.scheduling.algorithm");
        if (schedulingAlgorithm != null && schedulingAlgorithm.equals("demand")) {
            ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
            if (environment == null) {
                environment = Objects.requireNonNull(applicationContext).getBean(Environment.class);
            }
            if (probeService == null) {
                probeService = Objects.requireNonNull(applicationContext).getBean(ProbeService.class);
            }
            if (taskService == null) {
                taskService = Objects.requireNonNull(applicationContext).getBean(TaskService.class);
            }
            if (simpMessagingTemplate == null) {
                simpMessagingTemplate = Objects.requireNonNull(applicationContext).getBean(SimpMessagingTemplate.class);
            }
            log.info("Scheduled task running for measurement task execution");
            String connectorIpPort = environment.getProperty("connector.service.ip.port");
            if (connectorIpPort != null && !connectorIpPort.isBlank()) {
                List<Probe> connectedProbes = probeService.findAllByConnectorIpPortAndStatus(connectorIpPort, Constants.ProbeStatus.CONNECTED);
                for (Probe probe : connectedProbes) {
                    Task task = taskService.findEarliestTaskForProbe(probe.getId()).orElse(null);
                    if (task != null) {
                        long taskScheduledStartTimestamp = task.getScheduledStartTimestamp();
                        long currentTimestamp = Instant.now().getEpochSecond();
                        //Execute task only if its scheduled time is within 3 minutes or lesser from right now
                        //3 minutes is current spread value
                        if (taskScheduledStartTimestamp - currentTimestamp < 180) {
                            //Exec task
                            log.info("Sending task to probe");
                            simpMessagingTemplate.convertAndSend("/topic/measurement/" + task.getProbeId(),
                                    new InternalMeasurementRequestObject(
                                            task.getMeasurementId(),
                                            task.getProbeId(),
                                            task.getScheduledStartTimestamp(),
                                            task.getType(),
                                            task.getMeasurementSpecification()
                                    ));
                            if (task.getNextExecutionNumber() + 1 > task.getRepeatSpecification().getNumberOfRepeats()) {
                                taskService.delete(task);
                            } else {
                                task.setNextExecutionNumber(task.getNextExecutionNumber() + 1);
                                if (task.getType().equals(Constants.MeasurementType.ARBITRARY)) {
                                    ArbitraryMeasurementSpecification arbitraryMeasurementSpecification = (ArbitraryMeasurementSpecification) task.getMeasurementSpecification();
                                    int specifiedMeasurementDurationInMinutes = arbitraryMeasurementSpecification.getDurationInMinutes();
                                    task.setScheduledStartTimestamp(task.getScheduledStartTimestamp() + (specifiedMeasurementDurationInMinutes * 60L) + task.getRepeatSpecification().getInterval());
                                    //Use user specified duration + 1 minute as buffer time for arbitrary measurements
                                    task.setScheduledStopTimestamp(task.getScheduledStartTimestamp() + ((specifiedMeasurementDurationInMinutes + 1L) * 60L));
                                } else {
                                    task.setScheduledStartTimestamp(task.getScheduledStartTimestamp() + task.getRepeatSpecification().getInterval());
                                    //Use 2 minutes as buffer time for predefined measurements
                                    task.setScheduledStopTimestamp(task.getScheduledStartTimestamp() + (2L * 60L));
                                }
                                taskService.update(task);
                            }
                        }
                    }
                }
            }
        }
    }
}
