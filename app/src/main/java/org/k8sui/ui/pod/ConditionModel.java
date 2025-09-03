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
import org.k8sui.model.Condition;
import org.k8sui.ui.BaseTableModel;

public class ConditionModel extends BaseTableModel {

  private static final String[] headers = {"Type", "Status", "Updated", "Reason", "Message"};
  private List<Condition> conditions;

  /**
   * Constructor
   *
   * @param conditions Condition List
   */
  public ConditionModel(List<Condition> conditions) {
    super(headers);
    setConditions(conditions);
  }

  /**
   * Set Pod Conditions
   *
   * @param conditions Pod Condition List
   */
  public void setConditions(List<Condition> conditions) {
    this.conditions = conditions;
    Collections.sort(conditions);
  }

  @Override
  public int getRowCount() {
    return conditions.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return conditions.get(row).getType();
    } else if (col == 1) {
      return conditions.get(row).getStatus();
    } else if (col == 2) {
      return conditions.get(row).getStatus();
    } else if (col == 3) {
      return conditions.get(row).getUpdated().toString();
    } else if (col == 4) {
      return conditions.get(row).getReason();
    } else {
      return conditions.get(row).getMessage();
    }
  }
}
