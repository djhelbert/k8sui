package org.k8sui.ui.pv;

import org.k8sui.model.PersistentVolume;
import org.k8sui.model.PersistentVolumeClaim;
import org.k8sui.ui.BaseTableModel;

import java.util.Collections;
import java.util.List;

/**
 * Persistent Volume Claim Model
 */
public class PersistentVolumeClaimModel extends BaseTableModel {

    private static final String[] headers = {"UID", "Name", "Namespace", "Storage Class", "Access Modes"};
    private List<PersistentVolumeClaim> persistentVolumeClaims;

    public PersistentVolumeClaimModel(List<PersistentVolumeClaim> claims) {
        super(headers);
        setPersistentVolumes(claims);
    }

    public PersistentVolumeClaim getPersistentVolumeClaim(int row) {
        return persistentVolumeClaims.get(row);
    }

    public void setPersistentVolumes(List<PersistentVolumeClaim> claims) {
        this.persistentVolumeClaims = claims;
        Collections.sort(persistentVolumeClaims);
    }

    @Override
    public int getRowCount() {
        return persistentVolumeClaims.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            return persistentVolumeClaims.get(row).getUid();
        } else if (col == 1) {
            return persistentVolumeClaims.get(row).getName();
        } else if (col == 2) {
            return persistentVolumeClaims.get(row).getNameSpace();
        } else if (col == 3) {
            return persistentVolumeClaims.get(row).getStorageClassName();
        }

        return persistentVolumeClaims.get(row).getAccessModes().toString();
    }
}