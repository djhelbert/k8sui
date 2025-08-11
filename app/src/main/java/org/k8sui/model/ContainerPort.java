package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerPort implements Comparable<ContainerPort> {
    private Integer containerPort;

    @Override
    public String toString() {
        return containerPort.toString();
    }

    @Override
    public int compareTo(@NotNull ContainerPort containerPort) {
        return this.containerPort.compareTo(containerPort.getContainerPort());
    }
}
