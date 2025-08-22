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

import java.util.Collections;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class NameSpaceComboModel implements ComboBoxModel<String> {

  @Setter
  private List<String> names;
  private Object selectedItem;

  public NameSpaceComboModel(List<String> nameSpaces) {
    this.names = nameSpaces;
  }

  public void addNameSpace(String name) {
    names.add(name);
    Collections.sort(names);
  }

  public void removeNameSpace(String name) {
    names.remove(name);
    Collections.sort(names);
  }

  public void setSelectedItem(int index) {
    selectedItem = names.get(index);
  }

  @Override
  public void setSelectedItem(Object o) {
    selectedItem = o;
  }

  @Nullable
  @Override
  public Object getSelectedItem() {
    return selectedItem;
  }

  @Override
  public int getSize() {
    return names.size();
  }

  @Override
  public String getElementAt(int index) {
    return names.get(index);
  }

  @Override
  public void addListDataListener(ListDataListener l) {
  }

  @Override
  public void removeListDataListener(ListDataListener l) {
  }
}
