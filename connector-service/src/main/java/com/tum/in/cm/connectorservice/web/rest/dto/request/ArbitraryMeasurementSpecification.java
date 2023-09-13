package com.tum.in.cm.connectorservice.web.rest.dto.request;

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
    private int durationInMinutes;
    private String containerImagePath;
    private String containerEntrypointString;
    private ArrayList<String> cmdInputStrings;
    private Hashtable<String, String> envVars;
    private String outputPath;
    private boolean addLinuxNetworkAdminCapability;
}
