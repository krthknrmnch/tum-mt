package com.tum.in.cm.platformservice.web.rest.controller.exposed;

import com.tum.in.cm.platformservice.component.security.HasAuthorizedAdminRole;
import com.tum.in.cm.platformservice.component.security.HasAuthorizedUserRole;
import com.tum.in.cm.platformservice.component.security.TokenExtractor;
import com.tum.in.cm.platformservice.exception.CustomAuthException;
import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.connector.Connector;
import com.tum.in.cm.platformservice.model.probe.Probe;
import com.tum.in.cm.platformservice.service.ConnectorService;
import com.tum.in.cm.platformservice.service.ProbeService;
import com.tum.in.cm.platformservice.util.Constants;
import com.tum.in.cm.platformservice.web.rest.dto.request.ProbeRequestObject;
import com.tum.in.cm.platformservice.web.rest.dto.response.ProbesResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static com.tum.in.cm.platformservice.util.Constants.*;

@RestController
@RequestMapping(value = "/api")
public class ProbeController {
    @Autowired
    private ProbeService probeService;

    @Autowired
    private ConnectorService connectorService;

    /**
     * Endpoint to fetch all probes with optional country and status filters
     */
    @Operation(summary = "Get all probes with a filter on country and probe status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProbesResponseObject.class))}),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/probes", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    @Tag(name = "Probe")
    public ResponseEntity<ProbesResponseObject> getAllProbes(@RequestParam(required = false) String country,
                                                             @RequestParam(required = false) Constants.ProbeStatus status,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        ProbesResponseObject probesResponseObject = new ProbesResponseObject();
        List<Probe> probeList;
        Pageable pageable = PageRequest.of(page, size);
        Page<Probe> pagedProbes;

        if (country == null && status == null) {
            pagedProbes = probeService.listAll(pageable);
        } else if (country == null) {
            pagedProbes = probeService.listByStatus(status, pageable);
        } else if (status == null) {
            pagedProbes = probeService.listByCountry(country, pageable);
        } else {
            pagedProbes = probeService.listByCountryAndStatus(country, status, pageable);
        }

        probeList = pagedProbes.getContent();
        probesResponseObject.setCount(pagedProbes.getTotalElements());
        probesResponseObject.setProbeList(probeList);
        probesResponseObject.setCurrentPage(pagedProbes.getNumber());
        probesResponseObject.setTotalPages(pagedProbes.getTotalPages());

        return new ResponseEntity<>(probesResponseObject, HttpStatus.OK);
    }

    /**
     * Endpoint to fetch all probes for an authenticated user
     */
    @Operation(summary = "Get all probes for authenticated user with a filter on country and probe status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ProbesResponseObject.class))}),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/probes/my", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    @Tag(name = "Probe")
    public ResponseEntity<ProbesResponseObject> getProbesForSelf(@RequestParam(required = false) String country,
                                                                 @RequestParam(required = false) Constants.ProbeStatus status,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) throws CustomAuthException {
        String email = TokenExtractor.fetchToken();
        ProbesResponseObject probesResponseObject = new ProbesResponseObject();
        List<Probe> probeList;
        Pageable pageable = PageRequest.of(page, size);
        Page<Probe> pagedProbes;

        if (country == null && status == null) {
            pagedProbes = probeService.listByUserEmail(email, pageable);
        } else if (country == null) {
            pagedProbes = probeService.listByUserEmailAndStatus(email, status, pageable);
        } else if (status == null) {
            pagedProbes = probeService.listByUserEmailAndCountry(email, country, pageable);
        } else {
            pagedProbes = probeService.listByUserEmailAndCountryAndStatus(email, country, status, pageable);
        }

        probeList = pagedProbes.getContent();
        probesResponseObject.setCount(pagedProbes.getTotalElements());
        probesResponseObject.setProbeList(probeList);
        probesResponseObject.setCurrentPage(pagedProbes.getNumber());
        probesResponseObject.setTotalPages(pagedProbes.getTotalPages());

        return new ResponseEntity<>(probesResponseObject, HttpStatus.OK);
    }

    /**
     * Endpoint to fetch a single probe by probeId
     */
    @Operation(summary = "Get a probe by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Probe.class))}),
            @ApiResponse(responseCode = "404", description = PROBE_NOT_FOUND_MSG, content = @Content),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/probes/{probeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    @Tag(name = "Probe")
    public ResponseEntity<Probe> getProbeById(@PathVariable(value = "probeId") String probeId) throws CustomNotFoundException {
        return new ResponseEntity<>(probeService.findByProbeId(probeId), HttpStatus.OK);
    }

    /**
     * Endpoint to fetch the docker-compose config file for a probe
     */
    @Operation(summary = "Download the docker-compose config file for a probe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/octet-stream")}),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MSG, content = @Content),
            @ApiResponse(responseCode = "404", description = PROBE_NOT_FOUND_MSG, content = @Content),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/probes/config/{probeId}", produces = "application/octet-stream")
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    @Tag(name = "Probe")
    public void getProbeHostFile(@PathVariable(value = "probeId") String probeId, HttpServletResponse response) throws CustomNotFoundException, CustomAuthException, IOException {
        String email = TokenExtractor.fetchToken();
        Connector connector;
        Probe probe = probeService.findByProbeId(probeId);
        if (!probe.getUserEmail().equals(email)) {
            throw new CustomAuthException(UNAUTHORIZED_MSG);
        }
        File file = ResourceUtils.getFile("classpath:probe-docker-compose.yml");
        Charset charset = StandardCharsets.UTF_8;
        String text = Files.readString(file.toPath(), charset);
        text = text.replaceAll("PROBE_ID_STRING", probeId);
        text = text.replaceAll("PROBE_API_KEY_STRING", probe.getApiKey());
        connector = connectorService.findOneByRegion(probe.getRegion());
        if (connector == null) {
            connector = connectorService.findOneByRegionNotLike(probe.getRegion());
        }
        if (connector != null) {
            text = text.replaceAll("_NO_CONNECTORS_RUNNING_", connector.getIpPort());
        }
        byte[] outputData = text.getBytes();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/octet-stream");
        response.setContentLength(outputData.length);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "docker-compose" + ".yml" + "\"");
        response.getOutputStream().write(outputData);
    }

    /**
     * Endpoint to create a probe
     */
    @Operation(summary = "Create a new probe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content)})
    @PostMapping(value = "/probes")
    @HasAuthorizedAdminRole
    @SecurityRequirement(name = "Bearer_Authentication")
    @Tag(name = "Admin")
    public ResponseEntity<String> createProbe(@Valid @RequestBody ProbeRequestObject probeRequestObject) {
        Probe newProbe = new Probe();
        newProbe.setUserEmail(probeRequestObject.getUserEmail());
        newProbe.setCountry(probeRequestObject.getCountry());
        newProbe.setRegion(probeRequestObject.getRegion());
        newProbe.setDescription(probeRequestObject.getDescription());
        newProbe.setApiKey(UUID.randomUUID().toString());
        Probe insertedProbe = probeService.insert(newProbe);

        return new ResponseEntity<>(insertedProbe.getId(), HttpStatus.OK);
    }
}
