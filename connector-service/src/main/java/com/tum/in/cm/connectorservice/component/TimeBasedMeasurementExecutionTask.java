package com.tum.in.cm.connectorservice.component;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class TimeBasedMeasurementExecutionTask implements Runnable {

    private String taskId;
    private ProbeService probeService;
    private TaskService taskService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private String connectorIpPort;
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * Time based scheduling algorithm - Strict, time based, provided probe is available.
     * It forwards a measurement task to a probe based on a scheduled start timestamp and probe availability.
     */
    @Override
    public void run() {
        log.info("Scheduled task running for measurement execution with taskId: " + taskId);
        Task task = taskService.findById(taskId).orElse(null);
        if (task == null) {
            return;
        }
        Probe probe = null;
        try {
            probe = probeService.findByProbeId(task.getProbeId());
            if (probe.getConnectorIpPort().equals(connectorIpPort)) {
                if (probe.getStatus().equals(Constants.ProbeStatus.CONNECTED)) {
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
                }
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
                    Task insertedTask = taskService.update(task);
                    if (insertedTask != null) {
                        taskScheduler.schedule(new TimeBasedMeasurementExecutionTask(taskId, probeService, taskService, simpMessagingTemplate, connectorIpPort, taskScheduler), Instant.ofEpochSecond(insertedTask.getScheduledStartTimestamp()));
                    }
                }
            }
        } catch (CustomNotFoundException e) {
            taskService.delete(task);
        }
    }
}
