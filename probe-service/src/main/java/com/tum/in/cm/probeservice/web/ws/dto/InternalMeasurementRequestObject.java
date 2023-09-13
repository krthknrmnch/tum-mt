package com.tum.in.cm.probeservice.web.ws.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tum.in.cm.probeservice.util.Constants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NotNull
@NoArgsConstructor
@AllArgsConstructor
public class InternalMeasurementRequestObject {
    private String measurementId;
    private String probeId;
    private long timestamp;
    private Constants.MeasurementType type;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    private MeasurementSpecification measurementSpecification;
}
