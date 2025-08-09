package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicePort implements Comparable<ServicePort> {
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

    @Override
    public int compareTo(@NotNull ServicePort o) {
        return name.compareTo(o.getName());
    }
}
