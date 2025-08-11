package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecretData implements Comparable<SecretData> {
    private String key;
    private byte[] value;

    @Override
    public int compareTo(@NotNull SecretData secretData) {
        return key.compareTo(secretData.getKey());
    }
}
