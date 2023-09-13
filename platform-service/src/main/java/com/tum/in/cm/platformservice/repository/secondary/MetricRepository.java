package com.tum.in.cm.platformservice.repository.secondary;

import com.tum.in.cm.platformservice.model.metric.Metric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data repository class used for database access on metrics collection
 */
@Repository
public interface MetricRepository extends MongoRepository<Metric, String> {
    List<Metric> findAllByProbeIdOrderByTimestampAsc(@Param("probeId") String probeId);

    List<Metric> findAllByProbeIdAndTimestampIsBetween(@Param("probeId") String probeId, @Param("timestamp1") long timestamp1, @Param("timestamp2") long timestamp2);
}
