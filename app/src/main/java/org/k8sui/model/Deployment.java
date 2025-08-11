package org.k8sui.model;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class Deployment implements Comparable<Deployment> {
    private String uid;
    private String name;
    private String namespace;
    private Integer replicas;
    private Integer readyReplicas;
    private Map<String, String> labels;
    private Map<String, String> selectors;
    private List<Container> containers;

    public Deployment(String uid, @NotNull String name, @NotNull String namespace) {
        this.uid = uid;
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public int compareTo(@NotNull Deployment deployment) {
        return name.compareTo(deployment.getName());
    }
}
