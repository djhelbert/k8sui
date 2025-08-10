package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigMapData implements Comparable<ConfigMapData> {
    private String key;
    private String value;

    @Override
    public int compareTo(ConfigMapData data) {
        return key.compareTo(data.getKey());
    }
}
