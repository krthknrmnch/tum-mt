package com.tum.in.cm.probeservice.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetricRequestObject {
    private String probeId;
    private long timestamp;
    private byte[] statusData;
    private byte[] locationData;
}
