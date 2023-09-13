package com.tum.in.cm.platformservice.web.rest.controller.exposed;

import com.tum.in.cm.platformservice.component.security.HasAuthorizedUserRole;
import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.model.measurement.specification.*;
import com.tum.in.cm.platformservice.model.metric.Metric;
import com.tum.in.cm.platformservice.model.result.Result;
import com.tum.in.cm.platformservice.service.MeasurementService;
import com.tum.in.cm.platformservice.service.MetricService;
import com.tum.in.cm.platformservice.service.ProbeService;
import com.tum.in.cm.platformservice.service.ResultService;
import com.tum.in.cm.platformservice.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.tum.in.cm.platformservice.util.Constants.FORBIDDEN_MSG;
import static com.tum.in.cm.platformservice.util.Constants.RESULT_NOT_FOUND_MSG;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "Result")
public class ResultController {
    @Autowired
    private ResultService resultService;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private ProbeService probeService;

    @Autowired
    private MetricService metricService;

    /**
     * Endpoint to fetch results, if they exist, by measurementId
     */
    @Operation(summary = "Get result for a measurement by the measurement's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/octet-stream")}),
            @ApiResponse(responseCode = "404", description = RESULT_NOT_FOUND_MSG, content = @Content),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/results/{measurementId}", produces = "application/octet-stream")
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    public void getResultByMeasurementId(@PathVariable(value = "measurementId") String measurementId,
                                         HttpServletResponse response) throws CustomNotFoundException, IOException {
        Measurement measurement = measurementService.findByMeasurementId(measurementId);
        Constants.MeasurementType measurementType = measurement.getType();
        ArrayList<Result> resultList = (ArrayList<Result>) resultService.findAllByMeasurementId(measurementId);
        long lowTimestamp = 1L;
        long highTimestamp = 1L;
        ArrayList<String> probeList = (ArrayList<String>) probeService.buildProbeListFromSpecification(measurement.getProbeSpecification());
        ArrayList<Metric> metrics = new ArrayList<>();
        for (Result result : resultList) {
            lowTimestamp = result.getExecTimestamp();
            highTimestamp = result.getExecStopTimestamp();
            metrics.addAll(metricService.findAllByProbeIdAndTimestampIsBetween(result.getProbeId(), lowTimestamp - 1, highTimestamp + 1));
        }
        metrics.sort(Comparator.comparing(Metric::getTimestamp));
        StringBuilder metricsStringBuilder = new StringBuilder();
        HashMap<String, ArrayList<Metric>> metricsMap = new HashMap<>();
        for (Metric metric : metrics) {
            String probeId = metric.getProbeId();
            if (metricsMap.containsKey(probeId)) {
                ArrayList<Metric> metricArrayList = metricsMap.get(probeId);
                metricArrayList.add(metric);
                metricsMap.replace(probeId, metricArrayList);
            } else {
                ArrayList<Metric> metricArrayList = new ArrayList<>();
                metricArrayList.add(metric);
                metricsMap.put(probeId, metricArrayList);
            }
        }
        JSONObject jsonOutput = new JSONObject();
        for (String probeId : metricsMap.keySet()) {
            jsonOutput.put("mmt_id", measurementId);
            jsonOutput.put("prb_id", probeId);
            JSONArray metricsArray = new JSONArray();
            ArrayList<Metric> metricArrayList = metricsMap.get(probeId);
            for (Metric metric : metricArrayList) {
                JSONObject metricObject = new JSONObject();
                byte[] data = metric.getData().getData();
                String input = new String(data);
                JSONObject jsonInput = new JSONObject(input);
                metricObject.put("metric_timestamp", metric.getTimestamp());
                metricObject.put("metric", jsonInput);
                metricsArray.put(metricObject);
            }
            jsonOutput.put("metrics", metricsArray);
            metricsStringBuilder.append(jsonOutput);
            metricsStringBuilder.append("\n");
        }
        byte[] outputMetricsData = metricsStringBuilder.toString().getBytes();
        if (measurementType.equals(Constants.MeasurementType.ARBITRARY)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            for (Result result : resultList) {
                byte[] data = result.getData().getData();
                ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
                ZipInputStream zipInputStream = new ZipInputStream(byteStream);
                byte[] buffer = new byte[1024];
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while (zipEntry != null) {
                    if (!zipEntry.getName().equals(result.getProbeId() + "/")) {
                        zipOutputStream.putNextEntry(zipEntry);
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            zipOutputStream.write(buffer, 0, length);
                        }
                        zipOutputStream.closeEntry();
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
                zipInputStream.closeEntry();
                zipInputStream.close();
            }
            //Write metrics
            ZipEntry entry = new ZipEntry("metrics.json");
            entry.setSize(outputMetricsData.length);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(outputMetricsData);
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
            zipOutputStream.close();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/octet-stream");
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + measurementId + ".zip" + "\"");
            response.getOutputStream().write(byteArrayOutputStream.toByteArray());
        } else {
            String target = "";
            switch (String.valueOf(measurementType)) {
                case "HTTP" -> {
                    HttpMeasurementSpecification measurementSpecification = (HttpMeasurementSpecification) measurement.getMeasurementSpecification();
                    target = measurementSpecification.getTarget();
                }
                case "DNS" -> {
                    DnsMeasurementSpecification measurementSpecification = (DnsMeasurementSpecification) measurement.getMeasurementSpecification();
                    target = measurementSpecification.getTarget();
                }
                case "PING" -> {
                    PingMeasurementSpecification measurementSpecification = (PingMeasurementSpecification) measurement.getMeasurementSpecification();
                    target = measurementSpecification.getTarget();
                }
                case "TRACEROUTE" -> {
                    TracerouteMeasurementSpecification measurementSpecification = (TracerouteMeasurementSpecification) measurement.getMeasurementSpecification();
                    target = measurementSpecification.getTarget();
                }
                case "PARIS_TRACEROUTE" -> {
                    ParisTracerouteMeasurementSpecification measurementSpecification = (ParisTracerouteMeasurementSpecification) measurement.getMeasurementSpecification();
                    target = measurementSpecification.getTarget();
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, ArrayList<Result>> resultsMap = new HashMap<>();
            for (Result result : resultList) {
                String probeId = result.getProbeId();
                if (resultsMap.containsKey(probeId)) {
                    ArrayList<Result> resultArrayList = resultsMap.get(probeId);
                    resultArrayList.add(result);
                    resultsMap.replace(probeId, resultArrayList);
                } else {
                    ArrayList<Result> resultArrayList = new ArrayList<>();
                    resultArrayList.add(result);
                    resultsMap.put(probeId, resultArrayList);
                }
            }
            jsonOutput = new JSONObject();
            for (String probeId : resultsMap.keySet()) {
                jsonOutput.put("mmt_id", measurementId);
                jsonOutput.put("prb_id", probeId);
                jsonOutput.put("mmt_type", measurementType);
                jsonOutput.put("target", target);
                JSONArray resultsArray = new JSONArray();
                ArrayList<Result> resultArrayList = resultsMap.get(probeId);
                for (Result result : resultArrayList) {
                    JSONObject resultObject = new JSONObject();
                    byte[] data = result.getData().getData();
                    String input = new String(data);
                    JSONObject jsonInput = new JSONObject(input);
                    resultObject.put("measurement_start_timestamp", result.getExecTimestamp());
                    resultObject.put("measurement_end_timestamp", result.getExecStopTimestamp());
                    resultObject.put("result", jsonInput);
                    resultsArray.put(resultObject);
                }
                jsonOutput.put("results", resultsArray);
                stringBuilder.append(jsonOutput);
                stringBuilder.append("\n");
            }
            byte[] outputData = stringBuilder.toString().getBytes();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);
            //Write results
            ZipEntry entry = new ZipEntry("results.json");
            entry.setSize(outputData.length);
            zos.putNextEntry(entry);
            zos.write(outputData);
            zos.closeEntry();
            //Write metrics
            entry = new ZipEntry("metrics.json");
            entry.setSize(outputMetricsData.length);
            zos.putNextEntry(entry);
            zos.write(outputMetricsData);
            zos.closeEntry();
            zos.finish();
            zos.close();
            byte[] output = byteArrayOutputStream.toByteArray();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/octet-stream");
            response.setContentLength(output.length);
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + measurementId + ".zip" + "\"");
            response.getOutputStream().write(output);
        }
    }
}
