package org.k8sui.ui;

import org.k8sui.model.Deployment;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

/**
 * Deployment Table Model
 */
public class DeploymentModel extends AbstractTableModel {

    private static final String[] headers = {"UID", "Name", "Namespace", "Replicas", "Selector"};
    private List<Deployment> deployments;

    public DeploymentModel(List<Deployment> deploymentList) {
        setDeployments(deploymentList);
    }

    public void setDeployments(List<Deployment> deployments) {
        this.deployments = deployments;
        Collections.sort(deployments);
    }

    public Deployment getDeployment(int i) {
        return deployments.get(i);
    }

    @Override
    public int getRowCount() {
        return deployments.size();
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
            return deployments.get(row).getUid();
        } else if (col == 1) {
            return deployments.get(row).getName();
        } else if (col == 2) {
            return deployments.get(row).getNamespace();
        } else if (col == 3) {
            var replicaStatus = deployments.get(row).getReadyReplicas().toString();
            return replicaStatus + "/" + deployments.get(row).getReplicas();
        }

        if (deployments.get(row).getSelectors() == null) {
            return "";
        } else {
            return deployments.get(row).getSelectors().toString();
        }
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}
