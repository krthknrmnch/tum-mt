package com.tum.in.cm.connectorservice.component;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.service.ProbeService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.messaging.util.matcher.MessageMatcher;
import org.springframework.security.messaging.util.matcher.SimpMessageTypeMatcher;
import org.springframework.stereotype.Component;

/**
 * This interceptor intercepts incoming socket connections during the connect phase
 */
@Component
public class ConnectChannelInterceptor implements ChannelInterceptor {
    private final ProbeService probeService;
    private final MessageMatcher<Object> matcher = new SimpMessageTypeMatcher(SimpMessageType.CONNECT);

    public ConnectChannelInterceptor(ProbeService probeService) {
        this.probeService = probeService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        if (!this.matcher.matches(message)) {
            return message;
        }
        String probeId = SimpMessageHeaderAccessor.getFirstNativeHeader("probe_id", message.getHeaders());
        String apiKey = SimpMessageHeaderAccessor.getFirstNativeHeader("api_key", message.getHeaders());
        try {
            Probe probe = probeService.findByProbeId(probeId);
            if (!probe.getApiKey().equals(apiKey)) {
                throw new AccessDeniedException("Access Denied");
            }
        } catch (CustomNotFoundException e) {
            throw new AccessDeniedException("Access Denied");
        }
        return message;
    }
}
