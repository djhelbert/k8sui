package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServicePort implements Comparable<ServicePort> {
    private String name;
    private String protocol;
    private Integer port;
    private Integer targetPort;
    private String appProtocol;
    private Integer nodePort;

    @Override
    public int compareTo(@NotNull ServicePort servicePort) {
        return name.compareTo(servicePort.getName());
    }
}
