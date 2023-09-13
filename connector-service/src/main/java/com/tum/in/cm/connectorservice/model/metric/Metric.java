package com.tum.in.cm.connectorservice.model.metric;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "metrics")
@Getter
@Setter
public class Metric {
    @Id
    private String id;
    private String probeId;
    private long timestamp;
    private Binary data;
}
