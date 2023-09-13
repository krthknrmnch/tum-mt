package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.component.ApplicationContextProvider;
import com.tum.in.cm.connectorservice.model.connector.Connector;
import com.tum.in.cm.connectorservice.repository.primary.ConnectorRepository;
import com.tum.in.cm.connectorservice.util.Constants;
import com.tum.in.cm.connectorservice.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Connector service, containing logic for managing connectors and their details.
 */
@Service
@Slf4j
public class ConnectorService {
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private Environment environment;

    public void saveOrUpdate(Connector updatedConnector) {
        Connector connector = connectorRepository.findByIpPort(updatedConnector.getIpPort());
        if (connector == null) {
            connectorRepository.save(updatedConnector);
        } else {
            updatedConnector.setId(connector.getId());
            connectorRepository.save(updatedConnector);
        }
        log.info("Updated Connector with IpPort: " + updatedConnector.getIpPort());
    }

    /**
     * Following method runs every 3 minutes to update this service instance's health.
     */
    @Scheduled(fixedDelay = 180000)
    public void connectorHealthUpdate() {
        try {
            Connector connector = new Connector();
            connector.setIpPort(environment.getProperty("connector.service.ip.port"));
            connector.setOakestraIpPort(environment.getProperty("connector.service.oakestra.ip.port"));
            connector.setRegion(Constants.Region.valueOf(environment.getProperty("connector.service.region")));
            connector.setUpdateTimestamp(new Date());
            this.saveOrUpdate(connector);
        } catch (IllegalArgumentException e) {
            log.error("Please ensure connector region is set correctly as part of the environment variables.");
            Utils.initiateAppShutdown(ApplicationContextProvider.getApplicationContext(), 0);
        }
    }
}
