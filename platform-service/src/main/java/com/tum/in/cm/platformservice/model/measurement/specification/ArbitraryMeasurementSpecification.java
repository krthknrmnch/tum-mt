package com.tum.in.cm.platformservice.model.measurement.specification;

import com.tum.in.cm.platformservice.model.measurement.MeasurementSpecification;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Hashtable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArbitraryMeasurementSpecification extends MeasurementSpecification {
    @NotNull
    @Min(5)
    @Max(60)
    private int durationInMinutes;
    @NotBlank
    private String containerImagePath;
    private String containerEntrypointString;
    private ArrayList<String> cmdInputStrings;
    private Hashtable<String, String> envVars;
    @NotBlank
    private String outputPath;
    private boolean addLinuxNetworkAdminCapability;
}
