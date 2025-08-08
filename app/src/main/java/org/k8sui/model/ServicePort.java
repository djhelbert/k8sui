package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicePort {
    private String name;
    private String protocol;
    private Integer port;
    private Integer targetPort;
    private String appProtocol;
    private Integer nodePort;

    @Override
    public String toString() {
        return "Port[" + getName() + " " + getPort() + "]";
    }
}
