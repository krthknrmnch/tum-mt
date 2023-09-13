package com.tum.in.cm.platformservice.model.measurement.specification;

import com.tum.in.cm.platformservice.model.measurement.MeasurementSpecification;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DnsMeasurementSpecification extends MeasurementSpecification {
    @NotBlank
    private String target;
}
