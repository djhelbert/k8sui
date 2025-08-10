package org.k8sui.ui.deployment;

import org.k8sui.model.Container;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

/**
 * Container Table Model
 */
public class ContainerModel extends AbstractTableModel {

    private static final String[] headers = {"Name", "Image", "Ports"};

    private List<Container> containers;

    public ContainerModel(List<Container> containers) {
        setContainers(containers);
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
        Collections.sort(containers);
    }

    @Override
    public int getRowCount() {
        return containers.size();
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
            return containers.get(row).getName();
        } else if (col == 1) {
            return containers.get(row).getImage();
        }

        if (containers.get(row).getPorts() != null) {
            return containers.get(row).getPorts().toString();
        } else {
            return "";
        }
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}