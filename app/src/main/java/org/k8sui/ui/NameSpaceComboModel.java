package org.k8sui.ui;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.Collections;
import java.util.List;

public class NameSpaceComboModel implements ComboBoxModel<String> {
    @Setter
    private List<String> names;
    private Object selectedItem;

    public NameSpaceComboModel(List<String> nameSpaces) {
        this.names = nameSpaces;
    }

    public void addNameSpace(String name) {
        names.add(name);
        Collections.sort(names);
    }

    public void removeNameSpace(String name) {
        names.remove(name);
        Collections.sort(names);
    }

    public void setSelectedItem(int index) {
        selectedItem = names.get(index);
    }

    @Override
    public void setSelectedItem(Object o) {
        selectedItem = o;
    }

    @Nullable
    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getSize() {
        return names.size();
    }

    @Override
    public String getElementAt(int index) {
        return names.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
    }
}
