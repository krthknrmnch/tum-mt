package com.tum.in.cm.probeservice.component;

import com.tum.in.cm.probeservice.exception.CustomProcessExecutionException;
import com.tum.in.cm.probeservice.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

import static com.tum.in.cm.probeservice.util.Constants.CONNECTOR_SOCKET_ENDPOINT;
import static com.tum.in.cm.probeservice.util.Constants.WS_PREFIX;
import static com.tum.in.cm.probeservice.util.Utils.executeProcessAndReturnOutput;

/**
 * This class executes startup tasks once Spring context has been initialized.
 */
@Component
@Slf4j
public class StartupTask implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Running post-startup steps");
        String connectorIpPort = environment.getProperty("connector.service.ip.port");
        String probeId = environment.getProperty("probe.id");
        String apiKey = environment.getProperty("probe.api.key");
        checkNetworkInterfaceProperties();
        if (connectorIpPort == null || connectorIpPort.isBlank()) {
            log.error("Connector IP not set");
            Utils.initiateAppShutdown(ApplicationContextProvider.getApplicationContext(), 0);
        } else {
            //Start the docker daemon in the sysbox system container
            log.info("Starting docker daemon");
            try {
                startDockerDaemon();
            } catch (CustomProcessExecutionException e) {
                log.error("Starting docker daemon failed");
                Utils.initiateAppShutdown(ApplicationContextProvider.getApplicationContext(), 0);
            }
            log.info("CONNECTOR_IP_PORT is: " + connectorIpPort);
            log.info("Initiating websocket connection to connector");
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            StompSessionHandler stompSessionHandler = new CustomStompSessionHandler(environment);
            URI uri = UriComponentsBuilder.fromUriString(WS_PREFIX + connectorIpPort + CONNECTOR_SOCKET_ENDPOINT).build().toUri();
            WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
            httpHeaders.add("probe_id", probeId);
            httpHeaders.add("api_key", apiKey);
            StompHeaders connectHeaders = new StompHeaders();
            connectHeaders.add("probe_id", probeId);
            connectHeaders.add("api_key", apiKey);
            stompClient.connectAsync(uri.toString(), httpHeaders, connectHeaders, stompSessionHandler);
        }
        log.info("Post-startup steps complete");
    }

    public void startDockerDaemon() throws CustomProcessExecutionException {
        executeProcessAndReturnOutput(Arrays.asList("mkdir", "-p", "/run/openrc/exclusive"), 60);
        executeProcessAndReturnOutput(Arrays.asList("touch", "/run/openrc/softlevel"), 60);
        executeProcessAndReturnOutput(Arrays.asList("service", "docker", "restart"), 120);
    }

    public void checkNetworkInterfaceProperties() {
        boolean isWiredInterfaceActive = Boolean.parseBoolean(environment.getProperty("is.wired.interface.active"));
        boolean isWifiInterfaceActive = Boolean.parseBoolean(environment.getProperty("is.wifi.interface.active"));
        boolean isCellularInterfaceActive = Boolean.parseBoolean(environment.getProperty("is.cellular.interface.active"));
        String starlinkActiveInterface = environment.getProperty("starlink.active.interface");
        if (isWiredInterfaceActive || isWifiInterfaceActive || isCellularInterfaceActive) {
            try {
                switch (Objects.requireNonNull(starlinkActiveInterface)) {
                    case "wired", "wifi", "cellular", "none" -> {
                        return;
                    }
                }
                log.error("Please set property starlink.active.interface to one of wired, wifi, cellular, or none");
                Utils.initiateAppShutdown(ApplicationContextProvider.getApplicationContext(), 0);
            } catch (NullPointerException e) {
                log.error("Property starlink.active.interface not set");
                Utils.initiateAppShutdown(ApplicationContextProvider.getApplicationContext(), 0);
            }
        } else {
            log.error("Please set one of the active network interface properties to true");
            Utils.initiateAppShutdown(ApplicationContextProvider.getApplicationContext(), 0);
        }
    }
}
