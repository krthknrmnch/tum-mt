package com.tum.in.cm.connectorservice.model.probe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tum.in.cm.connectorservice.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "probes")
@Getter
@Setter
public class Probe {
    @Id
    private String id;
    @JsonIgnore
    private String userEmail;
    @JsonIgnore
    private String apiKey;
    private String ipv4;
    private String country;
    private Constants.Region region;
    private Constants.ProbeStatus status;
    private String description;
    @JsonIgnore
    private String connectorIpPort;
    private boolean isWiredInterfaceActive;
    private boolean isWifiInterfaceActive;
    private boolean isCellularInterfaceActive;
    private String starlinkActiveInterface;
}
