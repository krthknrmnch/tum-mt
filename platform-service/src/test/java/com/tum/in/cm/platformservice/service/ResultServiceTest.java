package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.result.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.RESULT_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ResultServiceTest {
    @Autowired
    private ResultService resultService;

    @BeforeEach
    public void setup() {
        resultService.deleteAll();
    }

    @AfterEach
    public void teardown() {
        resultService.deleteAll();
    }

    //Base Test
    @Test
    public void testNotFoundException() {
        assertThatThrownBy(() -> {
            List<Result> result = resultService.findAllByMeasurementId("Non_Existent_Id");
        }).isInstanceOf(CustomNotFoundException.class)
                .hasMessageContaining(RESULT_NOT_FOUND_MSG);
    }
}
