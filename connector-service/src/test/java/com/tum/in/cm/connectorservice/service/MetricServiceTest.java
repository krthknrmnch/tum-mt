package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.model.metric.Metric;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void testInsert() {
        Metric metric = new Metric();
        metric.setTimestamp(1L);
        Metric insertedMetric = metricService.insert(metric);
        assertThat(insertedMetric.getTimestamp()).isEqualTo(1L);
    }
}
