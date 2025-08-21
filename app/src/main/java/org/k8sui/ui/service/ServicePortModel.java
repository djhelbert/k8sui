/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.service;

import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.k8sui.model.ServicePort;

/**
 * Service Port Table Model
 */
public class ServicePortModel extends AbstractTableModel {

  private static final String[] headers = {"Name", "Protocol", "Port", "Target Port", "Node Port"};
  private List<ServicePort> servicePorts;

  /**
   * Constructor
   * @param servicePorts Service Ports
   */
  public ServicePortModel(List<ServicePort> servicePorts) {
    setServicePorts(servicePorts);
  }

  public void setServicePorts(List<ServicePort> servicePorts) {
    this.servicePorts = servicePorts;
    Collections.sort(servicePorts);
  }

  @Override
  public int getRowCount() {
    return servicePorts.size();
  }

  @Override
  public int getColumnCount() {
    return headers.length;
  }

  @Override
  public String getColumnName(int col) {
    return headers[col];
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return servicePorts.get(row).getName();
    } else if (col == 1) {
      return servicePorts.get(row).getProtocol();
    } else if (col == 2) {
      return defaultNull(servicePorts.get(row).getPort());
    } else if (col == 3) {
      return defaultNull(servicePorts.get(row).getTargetPort());
    } else {
      return defaultNull(servicePorts.get(row).getNodePort());
    }
  }

  /**
   * Default Value for Null
   * @param value Value
   * @return String
   */
  private String defaultNull(Integer value) {
    return value == null ? "" : value.toString();
  }
}
