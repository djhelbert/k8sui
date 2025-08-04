package org.k8sui.model;

public class Deployment {
    private String uid;
    private String name;
    private String namespace;

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

    @Override
    public String toString() {
        return "Deployment[" + getUid() + ":" + getNamespace() + ":" + getName() + "]";
    }
}
