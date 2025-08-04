package org.k8sui.model;

import java.time.OffsetDateTime;

public class NameSpace {
    private String uid;
    private String namespace;
    private OffsetDateTime creation;

    public NameSpace(String uid, String namespace, OffsetDateTime creation) {
        this.uid = uid;
        this.namespace = namespace;
        this.creation = creation;
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

    @Override
    public String toString() {
        return "Namespace[" + uid + ":" + namespace + ":" + creation + "]";
    }
}
