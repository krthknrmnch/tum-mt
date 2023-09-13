package com.tum.in.cm.platformservice.web.rest.dto.internal;

import com.tum.in.cm.platformservice.model.measurement.Measurement;
import com.tum.in.cm.platformservice.model.measurement.MeasurementSpecification;
import com.tum.in.cm.platformservice.model.measurement.RepeatSpecification;
import com.tum.in.cm.platformservice.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternalTaskRequestObject {
    private String measurementId;
    private String probeId;
    long scheduledStartTimestamp;
    private Constants.MeasurementType type;
    private MeasurementSpecification measurementSpecification;
    private RepeatSpecification repeatSpecification;

    public InternalTaskRequestObject(Measurement measurement, String probeId, long timestamp) {
        this.measurementId = measurement.getId();
        this.probeId = probeId;
        this.scheduledStartTimestamp = timestamp;
        this.type = measurement.getType();
        this.measurementSpecification = measurement.getMeasurementSpecification();
        this.repeatSpecification = measurement.getRepeatSpecification();
    }
}
