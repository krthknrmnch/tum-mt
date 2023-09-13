package com.tum.in.cm.connectorservice.web.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NotNull
@NoArgsConstructor
@AllArgsConstructor
public class MetricRequestObject {
    private String probeId;
    private long timestamp;
    private byte[] statusData;
    private byte[] locationData;
}
