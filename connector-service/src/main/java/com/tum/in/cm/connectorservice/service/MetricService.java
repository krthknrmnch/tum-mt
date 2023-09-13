package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.model.metric.Metric;
import com.tum.in.cm.connectorservice.repository.secondary.MetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Metric service used for storing metrics in the database.
 */
@Service
@Slf4j
public class MetricService {
    @Autowired
    private MetricRepository metricRepository;

    public Metric insert(Metric metric) {
        metricRepository.insert(metric);
        log.info("Inserted Metric with Id: " + metric.getId());
        return metric;
    }

    //Used in tests
    public void deleteAll() {
        metricRepository.deleteAll();
        log.info("Deleted All Metrics");
    }
}
