package com.tum.in.cm.platformservice.model.measurement;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProbeSpecification {
    @NotEmpty
    private ArrayList<String> probeIds;
}
