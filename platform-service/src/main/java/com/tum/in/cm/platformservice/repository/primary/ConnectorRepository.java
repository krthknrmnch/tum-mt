package com.tum.in.cm.platformservice.repository.primary;

import com.tum.in.cm.platformservice.model.connector.Connector;
import com.tum.in.cm.platformservice.util.Constants;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data repository class used for database access on connectors collection
 */
@Repository
public interface ConnectorRepository extends MongoRepository<Connector, String> {
    Optional<Connector> findFirstByRegion(@Param("region") Constants.Region region);

    Optional<Connector> findFirstByRegionNot(@Param("region") Constants.Region region);

    Optional<Connector> findByIpPort(@Param("ipPort") String ipPort);
}
