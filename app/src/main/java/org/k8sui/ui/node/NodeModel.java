/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.node;

import java.util.Collections;
import java.util.List;
import org.k8sui.model.Node;
import org.k8sui.ui.BaseTableModel;

public class NodeModel extends BaseTableModel {

  static final String[] headers = {"UID", "Name", "OS Image", "CPU", "Memory", "Internal IP"};
  private List<Node> nodes;

  public NodeModel(List<Node> nodes) {
    super(headers);
    setNodes(nodes);
  }

  public Node getNode(int row) {
    return nodes.get(row);
  }

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
    Collections.sort(nodes);
  }

  @Override
  public int getRowCount() {
    return nodes.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return nodes.get(row).getUid();
    } else if (col == 1) {
      return nodes.get(row).getName();
    } else if (col == 2) {
      return nodes.get(row).getImage();
    } else if (col == 3) {
      return nodes.get(row).getCpu();
    } else if (col == 4) {
      return nodes.get(row).getMemory();
    }

    return nodes.get(row).getIp();
  }
}