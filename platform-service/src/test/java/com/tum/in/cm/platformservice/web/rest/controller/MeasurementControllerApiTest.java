package com.tum.in.cm.platformservice.web.rest.controller;

import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.model.measurement.ProbeSpecification;
import com.tum.in.cm.platformservice.model.measurement.RepeatSpecification;
import com.tum.in.cm.platformservice.model.measurement.specification.ArbitraryMeasurementSpecification;
import com.tum.in.cm.platformservice.model.measurement.specification.TracerouteMeasurementSpecification;
import com.tum.in.cm.platformservice.model.probe.Probe;
import com.tum.in.cm.platformservice.service.MeasurementService;
import com.tum.in.cm.platformservice.service.ProbeService;
import com.tum.in.cm.platformservice.util.Constants;
import com.tum.in.cm.platformservice.web.rest.dto.request.MeasurementRequestObject;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Hashtable;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MeasurementControllerApiTest extends AbstractControllerApiTest {
    private final String endpoint = "/api/measurements";
    private final String email = "a@abc.com";

    @Autowired
    private MeasurementService measurementService;
    @Autowired
    private ProbeService probeService;

    @Override
    public void localSetup() {
    }

    @AfterEach
    public void breakdown() {
        measurementService.deleteAll();
        probeService.deleteAll();
    }

    @Test
    public void testGETbyId_OK() {
        Measurement measurement = new Measurement();
        measurement.setDescription("test");
        Measurement resultMeasurement = measurementService.insert(measurement);
        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.GET, endpoint + "/" + resultMeasurement.getId())
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
        Measurement arbitraryMeasurement = new Measurement();
        arbitraryMeasurement.setType(Constants.MeasurementType.ARBITRARY);
        measurementService.insert(arbitraryMeasurement);
        Measurement predefinedMeasurement = new Measurement();
        predefinedMeasurement.setType(Constants.MeasurementType.PING);
        measurementService.insert(predefinedMeasurement);

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
    public void testGET_Type_Filter_OK() {
        Measurement arbitraryMeasurement = new Measurement();
        arbitraryMeasurement.setType(Constants.MeasurementType.ARBITRARY);
        measurementService.insert(arbitraryMeasurement);
        Measurement predefinedMeasurement = new Measurement();
        predefinedMeasurement.setType(Constants.MeasurementType.PING);
        measurementService.insert(predefinedMeasurement);

        given()
                .header("Authorization", this.jwt)
                .and()
                .param("type", "PING")
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
        Measurement arbitraryMeasurement = new Measurement();
        arbitraryMeasurement.setType(Constants.MeasurementType.ARBITRARY);
        arbitraryMeasurement.setUserEmail("different_email");
        measurementService.insert(arbitraryMeasurement);
        Measurement predefinedMeasurement = new Measurement();
        predefinedMeasurement.setType(Constants.MeasurementType.PING);
        predefinedMeasurement.setUserEmail(email);
        measurementService.insert(predefinedMeasurement);

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
    public void testSTOPbyId_OK() {
        Measurement measurement = new Measurement();
        measurement.setType(Constants.MeasurementType.ARBITRARY);
        measurement.setUserEmail(email);
        Measurement resultMeasurement = measurementService.insert(measurement);
        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.DELETE, endpoint + "/" + resultMeasurement.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testSTOPbyId_NOT_FOUND() {
        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.DELETE, endpoint + "/non_existent_id")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void testSTOPbyId_UNAUTHORIZED() {
        Measurement measurement = new Measurement();
        measurement.setType(Constants.MeasurementType.ARBITRARY);
        measurement.setUserEmail("different_email@abc.com");
        Measurement resultMeasurement = measurementService.insert(measurement);
        given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.DELETE, endpoint + "/" + resultMeasurement.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void testPOST_Arbitrary_OK() {
        ArrayList<String> probeList = new ArrayList<>();
        Probe probe = new Probe();
        Probe insertedProbe = probeService.insert(probe);
        probeList.add(insertedProbe.getId());
        ProbeSpecification probeSpecification = new ProbeSpecification(probeList);
        RepeatSpecification repeatSpecification = new RepeatSpecification(0, 180);
        ArbitraryMeasurementSpecification arbitraryMeasurementSpecification = new ArbitraryMeasurementSpecification();
        arbitraryMeasurementSpecification.setDurationInMinutes(60);
        arbitraryMeasurementSpecification.setContainerImagePath("test");
        arbitraryMeasurementSpecification.setContainerEntrypointString("test");
        arbitraryMeasurementSpecification.setOutputPath("test");
        arbitraryMeasurementSpecification.setCmdInputStrings(new ArrayList<>());
        arbitraryMeasurementSpecification.setEnvVars(new Hashtable<>());
        MeasurementRequestObject arbitraryMeasurementRequestObject = new MeasurementRequestObject(
                Constants.MeasurementType.ARBITRARY,
                arbitraryMeasurementSpecification,
                "testDescription",
                repeatSpecification,
                probeSpecification
        );
        with()
                .body(arbitraryMeasurementRequestObject)
                .contentType(ContentType.JSON)
                .given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.POST, endpoint)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", this.jwt)
                .and()
                .param("type", "ARBITRARY")
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
    public void testPOST_Predefined_OK() {
        ArrayList<String> probeList = new ArrayList<>();
        Probe probe = new Probe();
        Probe insertedProbe = probeService.insert(probe);
        probeList.add(insertedProbe.getId());
        ProbeSpecification probeSpecification = new ProbeSpecification(probeList);
        RepeatSpecification repeatSpecification = new RepeatSpecification(0, 180);
        TracerouteMeasurementSpecification tracerouteMeasurementSpecification = new TracerouteMeasurementSpecification();
        tracerouteMeasurementSpecification.setTarget("test");
        tracerouteMeasurementSpecification.setMethod(Constants.TracerouteMethod.UDP);
        MeasurementRequestObject predefinedMeasurementRequestObject = new MeasurementRequestObject(
                Constants.MeasurementType.TRACEROUTE,
                tracerouteMeasurementSpecification,
                "testDescription",
                repeatSpecification,
                probeSpecification
        );
        with()
                .body(predefinedMeasurementRequestObject)
                .contentType(ContentType.JSON)
                .given()
                .header("Authorization", this.jwt)
                .when()
                .request(Method.POST, endpoint)
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", this.jwt)
                .and()
                .param("type", "TRACEROUTE")
                .when()
                .request(Method.GET, endpoint)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("count", equalTo(1));
    }
}
