/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.deployment;

import java.util.Collections;
import java.util.List;
import org.k8sui.model.Deployment;
import org.k8sui.ui.BaseTableModel;

/**
 * Deployment Table Model
 */
public class DeploymentModel extends BaseTableModel {

  private static final String[] headers = {"UID", "Name", "Namespace", "Replicas", "Selector"};
  private List<Deployment> deployments;

  /**
   * Constructor
   * @param deploymentList Deployment List
   */
  public DeploymentModel(List<Deployment> deploymentList) {
    super(headers);
    setDeployments(deploymentList);
  }

  public void setDeployments(List<Deployment> deployments) {
    this.deployments = deployments;
    Collections.sort(deployments);
  }

  public Deployment getDeployment(int i) {
    return deployments.get(i);
  }

  @Override
  public int getRowCount() {
    return deployments.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    if (col == 0) {
      return deployments.get(row).getUid();
    } else if (col == 1) {
      return deployments.get(row).getName();
    } else if (col == 2) {
      return deployments.get(row).getNamespace();
    } else if (col == 3) {
      var replicaStatus = deployments.get(row).getReadyReplicas().toString();
      return replicaStatus + "/" + deployments.get(row).getReplicas();
    }

    if (deployments.get(row).getSelectors() == null) {
      return "";
    } else {
      return deployments.get(row).getSelectors().toString();
    }
  }
}
