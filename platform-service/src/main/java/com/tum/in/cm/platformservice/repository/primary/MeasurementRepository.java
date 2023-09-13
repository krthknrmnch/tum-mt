package com.tum.in.cm.platformservice.repository.primary;

import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.util.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data repository class used for database access on measurements collection
 */
@Repository
public interface MeasurementRepository extends MongoRepository<Measurement, String> {
    Page<Measurement> findByTypeAndUserEmailOrderByStatusAsc(@Param("type") Constants.MeasurementType type, @Param("userEmail") String userEmail, @Param("pageable") Pageable pageable);

    Page<Measurement> findByTypeOrderByStatusAsc(@Param("type") Constants.MeasurementType type, @Param("pageable") Pageable pageable);

    Page<Measurement> findByUserEmailOrderByStatusAsc(@Param("userEmail") String userEmail, @Param("pageable") Pageable pageable);

    List<Measurement> findByUserEmail(@Param("userEmail") String userEmail);

    List<Measurement> findAllByStatus(@Param("status") Constants.MeasurementStatus status);
}
