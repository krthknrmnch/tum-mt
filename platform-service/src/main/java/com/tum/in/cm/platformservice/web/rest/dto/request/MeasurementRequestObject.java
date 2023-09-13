package com.tum.in.cm.platformservice.web.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tum.in.cm.platformservice.component.validation.MeasurementTypeSubset;
import com.tum.in.cm.platformservice.model.measurement.MeasurementSpecification;
import com.tum.in.cm.platformservice.model.measurement.ProbeSpecification;
import com.tum.in.cm.platformservice.model.measurement.RepeatSpecification;
import com.tum.in.cm.platformservice.model.measurement.specification.*;
import com.tum.in.cm.platformservice.util.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementRequestObject {
    @NotNull
    @MeasurementTypeSubset(anyOf = {Constants.MeasurementType.ARBITRARY,
            Constants.MeasurementType.PING,
            Constants.MeasurementType.TRACEROUTE,
            Constants.MeasurementType.PARIS_TRACEROUTE,
            Constants.MeasurementType.DNS,
            Constants.MeasurementType.HTTP
    })
    private Constants.MeasurementType type;
    @Schema(oneOf = {
            ArbitraryMeasurementSpecification.class,
            PingMeasurementSpecification.class,
            TracerouteMeasurementSpecification.class,
            ParisTracerouteMeasurementSpecification.class,
            DnsMeasurementSpecification.class,
            HttpMeasurementSpecification.class
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @NotNull
    @Valid
    private MeasurementSpecification measurementSpecification;
    @NotNull
    private String description;
    @NotNull
    @Valid
    private RepeatSpecification repeatSpecification;
    @NotNull
    @Valid
    private ProbeSpecification probeSpecification;
}
