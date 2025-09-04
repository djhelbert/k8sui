/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.pod;

import java.util.Collections;
import java.util.List;
import org.k8sui.model.Pod;
import org.k8sui.ui.BaseTableModel;

/**
 * Service Model
 */
public class PodModel extends BaseTableModel {

  static final String[] headers = {"UID", "Name", "Namespace", "Status", "IP", "Node", "Creation"};
  private List<Pod> pods;

  /**
   * Constructor
   *
   * @param pods Pod List
   */
  public PodModel(List<Pod> pods) {
    super(headers);
    setPods(pods);
  }

  /**
   * Set Pods
   *
   * @param pods Pod List
   */
  public void setPods(List<Pod> pods) {
    this.pods = pods;
    Collections.sort(pods);
  }

  public Pod getPod(int row) {
    return pods.get(row);
  }

  @Override
  public int getRowCount() {
    return pods.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return pods.get(row).getUid();
    } else if (col == 1) {
      return pods.get(row).getName();
    } else if (col == 2) {
      return pods.get(row).getNamespace();
    } else if (col == 3) {
      return pods.get(row).getStatus();
    } else if (col == 4) {
      return pods.get(row).getIp();
    } else if (col == 5) {
      return pods.get(row).getNode();
    } else {
      return pods.get(row).getCreation().toString();
    }
  }
}
