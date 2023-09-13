package com.tum.in.cm.platformservice.web.rest.controller;

import com.tum.in.cm.platformservice.model.probe.Probe;
import com.tum.in.cm.platformservice.service.ProbeService;
import com.tum.in.cm.platformservice.util.Constants;
import com.tum.in.cm.platformservice.web.rest.dto.request.ProbeRequestObject;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProbeControllerApiTest extends AbstractControllerApiTest {
    private final String endpoint = "/api/probes";
    private final String email = "a@abc.com";

    @Autowired
    private ProbeService probeService;

    @Override
    public void localSetup() {
    }

    @AfterEach
    public void breakdown() {
        probeService.deleteAll();
    }

    @Test
    public void testGETbyId_OK() {
        Probe probe = new Probe();
        Probe insertedProbe = probeService.insert(probe);
        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.GET, endpoint + "/" + insertedProbe.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
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

    @Test
    public void testGET_Multiple_OK() {
        Probe probe1 = new Probe();
        Probe probe2 = new Probe();
        probeService.insert(probe1);
        probeService.insert(probe2);
        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.GET, endpoint)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("count", equalTo(2))
                .and()
                .assertThat()
                .body("currentPage", equalTo(0))
                .and()
                .assertThat()
                .body("totalPages", equalTo(1));

    }

    @Test
    public void testGET_Country_Filter_OK() {
        Probe probe1 = new Probe();
        probe1.setCountry("DE");
        Probe probe2 = new Probe();
        probeService.insert(probe1);
        probeService.insert(probe2);
        given()
                .header("Authorization", this.jwt)
                .and()
                .param("country", "DE")
                .when()
                .request(Method.GET, endpoint)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("count", equalTo(1));
    }

    @Test
    public void testGET_Status_Filter_OK() {
        Probe probe1 = new Probe();
        probe1.setStatus(Constants.ProbeStatus.CONNECTED);
        Probe probe2 = new Probe();
        probeService.insert(probe1);
        probeService.insert(probe2);
        given()
                .header("Authorization", this.jwt)
                .and()
                .param("status", "CONNECTED")
                .when()
                .request(Method.GET, endpoint)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("count", equalTo(1));
    }

    @Test
    public void testGET_Self_OK() {
        Probe userProbe = new Probe();
        userProbe.setUserEmail(email);
        probeService.insert(userProbe);
        Probe differentProbe = new Probe();
        differentProbe.setUserEmail("different_email");
        probeService.insert(differentProbe);

        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.GET, endpoint + "/my")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("count", equalTo(1));
    }

    @Test
    public void testPOST_OK() {
        ProbeRequestObject probeRequestObject = new ProbeRequestObject(email, "testCountry", Constants.Region.EU, "testDescription");
        with()
                .body(probeRequestObject)
                .contentType(ContentType.JSON)
                .given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.POST, endpoint)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.GET, endpoint + "/my")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("count", equalTo(1));
    }
}
