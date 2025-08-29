/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.model;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Data
@ToString
public class Service implements Comparable<Service> {

  private String uid;
  private String name;
  private String namespace;
  private String type;
  private String clusterIp;
  private Map<String, String> selectors;
  private Map<String, String> labels;
  private List<ServicePort> servicePorts;

  /**
   * Constructor
   *
   * @param uid       UID
   * @param name      Name
   * @param namespace Name Space
   */
  public Service(String uid, String name, String namespace) {
    this.uid = uid;
    this.namespace = namespace;
    this.name = name;
  }

  @Override
  public int compareTo(@NotNull Service service) {
    return name.compareTo(service.getName());
  }
}
