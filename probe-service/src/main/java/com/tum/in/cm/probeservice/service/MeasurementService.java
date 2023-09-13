package com.tum.in.cm.probeservice.service;

import com.tum.in.cm.probeservice.component.StopArbitraryMeasurementTask;
import com.tum.in.cm.probeservice.exception.CustomProcessExecutionException;
import com.tum.in.cm.probeservice.util.Constants;
import com.tum.in.cm.probeservice.web.ws.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static com.tum.in.cm.probeservice.util.Utils.executeProcessAndReturnOutput;
import static com.tum.in.cm.probeservice.util.Utils.sendResultDataToConnector;

@Service
@Slf4j
public class MeasurementService {
    @Autowired
    private Environment environment;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    public void runMeasurement(InternalMeasurementRequestObject measurementRequestObject) {
        String connectorIpPort = environment.getProperty("connector.service.ip.port");
        try {
            log.info("Running measurement");
            List<String> strings = new ArrayList<>();
            String userHomeDirectory = System.getProperty("user.home");
            log.info("User home directory: " + userHomeDirectory);
            int durationInMinutes = 0;
            long execTimestamp = Instant.now().getEpochSecond();
            if (measurementRequestObject.getType().equals(Constants.MeasurementType.ARBITRARY)) {
                //Arbitrary measurement
                //Add command and args
                ArbitraryMeasurementSpecification measurementSpecification = (ArbitraryMeasurementSpecification) measurementRequestObject.getMeasurementSpecification();
                durationInMinutes = measurementSpecification.getDurationInMinutes();
                strings.addAll(arbitraryMeasurementPrefixBuilder(measurementSpecification.getEnvVars(), measurementSpecification.isAddLinuxNetworkAdminCapability()));
                strings.addAll(arbitraryMeasurementOutputBuilder(
                        measurementSpecification.getOutputPath(),
                        measurementRequestObject.getMeasurementId(),
                        measurementRequestObject.getProbeId(),
                        execTimestamp
                ));
                strings.add(measurementSpecification.getContainerImagePath());
                if (measurementSpecification.getContainerEntrypointString() != null) {
                    strings.add(measurementSpecification.getContainerEntrypointString());
                }
                if (measurementSpecification.getCmdInputStrings() != null) {
                    strings.addAll(arbitraryMeasurementArgumentsBuilder(measurementSpecification.getCmdInputStrings()));
                }
                //Create folder for measurement
                executeProcessAndReturnOutput(Arrays.asList(
                                "mkdir",
                                "-p",
                                userHomeDirectory
                                        + "/probe/"
                                        + measurementRequestObject.getMeasurementId()
                                        + "/"
                                        + measurementRequestObject.getProbeId()
                                        + "/"
                                        + execTimestamp
                        ),
                        60);
            } else {
                //Predefined measurement
                //Add command and args
                strings.addAll(predefinedMeasurementPrefixBuilder());
                strings.addAll(typeBuilder(measurementRequestObject.getType(), measurementRequestObject.getMeasurementSpecification()));
            }
            log.info("Executing measurement");
            long timeout = measurementRequestObject.getType().equals(Constants.MeasurementType.ARBITRARY) ? 300 : 120;
            String output = executeProcessAndReturnOutput(strings, timeout);
            if (measurementRequestObject.getType().equals(Constants.MeasurementType.ARBITRARY)) {
                //Arbitrary measurement
                //Schedule task to stop container in durationInMinutes minutes
                String containerId = output.replaceAll("\n", "");
                if (containerId.isBlank()) {
                    throw new CustomProcessExecutionException("Failed to spawn container for arbitrary measurement");
                }
                log.info("Scheduling automatic container stop task for arbitrary measurement");
                Instant timestamp = Instant.now().plusSeconds(durationInMinutes * 60L);
                taskScheduler.schedule(new StopArbitraryMeasurementTask(connectorIpPort, measurementRequestObject, execTimestamp, containerId), timestamp);
            } else {
                //Predefined measurement
                log.info("Sending result data to connector for predefined measurement");
                long execStopTimestamp = Instant.now().getEpochSecond();
                sendResultDataToConnector(measurementRequestObject, execTimestamp, execStopTimestamp, connectorIpPort, output.getBytes(), false, true);
            }
        } catch (CustomProcessExecutionException e) {
            //Measurement execution failed
            log.info("Sending result data to connector for failed measurement");
            long execStopTimestamp = Instant.now().getEpochSecond();
            sendResultDataToConnector(measurementRequestObject, measurementRequestObject.getTimestamp(), execStopTimestamp, connectorIpPort, new byte[0], measurementRequestObject.getType().equals(Constants.MeasurementType.ARBITRARY), false);
        }
    }

    public List<String> arbitraryMeasurementPrefixBuilder(Hashtable<String, String> envVars, boolean isAddLinuxNetworkAdminCapability) {
        List<String> strings = new ArrayList<>();
        strings.add("docker");
        strings.add("run");
        strings.add("-d");
        if (isAddLinuxNetworkAdminCapability) {
            strings.add("--cap-add=NET_ADMIN");
        }
        if (envVars != null && !envVars.isEmpty()) {
            for (String var : envVars.keySet()) {
                strings.add("--env");
                strings.add(var + "=" + envVars.get(var));
            }
        }
        return strings;
    }

