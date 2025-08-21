/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.deployment;

import java.util.Collections;
import java.util.List;
import org.k8sui.model.Container;
import org.k8sui.ui.BaseTableModel;

/**
 * Container Table Model
 */
public class ContainerModel extends BaseTableModel {

  private static final String[] headers = {"Name", "Image", "Image Pull Policy", "ConfigMapRef",
      "SecretRef", "Volumes", "Ports"};
  private List<Container> containers;

  /**
   * Constructor
   *
   * @param containers Container List
   */
  public ContainerModel(List<Container> containers) {
    super(headers);
    setContainers(containers);
  }

  public void setContainers(List<Container> containers) {
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
      return containers.get(row).getImagePullPolicy();
    } else if (col == 3) {
      return containers.get(row).getConfigMapRef();
    } else if (col == 4) {
      return containers.get(row).getSecretRef();
    } else if (col == 5) {
      if (containers.get(row).getVolumeMounts() == null) {
        return "";
      } else {
        return containers.get(row).getVolumeMounts().toString();
      }
    }

    if (containers.get(row).getPorts() != null) {
      return containers.get(row).getPorts().toString();
    } else {
      return "";
    }
  }
}