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

import javax.swing.table.AbstractTableModel;

/**
 * Base Table Model
 */
public abstract class BaseTableModel extends AbstractTableModel {

  /**
   * Headers Array
   */
  final String[] headers;

  /**
   * Constructor
   *
   * @param headers Header Array
   */
  public BaseTableModel(String[] headers) {
    this.headers = headers;
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
  public Class<String> getColumnClass(int col) {
    return String.class;
  }
}
