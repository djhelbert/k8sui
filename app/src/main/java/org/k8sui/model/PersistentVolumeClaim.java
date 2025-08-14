package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersistentVolumeClaim implements Comparable<PersistentVolumeClaim> {
    private String uid;
    private String name;
    private String nameSpace;
    private String storageClassName;
    private List<String> accessModes;
    private Map<String, String> resources;

    @Override
    public int compareTo(@NotNull PersistentVolumeClaim pv) {
        return name.compareTo(pv.getName());
    }
}
