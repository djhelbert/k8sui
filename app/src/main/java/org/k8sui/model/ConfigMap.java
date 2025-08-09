package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigMap implements Comparable<ConfigMap> {
    private String uid;
    private String name;
    private OffsetDateTime created;
    private String nameSpace;
    private List<ConfigMapData> data;

    @Override
    public int compareTo(ConfigMap cm) {
        return name.compareTo(cm.getName());
    }
}
