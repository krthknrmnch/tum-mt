package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.repository.primary.MeasurementRepository;
import com.tum.in.cm.platformservice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.MEASUREMENT_NOT_FOUND_MSG;

/**
 * Measurement service, containing logic for managing measurements and their details.
 */
@Service
@Slf4j
public class MeasurementService {
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private TaskService taskService;

    public Measurement findByMeasurementId(String measurementId) throws CustomNotFoundException {
        Measurement measurement = measurementRepository.findById(measurementId).orElse(null);
        if (measurement == null) {
            throw new CustomNotFoundException(MEASUREMENT_NOT_FOUND_MSG);
        }
        return measurement;
    }

    public Page<Measurement> listAll(Pageable pageable) {
        return measurementRepository.findAll(pageable);
    }

    public Page<Measurement> listByUserEmail(String userEmail, Pageable pageable) {
        return measurementRepository.findByUserEmailOrderByStatusAsc(userEmail, pageable);
    }

    public Page<Measurement> listByType(Constants.MeasurementType type, Pageable pageable) {
        return measurementRepository.findByTypeOrderByStatusAsc(type, pageable);
    }

    public Page<Measurement> listByTypeAndUserEmail(Constants.MeasurementType type, String userEmail, Pageable pageable) {
        return measurementRepository.findByTypeAndUserEmailOrderByStatusAsc(type, userEmail, pageable);
    }

    public List<Measurement> listByUserEmail(String userEmail) {
        return measurementRepository.findByUserEmail(userEmail);
    }

    public List<Measurement> findAllByStatus(Constants.MeasurementStatus status) {
        return measurementRepository.findAllByStatus(status);
    }

    public Measurement insert(Measurement measurement) {
        measurementRepository.insert(measurement);
        log.info("Inserted Measurement with Id: " + measurement.getId());
        return measurement;
    }

    public void update(Measurement updatedMeasurement) throws CustomNotFoundException {
        Measurement measurement = this.findByMeasurementId(updatedMeasurement.getId());
        updatedMeasurement.setId(measurement.getId());
        measurementRepository.save(updatedMeasurement);
        log.info("Updated Measurement with Id: " + updatedMeasurement.getId());
    }

    /**
     * Method stops measurement by setting status to STOPPED
     */
    public void stopMeasurementById(String measurementId) throws CustomNotFoundException {
        Measurement measurement = findByMeasurementId(measurementId);
        measurement.setStatus(Constants.MeasurementStatus.STOPPED);
        this.update(measurement);
        //Now delete any active tasks for this measurement
        taskService.deleteAllByMeasurementId(measurementId);
        log.info("Stopped Measurement with Id: " + measurementId);
    }

    /**
     * Method stops measurement by setting status to STOPPED
     */
    public void stopMeasurement(Measurement measurement) throws CustomNotFoundException {
        measurement.setStatus(Constants.MeasurementStatus.STOPPED);
        this.update(measurement);
        log.info("Stopped Measurement with Id: " + measurement.getId());
    }

    /**
     * This is a scheduled method that runs every 5 minutes.
     * It checks for active measurement tasks and marks measurements as completed when none exist for any specific measurementId.
     */
    @Scheduled(fixedDelay = 300000)
    public void scheduledMeasurementStatusUpdateTask() throws CustomNotFoundException {
        log.info("Scheduled task running for updating status of measurements");
        List<Measurement> activeMeasurements = this.findAllByStatus(Constants.MeasurementStatus.SCHEDULED);
        if (!activeMeasurements.isEmpty()) {
            for (Measurement measurement : activeMeasurements) {
                int count = taskService.countByMeasurementId(measurement.getId());
                if (count == 0) {
                    measurement.setStatus(Constants.MeasurementStatus.COMPLETED);
                    this.update(measurement);
                    log.info("Measurement marked as completed with Id: " + measurement.getId());
                }
            }
        }
    }

    //Used in tests
    public void deleteAll() {
        measurementRepository.deleteAll();
        log.info("Deleted All Measurements");
    }
}
