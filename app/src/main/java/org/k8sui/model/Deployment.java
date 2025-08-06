package org.k8sui.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Deployment implements Comparable<Deployment> {
    private String uid;
    private String name;
    private String namespace;
    private Integer replicas;
    private String image;
    private Integer port;
    private Map<String, String> labels;
    private List<Container> containers;

    public Deployment(String uid, String name, String namespace) {
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

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    @Override
    public String toString() {
        return "Deployment[" + getUid() + ":" + getNamespace() + ":" + getName() + "]";
    }

    @Override
    public int compareTo(@NotNull Deployment d) {
        return d.getName().compareTo(getName());
    }
}
