package com.tum.in.cm.platformservice.repository.secondary;

import com.tum.in.cm.platformservice.model.result.Result;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data repository class used for database access on results collection
 */
@Repository
public interface ResultRepository extends MongoRepository<Result, String> {
    List<Result> findAllByMeasurementIdOrderByTimestampAsc(@Param("measurementId") String measurementId);
}
