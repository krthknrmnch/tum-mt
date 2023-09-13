package com.tum.in.cm.connectorservice.web.rest.controller;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.model.result.Result;
import com.tum.in.cm.connectorservice.service.ProbeService;
import com.tum.in.cm.connectorservice.service.ResultService;
import com.tum.in.cm.connectorservice.util.Constants;
import com.tum.in.cm.connectorservice.web.rest.dto.request.ResultRequestObject;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.tum.in.cm.connectorservice.util.Constants.SUCCESS_MSG;

@RestController
@RequestMapping(value = "/api")
public class ResultController {
    @Autowired
    private ResultService resultService;
    @Autowired
    private ProbeService probeService;

    /**
     * Endpoint to send results from the probe services for a measurement
     */
    @PostMapping(value = "/results", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendResultDataFromProbe(@RequestHeader("api_key") String api_key,
                                                          @RequestParam("file") MultipartFile file,
                                                          @RequestParam("resultRequestObject") ResultRequestObject resultRequestObject) throws IOException {
        Probe probe;
        try {
            probe = probeService.findByProbeId(resultRequestObject.getProbeId());
        } catch (CustomNotFoundException e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String probeApiKey = probe.getApiKey();
        if (!api_key.equals(probeApiKey)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        Result newResult = new Result();
        newResult.setMeasurementId(resultRequestObject.getMeasurementId());
        newResult.setProbeId(resultRequestObject.getProbeId());
        newResult.setTimestamp(resultRequestObject.getTimestamp());
        newResult.setExecTimestamp(resultRequestObject.getExecTimestamp() < resultRequestObject.getTimestamp() ? newResult.getTimestamp() : resultRequestObject.getExecTimestamp());
        newResult.setExecStopTimestamp(resultRequestObject.getExecStopTimestamp());
        newResult.setSuccess(resultRequestObject.isSuccess());
        newResult.setType(resultRequestObject.getType());
        if (resultRequestObject.getType().equals(Constants.MeasurementType.ARBITRARY)) {
            newResult.setData(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        } else {
            //Format and store
            Binary binaryData = new Binary(BsonBinarySubType.BINARY, file.getBytes());
            byte[] data = binaryData.getData();
            String input = new String(data);
            switch (String.valueOf(resultRequestObject.getType())) {
                case "HTTP" ->
                        newResult.setData(new Binary(BsonBinarySubType.BINARY, getJsonObjectForHttpInput(input, resultRequestObject.isSuccess()).getBytes()));
                case "DNS" ->
                        newResult.setData(new Binary(BsonBinarySubType.BINARY, getJsonObjectForDnsInput(input, resultRequestObject.isSuccess()).getBytes()));
                case "PING" ->
                        newResult.setData(new Binary(BsonBinarySubType.BINARY, getJsonObjectForPingInput(input, resultRequestObject.isSuccess()).getBytes()));
                case "TRACEROUTE", "PARIS_TRACEROUTE" ->
                        newResult.setData(new Binary(BsonBinarySubType.BINARY, getJsonObjectForTracerouteInput(input, resultRequestObject.isSuccess()).getBytes()));
            }
        }
        resultService.insert(newResult);
        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }

    public String getJsonObjectForHttpInput(String input, boolean isSuccess) {
        JSONObject jsonOutput = new JSONObject();
        JSONObject jsonInput = null;
        if (isSuccess && input != null && !input.isBlank()) {
            jsonInput = new JSONObject(input);
        }
        jsonOutput.put("method", jsonInput != null ? jsonInput.get("method") : JSONObject.NULL);
        jsonOutput.put("url", jsonInput != null ? jsonInput.get("url") : JSONObject.NULL);
        jsonOutput.put("redirect_url", jsonInput != null ? jsonInput.get("redirect_url") : JSONObject.NULL);
        jsonOutput.put("response_code", jsonInput != null ? jsonInput.get("response_code") : JSONObject.NULL);
        jsonOutput.put("content_type", jsonInput != null ? jsonInput.get("content_type") : JSONObject.NULL);
        jsonOutput.put("size_upload", jsonInput != null ? jsonInput.get("size_upload") : JSONObject.NULL);
        jsonOutput.put("size_download", jsonInput != null ? jsonInput.get("size_download") : JSONObject.NULL);
        jsonOutput.put("time_appconnect", jsonInput != null ? jsonInput.get("time_appconnect") : JSONObject.NULL);
        jsonOutput.put("time_connect", jsonInput != null ? jsonInput.get("time_connect") : JSONObject.NULL);
        jsonOutput.put("time_namelookup", jsonInput != null ? jsonInput.get("time_namelookup") : JSONObject.NULL);
        jsonOutput.put("time_pretransfer", jsonInput != null ? jsonInput.get("time_pretransfer") : JSONObject.NULL);
        jsonOutput.put("time_redirect", jsonInput != null ? jsonInput.get("time_redirect") : JSONObject.NULL);
        jsonOutput.put("time_starttransfer", jsonInput != null ? jsonInput.get("time_starttransfer") : JSONObject.NULL);
        jsonOutput.put("time_total", jsonInput != null ? jsonInput.get("time_total") : JSONObject.NULL);
        return jsonOutput.toString();
    }

    public String getJsonObjectForDnsInput(String input, boolean isSuccess) {
        JSONObject jsonOutput = new JSONObject();
        Stream<String> outputStream = input.lines();
        List<String> matches;
        matches = outputStream.filter(string -> string.startsWith("Address:"))
                .map(string -> string.substring(string.lastIndexOf("Address:") + 8).strip())
                .toList();
        jsonOutput.put("server_addr", isSuccess && matches.size() > 0 ? matches.get(0) : JSONObject.NULL);
        jsonOutput.put("dst_addr_1", isSuccess && matches.size() > 1 ? matches.get(1) : JSONObject.NULL);
        jsonOutput.put("dst_addr_2", isSuccess && matches.size() > 2 ? matches.get(2) : JSONObject.NULL);
        return jsonOutput.toString();
    }

    public String getJsonObjectForPingInput(String input, boolean isSuccess) {
        JSONObject jsonOutput = new JSONObject();
        String dst_addr = "";
        int packets_size = 0;
        int packets_sent = 0;
        int packets_rcvd = 0;
        float packet_loss = 0;
        float min = 0;
        float avg = 0;
        float max = 0;
        List<String> stringList = input.lines().toList();
        try {
            for (String string : stringList) {
                if (string.contains("data bytes")) {
                    String[] firstArray = string.strip().split("\\):");
                    String[] firstSubArray = firstArray[0].strip().split("\\(");
                    dst_addr = firstSubArray[1];
                    String[] secondSubArray = firstArray[1].strip().split("data bytes");
                    packets_size = Integer.parseInt(secondSubArray[0].strip());
                }
                if (string.contains("packets transmitted")) {
                    String[] firstArray = string.strip().split(",");
                    String[] firstSubArray = firstArray[0].strip().split("packets transmitted");
                    packets_sent = Integer.parseInt(firstSubArray[0].strip());
                    String[] secondSubArray = firstArray[1].strip().split("packets received");
                    packets_rcvd = Integer.parseInt(secondSubArray[0].strip());
                    String[] thirdSubArray = firstArray[2].strip().split("packet loss");
                    packet_loss = Float.parseFloat(thirdSubArray[0].strip().replace("%", ""));
                }
                if (string.contains("round-trip")) {
                    String[] firstArray = string.strip().split("=");
                    String[] secondArray = firstArray[1].strip().split("ms");
                    String[] finalArray = secondArray[0].strip().split("/");
                    min = Float.parseFloat(finalArray[0].strip());
                    avg = Float.parseFloat(finalArray[1].strip());
                    max = Float.parseFloat(finalArray[2].strip());
                }
            }
        } catch (Exception ignored) {
        }

        jsonOutput.put("dst_addr", isSuccess ? dst_addr : JSONObject.NULL);
        jsonOutput.put("packets_size", isSuccess ? packets_size : JSONObject.NULL);
        jsonOutput.put("packets_sent", isSuccess ? packets_sent : JSONObject.NULL);
        jsonOutput.put("packets_rcvd", isSuccess ? packets_rcvd : JSONObject.NULL);
        jsonOutput.put("packet_loss_pc", isSuccess ? packet_loss : JSONObject.NULL);
        jsonOutput.put("min", isSuccess ? min : JSONObject.NULL);
        jsonOutput.put("avg", isSuccess ? avg : JSONObject.NULL);
        jsonOutput.put("max", isSuccess ? max : JSONObject.NULL);
        return jsonOutput.toString();
    }

    public String getJsonObjectForTracerouteInput(String input, boolean isSuccess) {
        JSONObject jsonOutput = new JSONObject();
        String dst_addr = "";
        int max_hops = 0;
        String from_addr = "";
        float rtt;
        List<String> stringList = input.lines().toList();
        JSONArray topArray = new JSONArray();
        try {
            for (String string : stringList) {
                if (string.contains("hops max")) {
                    String[] firstArray = string.strip().split("\\),");
                    String[] firstSubArray = firstArray[0].strip().split("\\(");
                    dst_addr = firstSubArray[1];
                    String[] secondSubArray = firstArray[1].strip().split("hops max");
                    max_hops = Integer.parseInt(secondSubArray[0].strip());
                } else {
                    JSONObject hopObject = new JSONObject();
                    JSONArray hopArray = new JSONArray();

                    String[] hops = string.strip().split(" ", 2);
                    int hop = Integer.parseInt(hops[0].strip());
                    hopObject.put("hop", hop);
                    if (hops[1].contains("*")) {
                        //Do not extract values
                        hopObject.put("packets", hopArray);
                        topArray.put(hopObject);
                        continue;
                    } else if (hops[1].contains("ms")) {
                        String[] firstArray = hops[1].strip().split("ms");
                        //First packet
                        if (firstArray[0].strip().contains(" ")) {
                            JSONObject packetObject = new JSONObject();
                            String[] secondArray = firstArray[0].strip().split(" ");
                            from_addr = secondArray[0].strip();
                            rtt = Float.parseFloat(secondArray[2].strip());
                            packetObject.put("from", from_addr);
                            packetObject.put("rtt", rtt);
                            hopArray.put(packetObject);
                        }
                        //Second packet
                        if (firstArray[1].strip().contains(" ")) {
                            JSONObject packetObject = new JSONObject();
                            String[] secondArray = firstArray[1].strip().split(" ");
                            from_addr = secondArray[0].strip();
                            rtt = Float.parseFloat(secondArray[2].strip());
                            packetObject.put("from", from_addr);
                            packetObject.put("rtt", rtt);
                            hopArray.put(packetObject);
                        } else {
                            JSONObject packetObject = new JSONObject();
                            rtt = Float.parseFloat(firstArray[1].strip());
                            packetObject.put("from", from_addr);
                            packetObject.put("rtt", rtt);
                            hopArray.put(packetObject);
                        }
                        //Third packet
                        if (firstArray[2].strip().contains(" ")) {
                            JSONObject packetObject = new JSONObject();
                            String[] secondArray = firstArray[2].strip().split(" ");
                            from_addr = secondArray[0].strip();
                            rtt = Float.parseFloat(secondArray[2].strip());
                            packetObject.put("from", from_addr);
                            packetObject.put("rtt", rtt);
                            hopArray.put(packetObject);
                        } else {
                            JSONObject packetObject = new JSONObject();
                            rtt = Float.parseFloat(firstArray[2].strip());
                            packetObject.put("from", from_addr);
                            packetObject.put("rtt", rtt);
                            hopArray.put(packetObject);
                        }
                    }
                    hopObject.put("packets", hopArray);
                    topArray.put(hopObject);
                }
            }
        } catch (Exception ignored) {
        }
        jsonOutput.put("dst_addr", isSuccess ? dst_addr : JSONObject.NULL);
        jsonOutput.put("max_hops", isSuccess ? max_hops : JSONObject.NULL);
        jsonOutput.put("hops", isSuccess ? topArray : JSONObject.NULL);
        return jsonOutput.toString();
    }
}
