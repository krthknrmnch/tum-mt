package com.tum.in.cm.connectorservice.web.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NotNull
@AllArgsConstructor
@NoArgsConstructor
public class ProbeRegistrationRequestObject {
    private String probeId;
    private String ipv4;
    private String connectorIpPort;
    private boolean isWiredInterfaceActive;
    private boolean isWifiInterfaceActive;
    private boolean isCellularInterfaceActive;
    private String starlinkActiveInterface;
}

