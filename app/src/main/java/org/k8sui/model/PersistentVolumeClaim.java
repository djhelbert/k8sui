package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
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
    private String status;
    private OffsetDateTime creation;
    private Map<String, String> labels;
    private List<String> accessModes;
    private Map<String, String> capacities;

    @Override
    public int compareTo(@NotNull PersistentVolumeClaim pv) {
        return name.compareTo(pv.getName());
    }
}
