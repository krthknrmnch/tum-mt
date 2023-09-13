package com.tum.in.cm.connectorservice.web.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tum.in.cm.connectorservice.util.Constants;
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
public class InternalTaskRequestObject {
    private String measurementId;
    private String probeId;
    long scheduledStartTimestamp;
    private Constants.MeasurementType type;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    private MeasurementSpecification measurementSpecification;
    private RepeatSpecification repeatSpecification;
}
