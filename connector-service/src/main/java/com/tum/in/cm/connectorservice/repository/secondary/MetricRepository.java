package com.tum.in.cm.connectorservice.repository.secondary;

import com.tum.in.cm.connectorservice.model.metric.Metric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Data repository class used for database access on metrics collection
 */
@Repository
public interface MetricRepository extends MongoRepository<Metric, String> {
}
