package com.tum.in.cm.platformservice.model.measurement.specification;

import com.tum.in.cm.platformservice.component.validation.TracerouteMethodSubset;
import com.tum.in.cm.platformservice.model.measurement.MeasurementSpecification;
import com.tum.in.cm.platformservice.util.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParisTracerouteMeasurementSpecification extends MeasurementSpecification {
    @NotBlank
    private String target;
    private int maxHops;
    @NotNull
    @TracerouteMethodSubset(anyOf = {Constants.TracerouteMethod.UDP,
            Constants.TracerouteMethod.ICMP,
            Constants.TracerouteMethod.TCP
    })
    private Constants.TracerouteMethod method;
}
