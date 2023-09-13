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
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequestObject {
    private String measurementId;
    private String probeId;
    private long timestamp;
    private long execTimestamp;
    private long execStopTimestamp;
    private boolean isSuccess;
    private Constants.MeasurementType type;
}
