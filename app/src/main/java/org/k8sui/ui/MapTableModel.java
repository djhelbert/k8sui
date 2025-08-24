/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Map Table Model
 */
public class MapTableModel extends BaseTableModel {

  /**
   * Key Value Pair List
   */
  private List<KeyValuePair> list = new ArrayList<>();

  /**
   * Constructor
   */
  public MapTableModel() {
    super(new String[]{"Key", "Value"});
  }

  public void setList(Map<String, String> map) {
    list = map.keySet().stream().map(k -> new KeyValuePair(k, map.get(k))).toList();
  }

  @Override
  public int getRowCount() {
    return list.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return columnIndex == 0 ? list.get(rowIndex).getKey() : list.get(rowIndex).getValue();
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class KeyValuePair {

    String key;
    String value;
  }
}
