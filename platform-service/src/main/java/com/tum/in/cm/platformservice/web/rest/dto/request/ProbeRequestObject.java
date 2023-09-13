package com.tum.in.cm.platformservice.web.rest.dto.request;

import com.tum.in.cm.platformservice.component.validation.RegionSubset;
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
public class ProbeRequestObject {
    @NotBlank
    private String userEmail;
    @NotBlank
    private String country;
    @NotNull
    @RegionSubset(anyOf = {Constants.Region.AF,
            Constants.Region.AS,
            Constants.Region.EU,
            Constants.Region.OC,
            Constants.Region.NA,
            Constants.Region.SA
    })
    private Constants.Region region;
    @NotNull
    private String description;
}
