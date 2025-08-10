package org.k8sui.ui.node;

import org.k8sui.model.Node;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

public class NodeModel extends AbstractTableModel {

    private static final String[] headers = {"UID", "Name", "OS Image", "CPU", "Memory", "Internal IP"};
    private List<Node> nodes;

    public NodeModel(List<Node> nodes) {
        setNodes(nodes);
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
        Collections.sort(nodes);
    }

    @Override
    public int getRowCount() {
        return nodes.size();
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
            return nodes.get(row).getUid();
        } else if (col == 1) {
            return nodes.get(row).getName();
        } else if (col == 2) {
            return nodes.get(row).getImage();
        } else if (col == 3) {
            return nodes.get(row).getCpu();
        } else if (col == 4) {
            return nodes.get(row).getMemory();
        }

        return nodes.get(row).getIp();
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}