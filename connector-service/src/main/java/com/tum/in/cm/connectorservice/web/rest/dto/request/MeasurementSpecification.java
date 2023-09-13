package com.tum.in.cm.connectorservice.web.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArbitraryMeasurementSpecification.class, name = "ARBITRARY"),
        @JsonSubTypes.Type(value = PingMeasurementSpecification.class, name = "PING"),
        @JsonSubTypes.Type(value = TracerouteMeasurementSpecification.class, name = "TRACEROUTE"),
        @JsonSubTypes.Type(value = ParisTracerouteMeasurementSpecification.class, name = "PARIS_TRACEROUTE"),
        @JsonSubTypes.Type(value = DnsMeasurementSpecification.class, name = "DNS"),
        @JsonSubTypes.Type(value = HttpMeasurementSpecification.class, name = "HTTP")
})
public abstract class MeasurementSpecification {
}
