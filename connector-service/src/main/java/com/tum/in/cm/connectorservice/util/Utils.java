package com.tum.in.cm.connectorservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
public final class Utils {

    private Utils() {
    }

    public static void initiateAppShutdown(ApplicationContext applicationContext, int returnCode) {
        log.info("Shutting down application");
        SpringApplication.exit(applicationContext, () -> returnCode);
    }
}
