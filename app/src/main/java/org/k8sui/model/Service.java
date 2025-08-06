package org.k8sui.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Service implements Comparable<Service> {
    private String uid;
    private String name;
    private String namespace;
    private String type;
    private Map<String, String> selectors;
    private List<Port> ports;

    public Service(String uid, String name, String namespace) {
        this.uid = uid;
        this.namespace = namespace;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getSelectors() {
        return selectors;
    }

    public void setSelectors(Map<String, String> selectors) {
        this.selectors = selectors;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
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
