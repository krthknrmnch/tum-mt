package com.tum.in.cm.connectorservice.component;

import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.model.task.Task;
import com.tum.in.cm.connectorservice.service.ProbeService;
import com.tum.in.cm.connectorservice.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * This class executes startup tasks once Spring context has been initialized.
 */
@Component
@Slf4j
public class StartupTask implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private Environment environment;
    @Autowired
    private ProbeService probeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Running post-startup steps");
        String schedulingAlgorithm = environment.getProperty("connector.scheduling.algorithm");
        if (schedulingAlgorithm != null && schedulingAlgorithm.equals("time")) {
            log.info("Scheduling all pending time based measurement tasks");
            String connectorIpPort = environment.getProperty("connector.service.ip.port");
            List<Probe> connectedProbes = probeService.findAllByConnectorIpPort(connectorIpPort);
            for (Probe probe : connectedProbes) {
                List<Task> tasks = taskService.findAllByProbeId(probe.getId());
                for (Task task : tasks) {
                    taskScheduler.schedule(new TimeBasedMeasurementExecutionTask(task.getId(), probeService, taskService, simpMessagingTemplate, connectorIpPort, taskScheduler), Instant.ofEpochSecond(task.getScheduledStartTimestamp()));
                }
            }
        }
        log.info("Post-startup steps complete");
    }
}
