package org.k8sui.ui.configmap;

import org.k8sui.model.ConfigMap;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Deployment Table Model
 */
public class ConfigMapModel extends AbstractTableModel {

    private static final String[] headers = {"UID", "Name", "Namespace", "Created"};
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<ConfigMap> maps;

    public ConfigMapModel(List<ConfigMap> deploymentList) {
        setMaps(deploymentList);
    }

    public void setMaps(List<ConfigMap> maps) {
        this.maps = maps;
        Collections.sort(maps);
    }

    public ConfigMap getConfigMap(int index) {
        return maps.get(index);
    }

    @Override
    public int getRowCount() {
        return maps.size();
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
            return maps.get(row).getUid();
        } else if (col == 1) {
            return maps.get(row).getName();
        } else if (col == 2) {
            return maps.get(row).getNameSpace();
        } else {
            if (maps.get(row).getCreated() == null) {
                return "";
            }

            return formatter.format(maps.get(row).getCreated());
        }
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}