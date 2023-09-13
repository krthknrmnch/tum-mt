package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.model.connector.Connector;
import com.tum.in.cm.platformservice.repository.primary.ConnectorRepository;
import com.tum.in.cm.platformservice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Connector service, containing logic for managing connectors and their details.
 */
@Service
@Slf4j
public class ConnectorService {
    @Autowired
    private ConnectorRepository connectorRepository;

    public Connector findByIpPort(String ipPort) {
        return connectorRepository.findByIpPort(ipPort).orElse(null);
    }

    public Connector findOneByRegion(Constants.Region region) {
        return connectorRepository.findFirstByRegion(region).orElse(null);
    }

    public Connector findOneByRegionNotLike(Constants.Region region) {
        return connectorRepository.findFirstByRegionNot(region).orElse(null);
    }
}