    public List<String> arbitraryMeasurementOutputBuilder(String outputPath, String measurementId, String probeId, long timestamp) {
        List<String> strings = new ArrayList<>();
        String userHomeDirectory = System.getProperty("user.home");
        strings.add("-v");
        strings.add(userHomeDirectory
                + "/probe/"
                + measurementId
                + "/"
                + probeId
                + "/"
                + timestamp
                + "/:"
                + outputPath
        );
        return strings;
    }

    public List<String> arbitraryMeasurementArgumentsBuilder(ArrayList<String> arrayList) {
        return new ArrayList<>(arrayList);
    }

    public List<String> predefinedMeasurementPrefixBuilder() {
        List<String> strings = new ArrayList<>();
        strings.add("docker");
        strings.add("run");
        strings.add("--rm");
        strings.add("cmnetworkplatform/probe-inner");
        strings.add("sh");
        strings.add("-c");
        return strings;
    }

    public List<String> typeBuilder(Constants.MeasurementType type, MeasurementSpecification measurementSpecification) {
        List<String> strings = new ArrayList<>();
        switch (String.valueOf(type)) {
            case "PING" -> strings.addAll(pingArgsBuilder((PingMeasurementSpecification) measurementSpecification));
            case "TRACEROUTE" ->
                    strings.addAll(tracerouteArgsBuilder((TracerouteMeasurementSpecification) measurementSpecification));
            case "PARIS_TRACEROUTE" ->
                    strings.addAll(parisTracerouteArgsBuilder((ParisTracerouteMeasurementSpecification) measurementSpecification));
            case "DNS" -> strings.addAll(dnsArgsBuilder((DnsMeasurementSpecification) measurementSpecification));
            case "HTTP" -> strings.addAll(httpArgsBuilder((HttpMeasurementSpecification) measurementSpecification));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings) {
            stringBuilder.append(s).append(" ");
        }
        return Collections.singletonList(stringBuilder.toString().trim());
    }

    public List<String> pingArgsBuilder(PingMeasurementSpecification measurementSpecification) {
        List<String> strings = new ArrayList<>();
        int numberOfPackets = measurementSpecification.getNumberOfPackets();
        int packetByteSize = measurementSpecification.getPacketByteSize();
        strings.add("ping");
        strings.add("-c");
        strings.add(numberOfPackets > 0 ? String.valueOf(numberOfPackets) : String.valueOf(3));
        if (packetByteSize > 0) {
            strings.add("-s");
            strings.add(String.valueOf(packetByteSize));
        }
        strings.add("-q");
        strings.add(measurementSpecification.getTarget());
        return strings;
    }

    public List<String> tracerouteArgsBuilder(TracerouteMeasurementSpecification measurementSpecification) {
        List<String> strings = new ArrayList<>();
        int maxHops = measurementSpecification.getMaxHops();
        Constants.TracerouteMethod tracerouteMethod = measurementSpecification.getMethod();
        strings.add("traceroute");
        return getCommonStringsForTraceroute(strings, maxHops, tracerouteMethod, measurementSpecification.getTarget());
    }

    public List<String> parisTracerouteArgsBuilder(ParisTracerouteMeasurementSpecification measurementSpecification) {
        List<String> strings = new ArrayList<>();
        int maxHops = measurementSpecification.getMaxHops();
        Constants.TracerouteMethod tracerouteMethod = measurementSpecification.getMethod();
        strings.add("paris-traceroute");
        return getCommonStringsForTraceroute(strings, maxHops, tracerouteMethod, measurementSpecification.getTarget());
    }

    private List<String> getCommonStringsForTraceroute(List<String> strings, int maxHops, Constants.TracerouteMethod tracerouteMethod, String target) {
        strings.add("-4");
        switch (String.valueOf(tracerouteMethod)) {
            case "UDP" -> strings.add("-U");
            case "ICMP" -> strings.add("-I");
            case "TCP" -> strings.add("-T");
        }
        strings.add("-m");
        strings.add(maxHops > 0 ? String.valueOf(maxHops) : String.valueOf(30));
        strings.add("-q");
        strings.add(String.valueOf(3));
        strings.add("-n");
        strings.add(target);
        return strings;
    }

    public List<String> dnsArgsBuilder(DnsMeasurementSpecification measurementSpecification) {
        List<String> strings = new ArrayList<>();
        strings.add("nslookup");
        strings.add(measurementSpecification.getTarget());
        return strings;
    }

    public List<String> httpArgsBuilder(HttpMeasurementSpecification measurementSpecification) {
        List<String> strings = new ArrayList<>();
        strings.add("curl");
        strings.add("-o");
        strings.add("/dev/null");
        strings.add("-s");
        strings.add("-w");
        strings.add("\"%{json}\"");
        strings.add("-X");
        strings.add(String.valueOf(measurementSpecification.getMethod()));
        if (measurementSpecification.getMethod().equals(Constants.HttpMethod.POST) &&
                measurementSpecification.getQueryString() != null &&
                !measurementSpecification.getQueryString().isBlank()) {
            strings.add("-d");
            strings.add(measurementSpecification.getQueryString());
        }
        strings.add(measurementSpecification.getTarget());
        return strings;
    }
}
