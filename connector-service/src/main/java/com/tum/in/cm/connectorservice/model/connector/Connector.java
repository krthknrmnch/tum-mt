package com.tum.in.cm.connectorservice.model.connector;

import com.tum.in.cm.connectorservice.util.Constants;
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
    private String id;
    private String ipPort;
    private String oakestraIpPort;
    private Constants.Region region;
    private Date updateTimestamp;
}

