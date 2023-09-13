package com.tum.in.cm.connectorservice.web.rest.dto.request;

import com.tum.in.cm.connectorservice.util.Constants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NotNull
@AllArgsConstructor
@NoArgsConstructor
public class ProbeStatusRequestObject {
    private String probeId;
    private Constants.ProbeStatus status;
}
