package com.tum.in.cm.platformservice.web.rest.controller;

import com.tum.in.cm.platformservice.component.security.JwtBuilder;
import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.user.User;
import com.tum.in.cm.platformservice.service.UserService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;

public abstract class AbstractControllerApiTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    protected String jwt;

    @BeforeEach
    public void setup() throws Exception {
        RestAssured.port = port;
        //Add user if it does not exist
        User user;
        try {
            user = userService.findByEmail("a@abc.com");
        } catch (CustomNotFoundException e) {
            String email = "a@abc.com";
            String password = "pass";
            boolean isAdmin = true;
            userService.attemptRegistration(email, password, isAdmin);
        }
        this.jwt = "Bearer " + new JwtBuilder().buildJwtForAdmin("a@abc.com", environment.getProperty("auth.jwt.secret"));
        localSetup();
    }

    public abstract void localSetup();
}
