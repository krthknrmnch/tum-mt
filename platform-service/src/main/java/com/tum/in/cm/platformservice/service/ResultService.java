package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.result.Result;
import com.tum.in.cm.platformservice.repository.secondary.ResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.RESULT_NOT_FOUND_MSG;

/**
 * Result service used for fetching results from the database.
 * Since results are linked by measurement ids, we use the latter to fetch the results.
 */
@Service
@Slf4j
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    public List<Result> findAllByMeasurementId(String measurementId) throws CustomNotFoundException {
        List<Result> resultList = resultRepository.findAllByMeasurementIdOrderByTimestampAsc(measurementId);
        if (resultList.isEmpty()) {
            throw new CustomNotFoundException(RESULT_NOT_FOUND_MSG);
        }
        return resultList;
    }

    //Used in tests
    public void deleteAll() {
        resultRepository.deleteAll();
        log.info("Deleted All Results");
    }
}
