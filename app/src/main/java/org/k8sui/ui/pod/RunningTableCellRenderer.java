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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Status Table Cell Renderer
 */
public class RunningTableCellRenderer extends DefaultTableCellRenderer {

  private static final Color DARK_GREEN = new Color(51, 102, 0);

  @Override
  public Component getTableCellRendererComponent(JTable t, Object o, boolean selected,
      boolean focus, int r, int c) {
    var comp = super.getTableCellRendererComponent(t, o, selected, focus, r, c);
    var s = t.getModel().getValueAt(r, c).toString();

    if (s.equalsIgnoreCase("Running")) {
      comp.setForeground(DARK_GREEN);
    } else {
      comp.setForeground(Color.BLACK);
    }

    return comp;
  }
}
