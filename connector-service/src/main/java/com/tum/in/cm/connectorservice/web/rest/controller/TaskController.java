package com.tum.in.cm.connectorservice.web.rest.controller;

import com.tum.in.cm.connectorservice.component.TimeBasedMeasurementExecutionTask;
import com.tum.in.cm.connectorservice.model.task.Task;
import com.tum.in.cm.connectorservice.service.ProbeService;
import com.tum.in.cm.connectorservice.service.TaskService;
import com.tum.in.cm.connectorservice.util.Constants;
import com.tum.in.cm.connectorservice.web.rest.dto.request.ArbitraryMeasurementSpecification;
import com.tum.in.cm.connectorservice.web.rest.dto.request.InternalTaskRequestObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import static com.tum.in.cm.connectorservice.util.Constants.SUCCESS_MSG;

@RestController
@RequestMapping(value = "/api")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProbeService probeService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * Endpoint to receive a measurement task sent from the platform service
     */
    @PostMapping(value = "/measurements")
    public ResponseEntity<String> sendMeasurementTask(@RequestHeader("api_key") String api_key, @Valid @RequestBody InternalTaskRequestObject taskRequestObject) {
        String connectorApiKey = environment.getProperty("connector.api.key");
        if (!api_key.equals(connectorApiKey)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        //Split into tasks
        Task task = new Task();
        task.setMeasurementId(taskRequestObject.getMeasurementId());
        task.setProbeId(taskRequestObject.getProbeId());
        task.setType(taskRequestObject.getType());
        task.setMeasurementSpecification(taskRequestObject.getMeasurementSpecification());
        task.setRepeatSpecification(taskRequestObject.getRepeatSpecification());
        task.setNextExecutionNumber(0);
        task.setScheduledStartTimestamp(taskRequestObject.getScheduledStartTimestamp());
        if (taskRequestObject.getType().equals(Constants.MeasurementType.ARBITRARY)) {
            ArbitraryMeasurementSpecification arbitraryMeasurementSpecification = (ArbitraryMeasurementSpecification) taskRequestObject.getMeasurementSpecification();
            int specifiedMeasurementDurationInMinutes = arbitraryMeasurementSpecification.getDurationInMinutes();
            //Use user specified duration + 1 minute as buffer time for arbitrary measurements
            task.setScheduledStopTimestamp(taskRequestObject.getScheduledStartTimestamp() + ((specifiedMeasurementDurationInMinutes + 1L) * 60L));
        } else {
            //Use 2 minutes as buffer time for predefined measurements
            task.setScheduledStopTimestamp(taskRequestObject.getScheduledStartTimestamp() + (2L * 60L));
        }
        Task insertedTask = taskService.insert(task);
        String schedulingAlgorithm = environment.getProperty("connector.scheduling.algorithm");
        String connectorIpPort = environment.getProperty("connector.service.ip.port");
        if (schedulingAlgorithm != null && schedulingAlgorithm.equals("time")) {
            taskScheduler.schedule(new TimeBasedMeasurementExecutionTask(insertedTask.getId(), probeService, taskService, simpMessagingTemplate, connectorIpPort, taskScheduler), Instant.ofEpochSecond(taskRequestObject.getScheduledStartTimestamp()));
        }
        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }
}
