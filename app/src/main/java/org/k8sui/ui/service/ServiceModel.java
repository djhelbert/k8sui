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
import org.k8sui.model.Service;
import org.k8sui.ui.BaseTableModel;

/**
 * Service Model
 */
public class ServiceModel extends BaseTableModel {

  static final String[] headers = {"UID", "Name", "Namespace", "Type", "Cluster IP", "Selectors"};
  private List<Service> services;

  /**
   * Constructor
   * @param serviceList Service List
   */
  public ServiceModel(List<Service> serviceList) {
    super(headers);
    setServices(serviceList);
  }

  public void setServices(List<Service> services) {
    this.services = services;
    Collections.sort(services);
  }

  public Service getService(int row) {
    return services.get(row);
  }

  @Override
  public int getRowCount() {
    return services.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return services.get(row).getUid();
    } else if (col == 1) {
      return services.get(row).getName();
    } else if (col == 2) {
      return services.get(row).getNamespace();
    } else if (col == 3) {
      return services.get(row).getType();
    } else if (col == 4) {
      return services.get(row).getClusterIp();
    }

    if (services.get(row).getSelectors() == null) {
      return "";
    } else {
      return services.get(row).getSelectors().toString();
    }
  }
}
