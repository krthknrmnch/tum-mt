package com.tum.in.cm.connectorservice.repository.primary;

import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.util.Constants;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data repository class used for database access on probes collection
 */
@Repository
public interface ProbeRepository extends MongoRepository<Probe, String> {
    List<Probe> findAllByConnectorIpPortAndStatus(@Param("connectorIpPort") String connectorIpPort, @Param("status") Constants.ProbeStatus status);

    List<Probe> findAllByConnectorIpPort(@Param("connectorIpPort") String connectorIpPort);
}
