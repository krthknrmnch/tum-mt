package com.tum.in.cm.platformservice.web.rest.controller;

import com.tum.in.cm.platformservice.service.UserService;
import io.restassured.http.Method;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerApiTest extends AbstractControllerApiTest {
    private final String endpoint = "/api/users";
    private final String email = "a@abc.com";
    private final String password = "pass";

    @Autowired
    private UserService userService;

    @Override
    public void localSetup() {
    }

    @AfterEach
    public void breakdown() {
        userService.deleteAll();
    }

    @Test
    public void testPUT_OK() {
        given()
                .header("Email", email)
                .header("Current-Password", password)
                .header("New-Password", password)
                .when()
                .request(Method.PUT, endpoint)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testDELETE_OK() {
        given()
                .header("Email", email)
                .header("Password", password)
                .when()
                .request(Method.DELETE, endpoint)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }
}
