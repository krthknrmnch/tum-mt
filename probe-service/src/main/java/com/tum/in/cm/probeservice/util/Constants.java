package com.tum.in.cm.probeservice.util;

public final class Constants {
    private Constants() {
    }

    /**
     * ENUMS
     */
    public enum MeasurementType {
        ARBITRARY,
        DNS,
        HTTP,
        PING,
        TRACEROUTE,
        PARIS_TRACEROUTE
    }

    public enum ProbeStatus {
        CONNECTED,
        RUNNING
    }

    public enum HttpMethod {
        GET,
        POST
    }

    public enum TracerouteMethod {
        UDP,
        ICMP,
        TCP
    }

    /**
     * STRINGS
     */
    //URLs
    public static final String HTTP_PREFIX = "http://";
    public static final String WS_PREFIX = "ws://";
    public static final String CONNECTOR_SOCKET_ENDPOINT = "/socket";
    public static final String CONNECTOR_MEASUREMENT_TOPIC = "/topic/measurement/";
    public static final String CONNECTOR_RESULTS_ENDPOINT = "/api/results";
    public static final String CONNECTOR_METRICS_ENDPOINT = "/api/metrics";
    public static final String CONNECTOR_STATUS_ENDPOINT = "/api/status";
    public static final String CONNECTOR_REGISTRATION_ENDPOINT = "/api/register";
}
