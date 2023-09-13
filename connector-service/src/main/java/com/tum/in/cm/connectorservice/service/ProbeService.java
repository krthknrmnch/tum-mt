package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.repository.primary.ProbeRepository;
import com.tum.in.cm.connectorservice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tum.in.cm.connectorservice.util.Constants.PROBE_NOT_FOUND_MSG;

/**
 * Probe service, containing logic for managing probes and their details.
 */
@Service
@Slf4j
public class ProbeService {
    @Autowired
    private ProbeRepository probeRepository;

    public Probe findByProbeId(String probeId) throws CustomNotFoundException {
        Probe probe = probeRepository.findById(probeId).orElse(null);
        if (probe == null) {
            throw new CustomNotFoundException(PROBE_NOT_FOUND_MSG);
        }
        return probe;
    }

    public List<Probe> findAllByConnectorIpPortAndStatus(String connectorIpPort, Constants.ProbeStatus status) {
        return probeRepository.findAllByConnectorIpPortAndStatus(connectorIpPort, status);
    }

    public List<Probe> findAllByConnectorIpPort(String connectorIpPort) {
        return probeRepository.findAllByConnectorIpPort(connectorIpPort);
    }

    public void update(Probe updatedProbe) throws CustomNotFoundException {
        Probe probe = this.findByProbeId(updatedProbe.getId());
        updatedProbe.setId(probe.getId());
        probeRepository.save(updatedProbe);
        log.info("Updated Probe with Id: " + updatedProbe.getId());
    }

    //Used in tests
    public void deleteAll() {
        probeRepository.deleteAll();
        log.info("Deleted All Probes");
    }
}
