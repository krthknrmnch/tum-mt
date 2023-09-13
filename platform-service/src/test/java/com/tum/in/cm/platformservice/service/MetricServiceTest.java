package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.metric.Metric;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.METRICS_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class MetricServiceTest {
    @Autowired
    private MetricService metricService;

    @BeforeEach
    public void setup() {
        metricService.deleteAll();
    }

    @AfterEach
    public void teardown() {
        metricService.deleteAll();
    }

    //Base Test
    @Test
    public void testNotFoundException() {
        assertThatThrownBy(() -> {
            List<Metric> metrics = metricService.findAllByProbeId("Non_Existent_Id");
        }).isInstanceOf(CustomNotFoundException.class)
                .hasMessageContaining(METRICS_NOT_FOUND_MSG);
    }
}
