package com.tum.in.cm.connectorservice.web.rest.dto.request;

import com.tum.in.cm.connectorservice.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpMeasurementSpecification extends MeasurementSpecification {
    private String target;
    private Constants.HttpMethod method;
    private String queryString;
}
