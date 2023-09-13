package com.tum.in.cm.probeservice.web.rest.dto;

import com.tum.in.cm.probeservice.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProbeStatusRequestObject {
    private String probeId;
    private Constants.ProbeStatus status;
}
