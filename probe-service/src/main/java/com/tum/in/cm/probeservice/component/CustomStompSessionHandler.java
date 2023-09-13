package com.tum.in.cm.probeservice.component;

import com.tum.in.cm.probeservice.config.ProbeStatusSingletonBeanConfig;
import com.tum.in.cm.probeservice.service.MeasurementService;
import com.tum.in.cm.probeservice.util.Utils;
import com.tum.in.cm.probeservice.web.rest.dto.ProbeRegistrationRequestObject;
import com.tum.in.cm.probeservice.web.ws.dto.InternalMeasurementRequestObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.*;

import static com.tum.in.cm.probeservice.util.Constants.*;
import static com.tum.in.cm.probeservice.util.Constants.ProbeStatus.RUNNING;
import static com.tum.in.cm.probeservice.util.Utils.updateProbeStatus;

@Slf4j
@AllArgsConstructor
public class CustomStompSessionHandler extends StompSessionHandlerAdapter {
    private Environment environment;
    private StompSession stompSession;

    public CustomStompSessionHandler(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("New session established: " + session.getSessionId());
        this.stompSession = session;
        //Subscribe
        log.info("Subscribing to new measurement tasks");
        String probeId = environment.getProperty("probe.id");
        boolean isWiredInterfaceActive = Boolean.parseBoolean(environment.getProperty("is.wired.interface.active"));
        boolean isWifiInterfaceActive = Boolean.parseBoolean(environment.getProperty("is.wifi.interface.active"));
        boolean isCellularInterfaceActive = Boolean.parseBoolean(environment.getProperty("is.cellular.interface.active"));
        String starlinkActiveInterface = environment.getProperty("starlink.active.interface");
        this.stompSession.subscribe(CONNECTOR_MEASUREMENT_TOPIC + probeId, this);
        //Register
        log.info("Registering probe to connector");
        String connectorIpPort = environment.getProperty("connector.service.ip.port");
        //Fetch machine IP
        String machineIP = "";
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            //Following address or port need not be reachable
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            machineIP = datagramSocket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            log.error("Error trying to fetch machine IP address");
        }
        ProbeRegistrationRequestObject probeRegistrationRequestObject = new ProbeRegistrationRequestObject(
                probeId,
                machineIP,
                connectorIpPort,
                isWiredInterfaceActive,
                isWifiInterfaceActive,
                isCellularInterfaceActive,
                starlinkActiveInterface
        );
        String apiKey = environment.getProperty("probe.api.key");
        URI uri = UriComponentsBuilder.fromHttpUrl(HTTP_PREFIX + connectorIpPort + CONNECTOR_REGISTRATION_ENDPOINT).build().toUri();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("api_key", apiKey);
        HttpEntity<ProbeRegistrationRequestObject> request = new HttpEntity<>(probeRegistrationRequestObject, httpHeaders);
        restTemplate.postForObject(uri, request, String.class);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error(exception.getMessage());
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return InternalMeasurementRequestObject.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload != null) {
            log.info("Received measurement task");
            InternalMeasurementRequestObject measurementRequestObject = (InternalMeasurementRequestObject) payload;
            MeasurementService measurementService = ApplicationContextProvider.getApplicationContext().getBean(MeasurementService.class);
            String connectorIpPort = environment.getProperty("connector.service.ip.port");
            //Send RUNNING status to connector
            ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
            ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean probeStatusSingletonBean = applicationContext.getBean(ProbeStatusSingletonBeanConfig.ProbeStatusSingletonBean.class);
            probeStatusSingletonBean.setRunning();
            updateProbeStatus(measurementRequestObject.getProbeId(), RUNNING, connectorIpPort);
            //Run Measurement
            measurementService.runMeasurement(measurementRequestObject);
        }
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        if (!session.isConnected()) {
            log.error("Websocket connection to connector disconnected");
            Utils.initiateAppShutdown(ApplicationContextProvider.getApplicationContext(), 0);
        }
    }
}
