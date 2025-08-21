package org.k8sui.ui;

import javax.swing.table.AbstractTableModel;

/**
 * Base Table Model
 */
public abstract class BaseTableModel extends AbstractTableModel  {
    final String[] headers;

    public BaseTableModel(String[] headers) {
        this.headers = headers;
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
    public Class<String> getColumnClass(int col) {
        return String.class;
    }
}
