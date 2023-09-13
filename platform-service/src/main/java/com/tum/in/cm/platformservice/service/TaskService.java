package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.repository.primary.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Task service containing business logic for managing tasks.
 */
@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public int countByMeasurementId(String measurementId) {
        return taskRepository.countByMeasurementId(measurementId);
    }

    public void deleteAllByMeasurementId(String measurementId) {
        taskRepository.deleteAllByMeasurementId(measurementId);
    }

    public int countByScheduledStartTimestampBetweenAndProbeId(long timestamp1, long timestamp2, String probeId) {
        return taskRepository.countByScheduledStartTimestampBetweenAndProbeId(timestamp1, timestamp2, probeId);
    }

    public int countByScheduledStopTimestampBetweenAndProbeId(long timestamp1, long timestamp2, String probeId) {
        return taskRepository.countByScheduledStopTimestampBetweenAndProbeId(timestamp1, timestamp2, probeId);
    }
}
