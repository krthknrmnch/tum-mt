package com.tum.in.cm.platformservice.web.rest.controller;

import com.tum.in.cm.platformservice.service.ResultService;
import io.restassured.http.Method;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ResultControllerApiTest extends AbstractControllerApiTest {
    private final String endpoint = "/api/results";

    @Autowired
    private ResultService resultService;

    @Override
    public void localSetup() {
    }

    @AfterEach
    public void breakdown() {
        resultService.deleteAll();
    }

    @Test
    public void testGETbyId_NOT_FOUND() {
        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.GET, endpoint + "/non_existent_id")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
