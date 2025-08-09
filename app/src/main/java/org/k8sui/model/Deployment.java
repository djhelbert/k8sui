package org.k8sui.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Data
public class Deployment implements Comparable<Deployment> {
    private String uid;
    private String name;
    private String namespace;
    private Integer replicas;
    private Map<String, String> labels;
    private Map<String, String> selectors;
    private List<Container> containers;

    public Deployment(String uid, String name, String namespace) {
        this.uid = uid;
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Deployment[" + getUid() + ":" + getNamespace() + ":" + getName() + "]";
    }

    @Override
    public int compareTo(@NotNull Deployment d) {
        return name.compareTo(d.getName());
    }
}
