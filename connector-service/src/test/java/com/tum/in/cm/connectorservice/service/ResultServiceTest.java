package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.model.result.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void testInsert() {
        Result result = new Result();
        result.setProbeId("Test");
        Result resultResult = resultService.insert(result);
        assertThat(resultResult.getProbeId()).isEqualTo("Test");
    }
}
