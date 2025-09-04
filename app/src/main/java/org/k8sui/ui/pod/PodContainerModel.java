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
import org.k8sui.model.PodContainer;
import org.k8sui.ui.BaseTableModel;

/**
 * Container Table Model
 */
public class PodContainerModel extends BaseTableModel {

  private static final String[] headers = {"Name", "Image", "Liveness", "Readiness", "Mounts", "Variables"};
  private List<PodContainer> containers;

  /**
   * Constructor
   *
   * @param containers Pod Container List
   */
  public PodContainerModel(List<PodContainer> containers) {
    super(headers);
    setContainers(containers);
  }

  /**
   * Set Pod Containers
   *
   * @param containers Pod Container List
   */
  public void setContainers(List<PodContainer> containers) {
    this.containers = containers;
    Collections.sort(containers);
  }

  @Override
  public int getRowCount() {
    return containers.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return containers.get(row).getName();
    } else if (col == 1) {
      return containers.get(row).getImage();
    } else if (col == 2) {
      return containers.get(row).getLiveness();
    } else if (col == 3) {
      return containers.get(row).getReadiness();
    } else if (col == 4) {
      if (containers.get(row).getMounts() == null) {
        return "";
      } else {
        return containers.get(row).getMounts().toString();
      }
    } else {
      return containers.get(row).getVariables().toString();
    }
  }
}