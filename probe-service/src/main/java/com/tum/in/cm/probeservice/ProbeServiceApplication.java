package com.tum.in.cm.probeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProbeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProbeServiceApplication.class, args);
    }
}
