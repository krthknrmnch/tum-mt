package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.tum.in.cm.connectorservice.util.Constants.PROBE_NOT_FOUND_MSG;
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
    public void testUpdateNotFound() {
        Probe probe = new Probe();
        probe.setId("Non_Existent_Id");
        assertThatThrownBy(() -> probeService.update(probe)).isInstanceOf(CustomNotFoundException.class)
                .hasMessageContaining(PROBE_NOT_FOUND_MSG);
    }

    //Base Test
    @Test
    public void testNotFoundException() {
        assertThatThrownBy(() -> probeService.findByProbeId("Non_Existent_Id")).isInstanceOf(CustomNotFoundException.class)
                .hasMessageContaining(PROBE_NOT_FOUND_MSG);
    }
}
