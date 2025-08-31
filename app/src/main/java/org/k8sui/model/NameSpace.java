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

import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Name Space
 */
@Data
@ToString
public class NameSpace implements Comparable<NameSpace> {

  private String uid;
  private String namespace;
  private OffsetDateTime creation;
  private String status;
  private Map<String, String> labels;
  private Map<String, String> annotations;

  /**
   * Constructor
   * @param uid UID
   * @param namespace Name Space
   * @param creation Creation Date
   * @param status Status
   */
  public NameSpace(String uid, String namespace, OffsetDateTime creation, String status) {
    this.uid = uid;
    this.namespace = namespace;
    this.creation = creation;
    this.status = status;
  }

  @Override
  public int compareTo(@NotNull NameSpace nameSpace) {
    return this.namespace.compareTo(nameSpace.getNamespace());
  }
}
