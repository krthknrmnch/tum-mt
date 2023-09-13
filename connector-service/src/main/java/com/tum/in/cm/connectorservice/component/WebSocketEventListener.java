package com.tum.in.cm.connectorservice.component;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.service.ProbeService;
import com.tum.in.cm.connectorservice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Hashtable;

import static com.tum.in.cm.connectorservice.util.Constants.CONNECTOR_MEASUREMENT_TOPIC;

@Component
@Slf4j
public class WebSocketEventListener {
    @Autowired
    private ProbeService probeService;

    private static final Hashtable<String, String> sessionProbeTable = new Hashtable<>();

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) throws CustomNotFoundException {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        if (headers.getDestination() != null) {
            String probeId = org.apache.commons.lang3.StringUtils.substringAfter(headers.getDestination(), CONNECTOR_MEASUREMENT_TOPIC);
            log.info("Session subscribed from probe with Id: " + probeId);
            sessionProbeTable.put(sessionId, probeId);
            //Update probe status
            Probe probe = probeService.findByProbeId(probeId);
            probe.setStatus(Constants.ProbeStatus.CONNECTED);
            probeService.update(probe);
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) throws CustomNotFoundException {
        log.info("Session disconnected");
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String probeId = sessionProbeTable.get(sessionId);
        //Update probe status
        Probe probe = probeService.findByProbeId(probeId);
        probe.setStatus(Constants.ProbeStatus.DISCONNECTED);
        probeService.update(probe);
        sessionProbeTable.remove(sessionId);
    }
}
