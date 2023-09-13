package com.tum.in.cm.platformservice.model.task;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tum.in.cm.platformservice.model.measurement.MeasurementSpecification;
import com.tum.in.cm.platformservice.model.measurement.RepeatSpecification;
import com.tum.in.cm.platformservice.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
@Getter
@Setter
public class Task {
    @Id
    private String id;
    private String measurementId;
    private String probeId;
    private Constants.MeasurementType type;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    private MeasurementSpecification measurementSpecification;
    private RepeatSpecification repeatSpecification;
    private int nextExecutionNumber;
    private long scheduledStartTimestamp;
    private long scheduledStopTimestamp;
}
