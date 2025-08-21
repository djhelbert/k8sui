/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.namespace;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.k8sui.model.NameSpace;
import org.k8sui.ui.BaseTableModel;

public class NameSpaceModel extends BaseTableModel {

  private static final String[] headers = {"UID", "Namespace", "Creation Date", "Status"};
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");
  private List<NameSpace> namespaces;

  /**
   * Constructor
   *
   * @param nodes NameSpace List
   */
  public NameSpaceModel(List<NameSpace> nodes) {
    super(headers);
    setNodes(nodes);
  }

  public void setNodes(List<NameSpace> nodes) {
    this.namespaces = nodes;
    Collections.sort(nodes);
  }

  public NameSpace getNameSpace(int row) {
    return namespaces.get(row);
  }

  @Override
  public int getRowCount() {
    return namespaces.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return namespaces.get(row).getUid();
    } else if (col == 1) {
      return namespaces.get(row).getNamespace();
    } else if (col == 2) {
      if (namespaces.get(row).getCreation() == null) {
        return "";
      } else {
        return formatter.format(namespaces.get(row).getCreation());
      }
    }

    return namespaces.get(row).getStatus();
  }
}