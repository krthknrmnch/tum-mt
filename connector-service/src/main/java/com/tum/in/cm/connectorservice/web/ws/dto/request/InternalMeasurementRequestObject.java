package com.tum.in.cm.connectorservice.web.ws.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tum.in.cm.connectorservice.util.Constants;
import com.tum.in.cm.connectorservice.web.rest.dto.request.MeasurementSpecification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
