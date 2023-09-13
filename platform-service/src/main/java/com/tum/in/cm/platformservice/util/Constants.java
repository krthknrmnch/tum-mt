package com.tum.in.cm.platformservice.util;

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

    public enum MeasurementStatus {
        SCHEDULED,
        SCHEDULING_FAILED,
        STOPPED,
        COMPLETED
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
    public static final String MEASUREMENT_NOT_FOUND_MSG = "Measurement not found";
    public static final String PROBE_NOT_FOUND_MSG = "Probe not found";
    public static final String RESULT_NOT_FOUND_MSG = "Result not found";
    public static final String METRICS_NOT_FOUND_MSG = "Metrics not found";
    public static final String USER_NOT_FOUND_MSG = "User not found";
    public static final String LOGIN_FAILED_MSG = "Login failed";
    public static final String REGISTRATION_FAILED_MSG = "Registration failed";
    public static final String UPDATE_FAILED_MSG = "Update failed";
    public static final String DELETE_FAILED_MSG = "Delete failed";
    public static final String UNAUTHORIZED_MSG = "Unauthorized";
    public static final String FORBIDDEN_MSG = "Forbidden";
    public static final String SUCCESS_MSG = "Success";
    public static final String USER_ALREADY_EXISTS_MSG = "User already exists";

    //URLs
    public static final String HTTP_PREFIX = "http://";
    public static final String CONNECTOR_MEASUREMENT_ENDPOINT = "/api/measurements";

    //AUTH - REST
    public static final String ROLE_AUTHORIZED_USER = "ROLE_USER";
    public static final String ROLE_AUTHORIZED_ADMIN = "ROLE_ADMIN";

    //AUTH - JWT
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_CLAIM_TYPE = "typ";
    public static final String TOKEN_CLAIM_ROLE = "role";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "TUM_CM_NWP";
    public static final Integer TOKEN_EXPIRY_MS = 21600000;

    //AUTH - Encryption
    public static final int PBKDF2_SALT_LENGTH = 64;
    public static final int PBKDF2_ITERATIONS = 200000;
}
