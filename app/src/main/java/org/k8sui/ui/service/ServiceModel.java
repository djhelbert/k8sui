package org.k8sui.ui.service;

import org.k8sui.model.Service;
import org.k8sui.ui.BaseTableModel;

import java.util.Collections;
import java.util.List;

/**
 * Service Model
 */
public class ServiceModel extends BaseTableModel {

    private static final String[] headers = {"UID", "Name", "Namespace", "Type", "Cluster IP", "Selectors"};
    private List<Service> services;

    public ServiceModel(List<Service> serviceList) {
        super(headers);
        setServices(serviceList);
    }

    public void setServices(List<Service> services) {
        this.services = services;
        Collections.sort(services);
    }

    public Service getService(int row) {
        return services.get(row);
    }

    @Override
    public int getRowCount() {
        return services.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return services.get(row).getUid();
        } else if (col == 1) {
            return services.get(row).getName();
        } else if (col == 2) {
            return services.get(row).getNamespace();
        } else if (col == 3) {
            return services.get(row).getType();
        } else if( col == 4) {
            return services.get(row).getClusterIp();
        }

        if (services.get(row).getSelectors() == null) {
            return "";
        } else {
            return services.get(row).getSelectors().toString();
        }
    }
}
