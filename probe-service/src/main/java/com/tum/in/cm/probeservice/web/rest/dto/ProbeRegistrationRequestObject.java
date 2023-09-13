package com.tum.in.cm.probeservice.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
