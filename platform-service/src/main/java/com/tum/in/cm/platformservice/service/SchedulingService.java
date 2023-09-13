package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.connector.Connector;
import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.model.measurement.specification.ArbitraryMeasurementSpecification;
import com.tum.in.cm.platformservice.model.probe.Probe;
import com.tum.in.cm.platformservice.util.Constants;
import com.tum.in.cm.platformservice.web.rest.dto.internal.InternalTaskRequestObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

import static com.tum.in.cm.platformservice.util.Constants.CONNECTOR_MEASUREMENT_ENDPOINT;
import static com.tum.in.cm.platformservice.util.Constants.HTTP_PREFIX;

/**
 * Scheduling service containing logic for scheduling measurement jobs.
 */
@Service
@Slf4j
public class SchedulingService {
    @Autowired
    private Environment environment;
    @Autowired
    private ProbeService probeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ConnectorService connectorService;

    public boolean isMeasurementConflicting(Measurement measurement, List<String> probesList) {
        String schedulingAlgorithm = environment.getProperty("connector.scheduling.algorithm");
        //Do not do any conflict detection for On-demand measurement scheduling
        if (schedulingAlgorithm != null && schedulingAlgorithm.equals("demand")) {
            return false;
        }
        Map<Integer, List<Long>> executionTimes = new HashMap<>();
        //Add first execution time start-time end to map
        if (measurement.getType().equals(Constants.MeasurementType.ARBITRARY)) {
            ArbitraryMeasurementSpecification arbitraryMeasurementSpecification = (ArbitraryMeasurementSpecification) measurement.getMeasurementSpecification();
            int specifiedMeasurementDurationInMinutes = arbitraryMeasurementSpecification.getDurationInMinutes();
            executionTimes.put(0, new ArrayList<>(Arrays.asList(
                    measurement.getScheduledStartTimestamp(),
                    (measurement.getScheduledStartTimestamp() + ((specifiedMeasurementDurationInMinutes + 1L) * 60L))
            )));
        } else {
            executionTimes.put(0, new ArrayList<>(Arrays.asList(
                    measurement.getScheduledStartTimestamp(),
                    (measurement.getScheduledStartTimestamp() + (2L * 60L))
            )));
        }
        //Add for repeats, if any
        for (int rep = 1; rep <= measurement.getRepeatSpecification().getNumberOfRepeats(); rep++) {
            if (measurement.getType().equals(Constants.MeasurementType.ARBITRARY)) {
                ArbitraryMeasurementSpecification arbitraryMeasurementSpecification = (ArbitraryMeasurementSpecification) measurement.getMeasurementSpecification();
                int specifiedMeasurementDurationInMinutes = arbitraryMeasurementSpecification.getDurationInMinutes();
                long nextStartTimestamp = executionTimes.get(rep - 1).get(0) + (specifiedMeasurementDurationInMinutes * 60L) + measurement.getRepeatSpecification().getInterval();
                executionTimes.put(rep, new ArrayList<>(List.of(
                        nextStartTimestamp,
                        nextStartTimestamp + ((specifiedMeasurementDurationInMinutes + 1L) * 60L)
                )));
            } else {
                long nextStartTimestamp = executionTimes.get(rep - 1).get(0) + measurement.getRepeatSpecification().getInterval();
                executionTimes.put(rep, new ArrayList<>(List.of(
                        nextStartTimestamp,
                        nextStartTimestamp + (2L * 60L)
                )));
            }
        }
        for (String probeId : probesList) {
            //The following block can be enabled to check for active probe conflicts since we do not take a startTimestamp from users
            /**
             //Optionally check conflicts first for probes that are running an active measurement currently
             try {
             Probe probe = probeService.findByProbeId(probeId);
             if (probe.getStatus().equals(Constants.ProbeStatus.RUNNING)) {
             //Cannot schedule, probe already running active measurement
             return true;
             }
             } catch (CustomNotFoundException e) {
             //Probe does not exist, return true for conflict
             return true;
             }
             **/
            //Check if execution time conflicts are there for preexisting tasks
            for (int rep : executionTimes.keySet()) {
                int conflictingTasks = taskService.countByScheduledStartTimestampBetweenAndProbeId(executionTimes.get(rep).get(0), executionTimes.get(rep).get(1), probeId)
                        + taskService.countByScheduledStopTimestampBetweenAndProbeId(executionTimes.get(rep).get(0), executionTimes.get(rep).get(1), probeId);
                if (conflictingTasks > 0) {
                    log.info("Measurement conflicting, status will be set to failed");
                    return true;
                }
            }
        }
        return false;
    }

    public void scheduleMeasurementJob(Measurement measurement, List<String> probesList) {
        URI uri;
        RestTemplate restTemplate = new RestTemplate();
        String connectorApiKey = environment.getProperty("connector.api.key");
        for (String probeId : probesList) {
            try {
                Probe probe = probeService.findByProbeId(probeId);
                String connectorIpPort = probe.getConnectorIpPort();
                Connector connector = connectorService.findByIpPort(connectorIpPort);
                //Use oakestra's internal IP if available
                if (connector != null && !connector.getOakestraIpPort().isBlank()) {
                    connectorIpPort = connector.getOakestraIpPort();
                }
                uri = UriComponentsBuilder.fromHttpUrl(HTTP_PREFIX + connectorIpPort + CONNECTOR_MEASUREMENT_ENDPOINT).build().toUri();
                InternalTaskRequestObject taskRequestObject = new InternalTaskRequestObject(measurement, probe.getId(), measurement.getScheduledStartTimestamp());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("api_key", connectorApiKey);
                HttpEntity<InternalTaskRequestObject> request = new HttpEntity<>(taskRequestObject, httpHeaders);
                restTemplate.postForObject(uri, request, String.class);
            } catch (CustomNotFoundException e) {
                //Ignored
            } catch (Exception e) {
                //HTTP exception, proceed with next probe
            }
        }
    }
}
