package com.tum.in.cm.connectorservice.web.rest.controller;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.metric.Metric;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.service.MetricService;
import com.tum.in.cm.connectorservice.service.ProbeService;
import com.tum.in.cm.connectorservice.web.rest.dto.request.MetricRequestObject;
import jakarta.validation.Valid;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.tum.in.cm.connectorservice.util.Constants.SUCCESS_MSG;

@RestController
@RequestMapping(value = "/api")
public class MetricController {
    @Autowired
    private MetricService metricService;
    @Autowired
    private ProbeService probeService;

    /**
     * Endpoint to send metrics from the probes
     */
    @PostMapping(value = "/metrics")
    public ResponseEntity<String> sendMetricsDataFromProbe(@RequestHeader("api_key") String api_key, @Valid @RequestBody MetricRequestObject metricRequestObject) {
        Probe probe;
        try {
            probe = probeService.findByProbeId(metricRequestObject.getProbeId());
        } catch (CustomNotFoundException e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String probeApiKey = probe.getApiKey();
        if (!api_key.equals(probeApiKey)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String defaultProbeLocation = probe.getCountry();
        Metric metric = new Metric();
        metric.setProbeId(metricRequestObject.getProbeId());
        metric.setTimestamp(metricRequestObject.getTimestamp());
        String statusData = new String(metricRequestObject.getStatusData());
        String locationData = new String(metricRequestObject.getLocationData());
        JSONObject jsonStatusInput = null;
        JSONObject jsonLocationInput = null;
        if (!statusData.isBlank())
            jsonStatusInput = new JSONObject(statusData);
        if (!locationData.isBlank())
            jsonLocationInput = new JSONObject(locationData);
        JSONObject jsonOutput = new JSONObject();
        jsonOutput.put("status", jsonStatusInput != null ? jsonStatusInput : JSONObject.NULL);
        jsonOutput.put("location", jsonLocationInput != null ? jsonLocationInput : (defaultProbeLocation.isBlank() ? JSONObject.NULL : defaultProbeLocation));
        metric.setData(new Binary(BsonBinarySubType.BINARY, jsonOutput.toString().getBytes()));
        metricService.insert(metric);
        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }
}
