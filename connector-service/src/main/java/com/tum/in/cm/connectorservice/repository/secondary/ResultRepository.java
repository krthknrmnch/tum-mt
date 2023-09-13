package com.tum.in.cm.connectorservice.repository.secondary;

import com.tum.in.cm.connectorservice.model.result.Result;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Data repository class used for database access on results collection
 */
@Repository
public interface ResultRepository extends MongoRepository<Result, String> {
}
