package com.tum.in.cm.probeservice.web.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PingMeasurementSpecification extends MeasurementSpecification {
    private String target;
    private int numberOfPackets;
    private int packetByteSize;
}
