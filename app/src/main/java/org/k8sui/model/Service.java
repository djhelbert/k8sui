package org.k8sui.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Data
public class Service implements Comparable<Service> {
    private String uid;
    private String name;
    private String namespace;
    private String type;
    private String clusterIp;
    private Map<String, String> selectors;
    private List<ServicePort> servicePorts;

    public Service(String uid, String name, String namespace) {
        this.uid = uid;
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Service[" + getUid() + ":" + getNamespace() + ":" + getType() + ":" + getName() + "]";
    }

    @Override
    public int compareTo(@NotNull Service svc) {
        return svc.getName().compareTo(svc.getName());
    }
}
