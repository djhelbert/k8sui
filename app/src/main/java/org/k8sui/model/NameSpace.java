package org.k8sui.model;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

@Data
@ToString
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
    public int compareTo(@NotNull NameSpace nameSpace) {
        return this.namespace.compareTo(nameSpace.getNamespace());
    }
}
