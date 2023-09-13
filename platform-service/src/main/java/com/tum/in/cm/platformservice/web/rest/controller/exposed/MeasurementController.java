package com.tum.in.cm.platformservice.web.rest.controller.exposed;

import com.tum.in.cm.platformservice.component.security.HasAuthorizedUserRole;
import com.tum.in.cm.platformservice.component.security.TokenExtractor;
import com.tum.in.cm.platformservice.exception.CustomAuthException;
import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.exception.CustomValidationException;
import com.tum.in.cm.platformservice.exception.RateLimitsException;
import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.service.MeasurementService;
import com.tum.in.cm.platformservice.service.ProbeService;
import com.tum.in.cm.platformservice.service.SchedulingService;
import com.tum.in.cm.platformservice.util.Constants;
import com.tum.in.cm.platformservice.web.rest.dto.request.MeasurementRequestObject;
import com.tum.in.cm.platformservice.web.rest.dto.response.MeasurementsResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static com.tum.in.cm.platformservice.util.Constants.*;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "Measurement")
public class MeasurementController {
    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private SchedulingService schedulingService;

    @Autowired
    private ProbeService probeService;

    /**
     * Endpoint to fetch all measurements with optional type filter
     */
    @Operation(summary = "Get all measurements with a filter on type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MeasurementsResponseObject.class))}),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/measurements", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    public ResponseEntity<MeasurementsResponseObject> getAllMeasurements(@RequestParam(required = false) Constants.MeasurementType type,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        MeasurementsResponseObject measurementsResponseObject = new MeasurementsResponseObject();
        List<Measurement> measurementList;
        Pageable pageable = PageRequest.of(page, size);
        Page<Measurement> pagedMeasurements;

        if (type == null) {
            pagedMeasurements = measurementService.listAll(pageable);
        } else {
            pagedMeasurements = measurementService.listByType(type, pageable);
        }

        measurementList = pagedMeasurements.getContent();
        measurementsResponseObject.setCount(pagedMeasurements.getTotalElements());
        measurementsResponseObject.setMeasurementList(measurementList);
        measurementsResponseObject.setCurrentPage(pagedMeasurements.getNumber());
        measurementsResponseObject.setTotalPages(pagedMeasurements.getTotalPages());

        return new ResponseEntity<>(measurementsResponseObject, HttpStatus.OK);
    }

    /**
     * Endpoint to fetch all measurements for authenticated user with optional type filter
     */
    @Operation(summary = "Get all measurements for authenticated user with a filter on type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MeasurementsResponseObject.class))}),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/measurements/my", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    public ResponseEntity<MeasurementsResponseObject> getMeasurementsForSelf(@RequestParam(required = false) Constants.MeasurementType type,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) throws CustomAuthException {
        String email = TokenExtractor.fetchToken();
        MeasurementsResponseObject measurementsResponseObject = new MeasurementsResponseObject();
        List<Measurement> measurementList;
        Pageable pageable = PageRequest.of(page, size);
        Page<Measurement> pagedMeasurements;

        if (email == null && type == null) {
            pagedMeasurements = measurementService.listAll(pageable);
        } else if (email == null) {
            pagedMeasurements = measurementService.listByType(type, pageable);
        } else if (type == null) {
            pagedMeasurements = measurementService.listByUserEmail(email, pageable);
        } else {
            pagedMeasurements = measurementService.listByTypeAndUserEmail(type, email, pageable);
        }

        measurementList = pagedMeasurements.getContent();
        measurementsResponseObject.setCount(pagedMeasurements.getTotalElements());
        measurementsResponseObject.setMeasurementList(measurementList);
        measurementsResponseObject.setCurrentPage(pagedMeasurements.getNumber());
        measurementsResponseObject.setTotalPages(pagedMeasurements.getTotalPages());

        return new ResponseEntity<>(measurementsResponseObject, HttpStatus.OK);
    }

    /**
     * Endpoint to fetch a single measurement by measurementId
     */
    @Operation(summary = "Get a single measurement by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Measurement.class))}),
            @ApiResponse(responseCode = "404", description = MEASUREMENT_NOT_FOUND_MSG, content = @Content),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @GetMapping(value = "/measurements/{measurementId}")
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    public ResponseEntity<Measurement> getByMeasurementId(@PathVariable(value = "measurementId") String measurementId) throws CustomNotFoundException {
        return new ResponseEntity<>(measurementService.findByMeasurementId(measurementId), HttpStatus.OK);
    }

    /**
     * Endpoint to create a measurement
     */
    @Operation(summary = "Create a measurement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "401", content = @Content),
            @ApiResponse(responseCode = "422", content = @Content, description = "Unprocessable Entity - Measurement creation failed. Measurement/Probe rate limits exceeded."),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @PostMapping(value = "/measurements")
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    public ResponseEntity<String> createMeasurement(@Valid @RequestBody MeasurementRequestObject measurementRequestObject) throws CustomAuthException, RateLimitsException, CustomValidationException {
        String email = TokenExtractor.fetchToken();
        //Apply user rate limit checks
        //1. Probe limit checks
        List<String> probesList = probeService.buildProbeListFromSpecification(measurementRequestObject.getProbeSpecification());
        for (String probeId : probesList) {
            try {
                probeService.findByProbeId(probeId);
            } catch (CustomNotFoundException e) {
                throw new CustomValidationException(e.getMessage());
            }
        }
        if (probesList.size() > 100) {
            throw new RateLimitsException("Probe limits exceeded");
        }
        //2. Measurement limit checks
        List<Measurement> existingMeasurements = measurementService.listByUserEmail(email);
        if (!measurementRequestObject.getType().equals(MeasurementType.ARBITRARY)) {
            //Predefined measurements - rate limit checks
            long countScheduledPredefinedMeasurements = existingMeasurements
                    .stream()
                    .filter(measurement -> measurement.getType() != MeasurementType.ARBITRARY)
                    .filter(measurement -> measurement.getStatus() == MeasurementStatus.SCHEDULED)
                    .count();
            if (countScheduledPredefinedMeasurements > 24L) {
                throw new RateLimitsException("Predefined measurement limits exceeded");
            }
        } else {
            //Arbitrary measurements - rate limit checks
            long countScheduledArbitraryMeasurements = existingMeasurements
                    .stream()
                    .filter(measurement -> measurement.getType() == MeasurementType.ARBITRARY)
                    .filter(measurement -> measurement.getStatus() == MeasurementStatus.SCHEDULED)
                    .count();
            if (countScheduledArbitraryMeasurements > 2L) {
                throw new RateLimitsException("Arbitrary measurement limits exceeded");
            }
        }
        long currentTimestamp = Instant.now().getEpochSecond();
        Measurement measurement = new Measurement(measurementRequestObject, email, currentTimestamp);
        Measurement insertedMeasurement;
        if (schedulingService.isMeasurementConflicting(measurement, probesList)) {
            measurement.setStatus(MeasurementStatus.SCHEDULING_FAILED);
            insertedMeasurement = measurementService.insert(measurement);
        } else {
            measurement.setStatus(MeasurementStatus.SCHEDULED);
            insertedMeasurement = measurementService.insert(measurement);
            schedulingService.scheduleMeasurementJob(insertedMeasurement, probesList);
        }
        return new ResponseEntity<>(insertedMeasurement.getId(), HttpStatus.OK);
    }

    /**
     * Endpoint to stop a single measurement by measurementId
     */
    @Operation(summary = "Stop an ongoing or scheduled measurement by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MSG, content = @Content),
            @ApiResponse(responseCode = "404", description = MEASUREMENT_NOT_FOUND_MSG, content = @Content),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MSG, content = @Content)})
    @DeleteMapping(value = "/measurements/{measurementId}")
    @HasAuthorizedUserRole
    @SecurityRequirement(name = "Bearer_Authentication")
    public ResponseEntity<String> stopByMeasurementId(@PathVariable(value = "measurementId") String measurementId) throws CustomNotFoundException, CustomAuthException {
        String email = TokenExtractor.fetchToken();
        Measurement measurement = measurementService.findByMeasurementId(measurementId);
        if (email.matches(measurement.getUserEmail())) {
            measurementService.stopMeasurementById(measurementId);
        } else {
            throw new CustomAuthException(UNAUTHORIZED_MSG);
        }
        return new ResponseEntity<>(SUCCESS_MSG, HttpStatus.OK);
    }
}
