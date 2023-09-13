package com.tum.in.cm.probeservice.service;

import com.tum.in.cm.probeservice.component.ApplicationContextProvider;
import com.tum.in.cm.probeservice.config.ProbeStatusSingletonBeanConfig;
import com.tum.in.cm.probeservice.exception.CustomProcessExecutionException;
import com.tum.in.cm.probeservice.util.Constants;
import com.tum.in.cm.probeservice.web.rest.dto.MetricRequestObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

import static com.tum.in.cm.probeservice.util.Utils.executeProcessAndReturnOutput;
import static com.tum.in.cm.probeservice.util.Utils.sendMetricsDataToConnector;

@Service
@Slf4j
public class MetricService {
    @Autowired
    private Environment environment;

    /**
     * This is a scheduled method that runs every second.
     * It fetches the dish metrics data exposed via the Dishy API.
     * It forwards this data to the connector for metrics persistence.
     */
    @Scheduled(fixedDelay = 1000)
    public void runMetricsJobAndSendData() {
        String connectorIpPort = environment.getProperty("connector.service.ip.port");
        boolean metricsCollectionEnabled = Boolean.parseBoolean(environment.getProperty("starlink.dish.metrics.collection"));
        boolean locationCollectionEnabled = Boolean.parseBoolean(environment.getProperty("starlink.dish.location.collection"));
        String probeId = environment.getProperty("probe.id");
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean probeStatusSingletonBean = applicationContext.getBean(ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean.class);
        Constants.ProbeStatus probeStatus = probeStatusSingletonBean.getProbeStatus();
        if (metricsCollectionEnabled && probeStatus != null && probeStatus.equals(Constants.ProbeStatus.RUNNING)) {
            log.info("Fetching metrics data");
            try {
                long timestamp = Instant.now().getEpochSecond();
                String statusData = "";
                String locationData = "";
                statusData = executeProcessAndReturnOutput(Arrays.asList(
                        "grpcurl",
                        "-plaintext",
                        "-d",
                        "{\"getStatus\":{}}",
                        "192.168.100.1:9200",
                        "SpaceX.API.Device.Device/Handle"
                ), 60);
                if (locationCollectionEnabled) {
                    locationData = executeProcessAndReturnOutput(Arrays.asList(
                            "grpcurl",
                            "-plaintext",
                            "-d",
                            "{\"getLocation\":{}}",
                            "192.168.100.1:9200",
                            "SpaceX.API.Device.Device/Handle"
                    ), 60);
                }
                MetricRequestObject metricRequestObject = new MetricRequestObject();
                metricRequestObject.setProbeId(probeId);
                metricRequestObject.setTimestamp(timestamp);
                if (statusData.isBlank() || statusData.startsWith("Failed") || statusData.contains("grpcurl: not found")) {
                    throw new CustomProcessExecutionException("Failed to connect to the Starlink gRPC endpoint - 192.168.100.1");
                }
                if (locationData.startsWith("ERROR") || locationData.contains("not enabled")) {
                    throw new CustomProcessExecutionException("Failed to fetch location data from the Starlink gRPC endpoint - 192.168.100.1 - Please enable data collection");
                }
                metricRequestObject.setStatusData(statusData.getBytes());
                metricRequestObject.setLocationData(locationData.getBytes());
                log.info("Sending metrics data to connector");
                sendMetricsDataToConnector(metricRequestObject, connectorIpPort);
            } catch (CustomProcessExecutionException e) {
                log.error("Metrics fetch failed");
            }
        }
    }
}
