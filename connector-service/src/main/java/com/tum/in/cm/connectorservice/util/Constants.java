package com.tum.in.cm.connectorservice.util;

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
        RUNNING,
        DISCONNECTED,
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

    public enum Region {
        AF,
        AS,
        EU,
        OC,
        NA,
        SA
    }

    /**
     * STRINGS
     */
    //MESSAGES
    public static final String SUCCESS_MSG = "Success";
    public static final String PROBE_NOT_FOUND_MSG = "Probe not found";

    //URLs
    public static final String CONNECTOR_MEASUREMENT_TOPIC = "/topic/measurement/";
}
