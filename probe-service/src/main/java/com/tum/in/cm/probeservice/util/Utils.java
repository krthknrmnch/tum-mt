package com.tum.in.cm.probeservice.util;

import ch.qos.logback.classic.LoggerContext;
import com.tum.in.cm.probeservice.component.ApplicationContextProvider;
import com.tum.in.cm.probeservice.config.ProbeStatusSingletonBeanConfig;
import com.tum.in.cm.probeservice.exception.CustomProcessExecutionException;
import com.tum.in.cm.probeservice.web.rest.dto.MetricRequestObject;
import com.tum.in.cm.probeservice.web.rest.dto.ProbeStatusRequestObject;
import com.tum.in.cm.probeservice.web.rest.dto.ResultRequestObject;
import com.tum.in.cm.probeservice.web.ws.dto.InternalMeasurementRequestObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.tum.in.cm.probeservice.util.Constants.*;

@Slf4j
public final class Utils {

    private Utils() {
    }

    public static boolean sendResultDataToConnector(InternalMeasurementRequestObject measurementRequestObject, long execTimestamp, long execStopTimestamp, String connectorIpPort, byte[] data, boolean isArbitrary, boolean isSuccess) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        ResultRequestObject resultRequestObject = new ResultRequestObject();
        resultRequestObject.setProbeId(measurementRequestObject.getProbeId());
        resultRequestObject.setMeasurementId(measurementRequestObject.getMeasurementId());
        resultRequestObject.setTimestamp(measurementRequestObject.getTimestamp());
        resultRequestObject.setExecTimestamp(execTimestamp);
        resultRequestObject.setExecStopTimestamp(execStopTimestamp);
        resultRequestObject.setSuccess(isSuccess);
        resultRequestObject.setType(measurementRequestObject.getType());
        Environment environment = applicationContext.getEnvironment();
        String apiKey = environment.getProperty("probe.api.key");
        URI uri = UriComponentsBuilder.fromHttpUrl(HTTP_PREFIX + connectorIpPort + CONNECTOR_RESULTS_ENDPOINT).build().toUri();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("api_key", apiKey);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        if (isArbitrary) {
            params.add("file", new ByteArrayResource(data) {
                @Override
                public String getFilename() {
                    return execTimestamp + "_" + measurementRequestObject.getMeasurementId() + "_" + measurementRequestObject.getProbeId() + ".zip";
                }
            });
        } else {
            params.add("file", new ByteArrayResource(data) {
                @Override
                public String getFilename() {
                    return execTimestamp + "_" + measurementRequestObject.getMeasurementId() + "_" + measurementRequestObject.getProbeId();
                }
            });
        }
        params.add("resultRequestObject", resultRequestObject);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        try {
            restTemplate.postForEntity(uri, requestEntity, String.class);
        } catch (Exception e) {
            log.error("Sending results failed");
            //Failed to send results to connector, error logged
            //Send READY status to connector
            ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean probeStatusSingletonBean = applicationContext.getBean(ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean.class);
            probeStatusSingletonBean.setConnected();
            updateProbeStatus(measurementRequestObject.getProbeId(), ProbeStatus.CONNECTED, connectorIpPort);
            return false;
        }
        //Send READY status to connector
        ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean probeStatusSingletonBean = applicationContext.getBean(ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean.class);
        probeStatusSingletonBean.setConnected();
        updateProbeStatus(measurementRequestObject.getProbeId(), ProbeStatus.CONNECTED, connectorIpPort);
        return true;
    }

    public static void sendMetricsDataToConnector(MetricRequestObject metricRequestObject, String connectorIpPort) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        String apiKey = environment.getProperty("probe.api.key");
        URI uri = UriComponentsBuilder.fromHttpUrl(HTTP_PREFIX + connectorIpPort + CONNECTOR_METRICS_ENDPOINT).build().toUri();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("api_key", apiKey);
        HttpEntity<MetricRequestObject> request = new HttpEntity<>(metricRequestObject, httpHeaders);
        try {
            restTemplate.postForObject(uri, request, String.class);
        } catch (Exception e) {
            log.error("Sending metrics failed");
        }
    }

    public static void updateProbeStatus(String probeId, Constants.ProbeStatus status, String connectorIpPort) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        String apiKey = environment.getProperty("probe.api.key");
        URI uri = UriComponentsBuilder.fromHttpUrl(HTTP_PREFIX + connectorIpPort + CONNECTOR_STATUS_ENDPOINT).build().toUri();
        RestTemplate restTemplate = new RestTemplate();
        ProbeStatusRequestObject probeStatusRequestObject = new ProbeStatusRequestObject(probeId, status);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("api_key", apiKey);
        HttpEntity<ProbeStatusRequestObject> request = new HttpEntity<>(probeStatusRequestObject, httpHeaders);
        try {
            restTemplate.postForObject(uri, request, String.class);
        } catch (Exception e) {
            log.error("Updating probe status failed");
        }
    }

    public static String executeProcessAndReturnOutput(List<String> commands, long timeoutSeconds) throws CustomProcessExecutionException {
        String returnString;
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        try {
            String userHomeDirectory = System.getProperty("user.home");
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(userHomeDirectory));
            builder.command(commands);
            Process process = builder.start();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            BufferedReader errorReader
                    = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
            if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                //Timed out
                process.destroyForcibly();
                throw new Exception();
            } else {
                returnString = output.toString();
                return returnString;
            }
        } catch (Exception exception) {
            log.error("Execution failed");
            log.error("Standard Output: " + output);
            log.error("Error Output:" + errorOutput);
            throw new CustomProcessExecutionException("Process execution failed");
        }
    }

    public static void initiateAppShutdown(ApplicationContext applicationContext, int returnCode) {
        log.error("Shutting down application");
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();
        SpringApplication.exit(applicationContext, () -> returnCode);
    }
}
