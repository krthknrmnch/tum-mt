package com.tum.in.cm.platformservice.model.result;

import com.tum.in.cm.platformservice.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
