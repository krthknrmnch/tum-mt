package com.tum.in.cm.platformservice.service;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.probe.Probe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.tum.in.cm.platformservice.util.Constants.PROBE_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ProbeServiceTest {
    @Autowired
    private ProbeService probeService;

    @BeforeEach
    public void setup() {
        probeService.deleteAll();
    }

    @AfterEach
    public void teardown() {
        probeService.deleteAll();
    }

    //Base Test
    @Test
    public void testInsert() {
        Probe probe = new Probe();
        probe.setDescription("Test");
        Probe resultProbe = probeService.insert(probe);
        assertThat(resultProbe.getDescription()).isEqualTo("Test");
    }

    //Base Test
    @Test
    public void testNotFoundException() {
        assertThatThrownBy(() -> {
            Probe probe = probeService.findByProbeId("Non_Existent_Id");
        }).isInstanceOf(CustomNotFoundException.class)
                .hasMessageContaining(PROBE_NOT_FOUND_MSG);
    }
}
