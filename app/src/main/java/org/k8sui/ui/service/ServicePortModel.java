package org.k8sui.ui.service;

import org.k8sui.model.ServicePort;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

/**
 * Service Port Table Model
 */
public class ServicePortModel extends AbstractTableModel {

    private static final String[] headers = {"Name", "Protocol", "Port", "Target Port", "Node Port"};

    private List<ServicePort> servicePorts;

    public ServicePortModel(List<ServicePort> servicePorts) {
        setServicePorts(servicePorts);
    }

    public void setServicePorts(List<ServicePort> servicePorts) {
        this.servicePorts = servicePorts;
        Collections.sort(servicePorts);
    }

    @Override
    public int getRowCount() {
        return servicePorts.size();
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
            return servicePorts.get(row).getName();
        } else if (col == 1) {
            return servicePorts.get(row).getProtocol();
        } else if (col == 2) {
            return defaultNull(servicePorts.get(row).getPort());
        } else if (col == 3) {
            return defaultNull(servicePorts.get(row).getTargetPort());
        } else {
            return defaultNull(servicePorts.get(row).getNodePort());
        }
    }

    private String defaultNull(Integer value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}
