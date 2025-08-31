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
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Persistent Volume Claim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersistentVolumeClaim implements Comparable<PersistentVolumeClaim> {

  private String uid;
  private String name;
  private String nameSpace;
  private String storageClassName;
  private String status;
  private OffsetDateTime creation;
  private Map<String, String> labels;
  private List<String> accessModes;
  private Map<String, String> capacities;
  private Map<String, String> annotations;

  @Override
  public int compareTo(@NotNull PersistentVolumeClaim pv) {
    return name.compareTo(pv.getName());
  }
}
