package org.k8sui.ui.node;

import org.k8sui.model.Node;
import org.k8sui.ui.BaseTableModel;

import java.util.Collections;
import java.util.List;

public class NodeModel extends BaseTableModel {

    private static final String[] headers = {"UID", "Name", "OS Image", "CPU", "Memory", "Internal IP"};
    private List<Node> nodes;

    public NodeModel(List<Node> nodes) {
        super(headers);
        setNodes(nodes);
    }

    public Node getNode(int row) {
        return nodes.get(row);
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
}