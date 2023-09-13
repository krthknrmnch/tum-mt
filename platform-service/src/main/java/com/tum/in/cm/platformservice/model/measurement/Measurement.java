package com.tum.in.cm.platformservice.model.measurement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tum.in.cm.platformservice.util.Constants;
import com.tum.in.cm.platformservice.web.rest.dto.request.MeasurementRequestObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
    @Id
    private String id;
    private Constants.MeasurementType type;
    private MeasurementSpecification measurementSpecification;
    private String description;
    @JsonIgnore
    private String userEmail;
    private long scheduledStartTimestamp;
    private RepeatSpecification repeatSpecification;
    private ProbeSpecification probeSpecification;
    private Constants.MeasurementStatus status;

    public Measurement(MeasurementRequestObject measurementRequestObject, String email, long timestamp) {
        this.setUserEmail(email);
        this.setType(measurementRequestObject.getType());
        this.measurementSpecification = measurementRequestObject.getMeasurementSpecification();
        this.description = measurementRequestObject.getDescription();
        this.scheduledStartTimestamp = timestamp;
        this.setProbeSpecification(measurementRequestObject.getProbeSpecification());
        this.setRepeatSpecification(measurementRequestObject.getRepeatSpecification());
    }
}
