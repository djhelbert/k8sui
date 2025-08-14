package org.k8sui.ui.pv;

import org.k8sui.model.PersistentVolume;
import org.k8sui.ui.BaseTableModel;

import java.util.Collections;
import java.util.List;

public class PersistentVolumeModel extends BaseTableModel {

    private static final String[] headers = {"UID", "Name", "Namespace", "Storage Class", "Host Path", "Reclaim Policy", "Access Modes"};
    private List<PersistentVolume> persistentVolumes;

    public PersistentVolumeModel(List<PersistentVolume> PersistentVolumes) {
        super(headers);
        setPersistentVolumes(PersistentVolumes);
    }

    public void setPersistentVolumes(List<PersistentVolume> PersistentVolumes) {
        this.persistentVolumes = PersistentVolumes;
        Collections.sort(PersistentVolumes);
    }

    @Override
    public int getRowCount() {
        return persistentVolumes.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return persistentVolumes.get(row).getUid();
        } else if (col == 1) {
            return persistentVolumes.get(row).getName();
        } else if (col == 2) {
            return persistentVolumes.get(row).getNameSpace();
        } else if (col == 3) {
            return persistentVolumes.get(row).getStorageClassName();
        } else if (col == 4) {
            return persistentVolumes.get(row).getHostPath();
        } else if (col == 5) {
            return persistentVolumes.get(row).getPersistentVolumeReclaimPolicy();
        }

        return persistentVolumes.get(row).getAccessModes().toString();
    }
}