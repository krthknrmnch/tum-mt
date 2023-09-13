package com.tum.in.cm.connectorservice.web.rest.controller;

import com.tum.in.cm.connectorservice.exception.CustomNotFoundException;
import com.tum.in.cm.connectorservice.model.probe.Probe;
import com.tum.in.cm.connectorservice.service.ProbeService;
import com.tum.in.cm.connectorservice.web.rest.dto.request.ProbeRegistrationRequestObject;
import com.tum.in.cm.connectorservice.web.rest.dto.request.ProbeStatusRequestObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.tum.in.cm.connectorservice.util.Constants.SUCCESS_MSG;

@RestController
@RequestMapping("/api")
public class ProbeController {
    @Autowired
    private ProbeService probeService;

    /**
     * Endpoint to register probe and its IP address to this connector
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerProbe(@RequestHeader("api_key") String api_key, @Valid @RequestBody ProbeRegistrationRequestObject probeRegistrationRequestObject) throws CustomNotFoundException {
        Probe probe;
        try {
            probe = probeService.findByProbeId(probeRegistrationRequestObject.getProbeId());
        } catch (CustomNotFoundException e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String probeApiKey = probe.getApiKey();
        if (!api_key.equals(probeApiKey)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        probe.setConnectorIpPort(probeRegistrationRequestObject.getConnectorIpPort());
        probe.setIpv4(probeRegistrationRequestObject.getIpv4());
        probe.setWiredInterfaceActive(probeRegistrationRequestObject.isWiredInterfaceActive());
        probe.setWifiInterfaceActive(probeRegistrationRequestObject.isWifiInterfaceActive());
        probe.setCellularInterfaceActive(probeRegistrationRequestObject.isCellularInterfaceActive());
        probe.setStarlinkActiveInterface(probeRegistrationRequestObject.getStarlinkActiveInterface());
        probeService.update(probe);

        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }

    /**
     * Endpoint to update probe status
     */
    @PostMapping("/status")
    public ResponseEntity<String> updateProbeStatus(@RequestHeader("api_key") String api_key, @Valid @RequestBody ProbeStatusRequestObject probeStatusRequestObject) throws CustomNotFoundException {
        Probe probe;
        try {
            probe = probeService.findByProbeId(probeStatusRequestObject.getProbeId());
        } catch (CustomNotFoundException e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String probeApiKey = probe.getApiKey();
        if (!api_key.equals(probeApiKey)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        probe.setStatus(probeStatusRequestObject.getStatus());
        probeService.update(probe);

        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }
}
