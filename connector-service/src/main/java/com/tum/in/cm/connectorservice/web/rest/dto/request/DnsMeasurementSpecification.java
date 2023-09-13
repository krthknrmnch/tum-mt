package com.tum.in.cm.connectorservice.web.rest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DnsMeasurementSpecification extends MeasurementSpecification {
    private String target;
}
