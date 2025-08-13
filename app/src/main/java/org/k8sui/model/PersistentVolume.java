package org.k8sui.model;

import io.kubernetes.client.custom.Quantity;
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
public class PersistentVolume implements Comparable<PersistentVolume> {
    private String uid;
    private String name;
    private String nameSpace;
    private String storageClassName;
    private List<String> accessModes;
    private Map<String, String> capacities;
    private String hostPath;

    @Override
    public int compareTo(@NotNull PersistentVolume pv) {
        return name.compareTo(pv.getName());
    }
}
