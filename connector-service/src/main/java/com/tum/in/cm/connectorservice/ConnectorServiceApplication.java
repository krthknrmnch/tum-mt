package com.tum.in.cm.connectorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO: Future - Define an AuthenticationManager bean to remove the UserDetailsServiceAutoConfiguration exclusion
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableScheduling
public class ConnectorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConnectorServiceApplication.class, args);
    }
}
