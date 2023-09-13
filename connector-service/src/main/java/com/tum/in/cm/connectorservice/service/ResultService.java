package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.model.result.Result;
import com.tum.in.cm.connectorservice.repository.secondary.ResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Result service used for storing results in the database.
 */
@Service
@Slf4j
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    public Result insert(Result result) {
        resultRepository.insert(result);
        log.info("Inserted Result with Id: " + result.getId());
        return result;
    }

    //Used in tests
    public void deleteAll() {
        resultRepository.deleteAll();
        log.info("Deleted All Results");
    }
}
