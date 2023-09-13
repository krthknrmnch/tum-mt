package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.metric.Metric;
import com.tum.in.cm.platformservice.repository.secondary.MetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.METRICS_NOT_FOUND_MSG;

/**
 * Metric service used for fetching metrics from the database.
 */
@Service
@Slf4j
public class MetricService {
    @Autowired
    private MetricRepository metricRepository;

    public List<Metric> findAllByProbeId(String probeId) throws CustomNotFoundException {
        List<Metric> metricList = metricRepository.findAllByProbeIdOrderByTimestampAsc(probeId);
        if (metricList.isEmpty()) {
            throw new CustomNotFoundException(METRICS_NOT_FOUND_MSG);
        }
        return metricList;
    }

    public List<Metric> findAllByProbeIdAndTimestampIsBetween(String probeId, long timestamp1, long timestamp2) {
        return metricRepository.findAllByProbeIdAndTimestampIsBetween(probeId, timestamp1, timestamp2);
    }

    //Used in tests
    public void deleteAll() {
        metricRepository.deleteAll();
        log.info("Deleted All Metrics");
    }
}
