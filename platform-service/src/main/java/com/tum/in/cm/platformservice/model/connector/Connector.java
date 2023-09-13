package com.tum.in.cm.platformservice.model.connector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tum.in.cm.platformservice.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "connectors")
@Getter
@Setter
public class Connector {
    @Id
    @JsonIgnore
    private String id;
    private String ipPort;
    private String oakestraIpPort;
    private Constants.Region region;
    @JsonIgnore
    private Date updateTimestamp;
}
