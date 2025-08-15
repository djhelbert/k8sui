package org.k8sui.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map Table Model
 */
public class MapTableModel extends BaseTableModel {
    private List<KeyValuePair> list = new ArrayList<>();

    public MapTableModel() {
        super(new String[]{"Key", "Value"});
    }

    public void setList(Map<String, String> map) {
        list = map.keySet().stream().map(k -> new KeyValuePair(k,map.get(k))).toList();
    }

    public Map<String, String> getMap() {
        final Map<String, String> map = new HashMap<>();
        for(KeyValuePair kvp : list) {
            map.put(kvp.getKey(), kvp.getValue());
        }
        return map;
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnIndex == 0 ? list.get(rowIndex).getKey() : list.get(rowIndex).getValue();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class KeyValuePair {
        String key;
        String value;
    }
}
