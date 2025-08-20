/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.pv;

import java.util.Collections;
import java.util.List;
import org.k8sui.model.PersistentVolume;
import org.k8sui.ui.BaseTableModel;

/**
 * Persistent Volume Model
 */
public class PersistentVolumeModel extends BaseTableModel {

  private static final String[] headers = {"UID", "Name", "Capacity", "Storage Class", "Host Path",
      "Reclaim Policy", "Status", "Access Modes"};
  private List<PersistentVolume> persistentVolumes;

  /**
   * Constructor
   * @param PersistentVolumes Persistent Volume List
   */
  public PersistentVolumeModel(List<PersistentVolume> PersistentVolumes) {
    super(headers);
    setPersistentVolumes(PersistentVolumes);
  }

  public PersistentVolume getPersistentVolume(int row) {
    return persistentVolumes.get(row);
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
      return persistentVolumes.get(row).getCapacities().toString();
    } else if (col == 3) {
      return persistentVolumes.get(row).getStorageClassName();
    } else if (col == 4) {
      return persistentVolumes.get(row).getHostPath();
    } else if (col == 5) {
      return persistentVolumes.get(row).getPersistentVolumeReclaimPolicy();
    } else if (col == 6) {
      return persistentVolumes.get(row).getStatus();
    }

    return persistentVolumes.get(row).getAccessModes().toString();
  }
}