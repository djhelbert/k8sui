package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeMount {
  private String name;
  private String mountPath;

  @Override
  public String toString() {
    return name + ":" + mountPath;
  }
}
