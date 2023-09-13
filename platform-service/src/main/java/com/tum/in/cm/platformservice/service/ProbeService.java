package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.measurement.ProbeSpecification;
import com.tum.in.cm.platformservice.model.probe.Probe;
import com.tum.in.cm.platformservice.repository.primary.ProbeRepository;
import com.tum.in.cm.platformservice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.PROBE_NOT_FOUND_MSG;

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

    public Page<Probe> listAll(Pageable pageable) {
        return probeRepository.findAll(pageable);
    }

    public Page<Probe> listByCountry(String country, Pageable pageable) {
        return probeRepository.findByCountryOrderByCountryAsc(country, pageable);
    }

    public Page<Probe> listByStatus(Constants.ProbeStatus status, Pageable pageable) {
        return probeRepository.findByStatusOrderByStatusAsc(status, pageable);
    }

    public Page<Probe> listByCountryAndStatus(String country, Constants.ProbeStatus status, Pageable pageable) {
        return probeRepository.findByCountryAndStatusOrderByCountryAsc(country, status, pageable);
    }

    public Page<Probe> listByUserEmail(String userEmail, Pageable pageable) {
        return probeRepository.findByUserEmail(userEmail, pageable);
    }

    public Page<Probe> listByUserEmailAndCountry(String userEmail, String country, Pageable pageable) {
        return probeRepository.findByUserEmailAndCountryOrderByCountryAsc(userEmail, country, pageable);
    }

    public Page<Probe> listByUserEmailAndStatus(String userEmail, Constants.ProbeStatus status, Pageable pageable) {
        return probeRepository.findByUserEmailAndStatusOrderByStatusAsc(userEmail, status, pageable);
    }

    public Page<Probe> listByUserEmailAndCountryAndStatus(String userEmail, String country, Constants.ProbeStatus status, Pageable pageable) {
        return probeRepository.findByUserEmailAndCountryAndStatusOrderByCountryAsc(userEmail, country, status, pageable);
    }

    public Probe insert(Probe probe) {
        probeRepository.insert(probe);
        log.info("Inserted Probe with Id: " + probe.getId());
        return probe;
    }

    public List<String> buildProbeListFromSpecification(ProbeSpecification probeSpecification) {
        List<String> probesList = new ArrayList<>();
        if (!probeSpecification.getProbeIds().isEmpty()) {
            probesList = probeSpecification.getProbeIds();
        }
        return probesList;
    }

    //Used in tests
    public void deleteAll() {
        probeRepository.deleteAll();
        log.info("Deleted All Probes");
    }
}
