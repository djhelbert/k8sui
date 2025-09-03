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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * Pod Container
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PodContainer implements Comparable<PodContainer> {

  private String name;
  private String image;
  private String status;
  private List<String> mounts;
  private List<EnvVar> variables;

  @Override
  public int compareTo(@NotNull PodContainer container) {
    return name.compareTo(container.getName());
  }
}
