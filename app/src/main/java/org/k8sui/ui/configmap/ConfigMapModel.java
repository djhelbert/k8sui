package org.k8sui.ui.configmap;

import org.k8sui.model.ConfigMap;
import org.k8sui.ui.BaseTableModel;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Config Map Table Model
 */
public class ConfigMapModel extends BaseTableModel {

    private static final String[] headers = {"UID", "Name", "Namespace", "Created"};
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<ConfigMap> maps;

    public ConfigMapModel(List<ConfigMap> maps) {
        super(headers);
        setMaps(maps);
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
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return maps.get(row).getUid();
        } else if (col == 1) {
            return maps.get(row).getName();
        } else if (col == 2) {
            return maps.get(row).getNameSpace();
        } else {
            if (maps.get(row).getCreationDate() == null) {
                return "";
            }
            return formatter.format(maps.get(row).getCreationDate());
        }
    }
}