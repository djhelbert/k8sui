package org.k8sui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Environment Variable
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvVar {
  private String from;
  private String name;
  private String value;

  @Override
  public String toString() {
    return (from == null ? "" : from + ":") + name + (value == null ? "" : ":" + value);
  }
}
