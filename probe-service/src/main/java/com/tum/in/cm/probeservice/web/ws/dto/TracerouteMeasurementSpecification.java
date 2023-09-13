package com.tum.in.cm.probeservice.web.ws.dto;

import com.tum.in.cm.probeservice.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TracerouteMeasurementSpecification extends MeasurementSpecification {
    private String target;
    private int maxHops;
    private Constants.TracerouteMethod method;
}
