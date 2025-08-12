package org.k8sui.ui.secret;

import org.k8sui.model.SecretData;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

/**
 * Secret Data Table Model
 */
public class SecretDataModel extends AbstractTableModel {

    private static final String[] headers = {"Key", "Value (Base64 Encoded)"};
    private List<SecretData> data;

    public SecretDataModel(List<SecretData> data) {
        setData(data);
    }

    public void setData(List<SecretData> data) {
        this.data = data;
        Collections.sort(data);
    }

    public void addData(SecretData secretData) {
        data.add(secretData);
        Collections.sort(data);
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
            return new String(data.get(row).getValue());
        }
    }

    @Override
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}