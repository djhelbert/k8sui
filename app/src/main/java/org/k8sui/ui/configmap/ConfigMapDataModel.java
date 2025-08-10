package org.k8sui.ui.configmap;

import org.k8sui.model.ConfigMapData;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

/**
 * Config Map Data Table Model
 */
public class ConfigMapDataModel extends AbstractTableModel {

    private static final String[] headers = {"Key", "Value"};
    private List<ConfigMapData> data;

    public ConfigMapDataModel(List<ConfigMapData> data) {
        setData(data);
    }

    public void setData(List<ConfigMapData> data) {
        this.data = data;
        Collections.sort(data);
    }

    public ConfigMapData getConfigMapData(int index) {
        return data.get(index);
    }

    @Override
    public int getRowCount() {
        return data.size();
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
            return data.get(row).getKey();
        } else {
            return data.get(row).getValue();
        }
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}