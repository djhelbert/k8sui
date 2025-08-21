/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.configmap;

import java.util.Collections;
import java.util.List;
import org.k8sui.model.ConfigMapData;
import org.k8sui.ui.BaseTableModel;

/**
 * Config Map Data Table Model
 */
public class ConfigMapDataModel extends BaseTableModel {

  private static final String[] headers = {"Key", "Value"};
  private List<ConfigMapData> data;

  /**
   * Constructor
   *
   * @param data ConfigMap Data
   */
  public ConfigMapDataModel(List<ConfigMapData> data) {
    super(headers);
    setData(data);
  }

  public void setData(List<ConfigMapData> data) {
    this.data = data;
    Collections.sort(data);
  }

  public void addData(ConfigMapData configMapData) {
    data.add(configMapData);
    Collections.sort(data);
  }

  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return data.get(row).getKey();
    } else {
      return data.get(row).getValue();
    }
  }
}