package com.tum.in.cm.connectorservice.repository.primary;

import com.tum.in.cm.connectorservice.model.connector.Connector;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Data repository class used for database access on connectors collection
 */
@Repository
public interface ConnectorRepository extends MongoRepository<Connector, String> {
    Connector findByIpPort(@Param("ipPort") String ipPort);
}
