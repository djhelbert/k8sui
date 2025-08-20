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

import java.util.Map;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Data
@ToString
public class Node implements Comparable<Node> {

  private String uid;
  private String name;
  private String cpu;
  private String image;
  private String ip;
  private String memory;
  private Map<String, String> labels;

  public Node(String uid, String name, String cpu) {
    this.uid = uid;
    this.name = name;
    this.cpu = cpu;
  }

  @Override
  public int compareTo(@NotNull Node node) {
    return name.compareTo(node.getName());
  }
}
