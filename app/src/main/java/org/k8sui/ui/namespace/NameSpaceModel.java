package org.k8sui.ui.namespace;

import org.k8sui.model.NameSpace;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class NameSpaceModel extends AbstractTableModel {

    private static final String[] headers = {"UID", "Namespace", "Creation Date", "Status"};
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<NameSpace> namespaces;

    public NameSpaceModel(List<NameSpace> nodes) {
        setNodes(nodes);
    }

    public void setNodes(List<NameSpace> nodes) {
        this.namespaces = nodes;
        Collections.sort(nodes);
    }

    @Override
    public int getRowCount() {
        return namespaces.size();
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public String getColumnName(int col) {
        return headers[col];
    }

    public NameSpace get(int row) {
        return namespaces.get(row);
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return namespaces.get(row).getUid();
        } else if (col == 1) {
            return namespaces.get(row).getNamespace();
        } else if (col == 2) {
            if (namespaces.get(row).getCreation() == null) {
                return "";
            }

            return formatter.format(namespaces.get(row).getCreation());
        }

        return namespaces.get(row).getStatus();
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}