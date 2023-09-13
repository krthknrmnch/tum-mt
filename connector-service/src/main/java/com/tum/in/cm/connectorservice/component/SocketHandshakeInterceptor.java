package com.tum.in.cm.connectorservice.component;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.service.ProbeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * This interceptor intercepts incoming socket connections before the handshake process
 */
@Component
public class SocketHandshakeInterceptor implements HandshakeInterceptor {
    private final ProbeService probeService;

    public SocketHandshakeInterceptor(ProbeService probeService) {
        this.probeService = probeService;
    }

    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpHeaders httpHeaders = request.getHeaders();
        String probeId = httpHeaders.getFirst("probe_id");
        String apiKey = httpHeaders.getFirst("api_key");
        try {
            Probe probe = probeService.findByProbeId(probeId);
            if (!probe.getApiKey().equals(apiKey)) {
                throw new AccessDeniedException("Access Denied");
            }
        } catch (CustomNotFoundException e) {
            throw new AccessDeniedException("Access Denied");
        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
