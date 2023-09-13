package com.tum.in.cm.platformservice.model.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Metric {
    @Id
    private String id;
    private String probeId;
    private long timestamp;
    private Binary data;
}
