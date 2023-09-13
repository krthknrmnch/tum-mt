package com.tum.in.cm.platformservice.model.measurement.specification;

import com.tum.in.cm.platformservice.component.validation.HttpMethodSubset;
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
public class HttpMeasurementSpecification extends MeasurementSpecification {
    @NotBlank
    private String target;
    @NotNull
    @HttpMethodSubset(anyOf = {Constants.HttpMethod.GET,
            Constants.HttpMethod.POST
    })
    private Constants.HttpMethod method;
    private String queryString;
}
