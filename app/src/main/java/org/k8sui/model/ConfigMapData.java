package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigMapData implements Comparable<ConfigMapData> {
    private String key;
    private String value;

    @Override
    public int compareTo(@NotNull ConfigMapData configMapData) {
        return key.compareTo(configMapData.getKey());
    }
}
