package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.measurement.Measurement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.tum.in.cm.platformservice.util.Constants.MEASUREMENT_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class MeasurementServiceTest {
    @Autowired
    private MeasurementService measurementService;

    @BeforeEach
    public void setup() {
        measurementService.deleteAll();
    }

    @AfterEach
    public void teardown() {
        measurementService.deleteAll();
    }

    //Base Test
    @Test
    public void testInsertMeasurement() {
        Measurement measurement = new Measurement();
        measurement.setDescription("test");
        Measurement resultMeasurement = measurementService.insert(measurement);
        assertThat(resultMeasurement.getDescription()).isEqualTo("test");
    }

    //Base Test
    @Test
    public void testNotFoundException() {
        assertThatThrownBy(() -> {
            Measurement measurement = measurementService.findByMeasurementId("Non_Existent_Id");
        }).isInstanceOf(CustomNotFoundException.class)
                .hasMessageContaining(MEASUREMENT_NOT_FOUND_MSG);
    }
}
