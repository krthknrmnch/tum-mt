package com.tum.in.cm.platformservice.repository.primary;

import com.tum.in.cm.platformservice.model.task.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Data repository class used for database access on tasks collection
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    int countByMeasurementId(@Param("measurementId") String measurementId);

    void deleteAllByMeasurementId(@Param("measurementId") String measurementId);

    int countByScheduledStartTimestampBetweenAndProbeId(@Param("timestamp1") long timestamp1, @Param("timestamp2") long timestamp2, @Param("probeId") String probeId);

    int countByScheduledStopTimestampBetweenAndProbeId(@Param("timestamp1") long timestamp1, @Param("timestamp2") long timestamp2, @Param("probeId") String probeId);
}
