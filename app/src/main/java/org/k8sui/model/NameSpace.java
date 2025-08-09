package org.k8sui.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

@Data
public class NameSpace implements Comparable<NameSpace> {
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

    @Override
    public String toString() {
        return "Namespace[" + getUid() + ":" + getNamespace() + ":" + getCreation() + ":" + getStatus() + "]";
    }

    @Override
    public int compareTo(@NotNull NameSpace n) {
        return namespace.compareTo(n.getNamespace());
    }
}
