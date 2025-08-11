package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConfigMap implements Comparable<ConfigMap> {
    private String uid;
    private String name;
    private OffsetDateTime creationDate;
    private String nameSpace;
    private List<ConfigMapData> data;

    @Override
    public int compareTo(@NotNull ConfigMap configMap) {
        return name.compareTo(configMap.getName());
    }
}
