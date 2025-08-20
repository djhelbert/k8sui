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
import org.k8sui.model.PersistentVolumeClaim;
import org.k8sui.ui.BaseTableModel;

/**
 * Persistent Volume Claim Model
 */
public class PersistentVolumeClaimModel extends BaseTableModel {

  private static final String[] headers = {"UID", "Name", "Namespace", "Storage Class",
      "Creation Date", "Status", "Access Modes"};
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
    } else if (col == 4) {
      return persistentVolumeClaims.get(row).getCreation().toString();
    } else if (col == 5) {
      return persistentVolumeClaims.get(row).getStatus();
    }

    return persistentVolumeClaims.get(row).getAccessModes().toString();
  }
}