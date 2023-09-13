package com.tum.in.cm.connectorservice.model.result;

import com.tum.in.cm.connectorservice.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "results")
@Getter
@Setter
public class Result {
    @Id
    private String id;
    private String measurementId;
    private String probeId;
    private long timestamp;
    private long execTimestamp;
    private long execStopTimestamp;
    private Binary data;
    private boolean isSuccess;
    private Constants.MeasurementType type;
}
