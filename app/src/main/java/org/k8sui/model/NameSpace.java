package org.k8sui.model;

import java.time.OffsetDateTime;

public class NameSpace {
    private String uid;
    private String namespace;
    private OffsetDateTime creation;
    private String status;

    public NameSpace(String uid, String namespace, OffsetDateTime creation, String status) {
        this.uid = uid;
        this.namespace = namespace;
        this.creation = creation;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public OffsetDateTime getCreation() {
        return creation;
    }

    public void setCreation(OffsetDateTime creation) {
        this.creation = creation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Namespace[" + getUid() + ":" + getNamespace() + ":" + getCreation() + ":" + getStatus() + "]";
    }
}
