package org.k8sui.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Status Table Cell Renderer
 */
public class StatusTableCellRenderer extends DefaultTableCellRenderer {

    private static final Color DARK_GREEN = new Color(51, 102, 0);

    @Override
    public Component getTableCellRendererComponent(JTable t, Object o, boolean selected, boolean focus, int r, int c) {
        Component comp = super.getTableCellRendererComponent(t, o, selected, focus, r, c);
        String s = t.getModel().getValueAt(r, c).toString();

        if (s.equalsIgnoreCase("active")) {
            comp.setForeground(DARK_GREEN);
        } else if (s.equalsIgnoreCase("terminating")) {
            comp.setForeground(Color.RED);
        } else {
            comp.setForeground(Color.BLACK);
        }

        return comp;
    }
}
