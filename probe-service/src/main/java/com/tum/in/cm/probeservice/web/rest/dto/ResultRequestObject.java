package com.tum.in.cm.probeservice.web.rest.dto;

import com.tum.in.cm.probeservice.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
