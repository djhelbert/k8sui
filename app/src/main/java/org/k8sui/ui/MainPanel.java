/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import org.k8sui.ui.configmap.ConfigMapPanel;
import org.k8sui.ui.deployment.DeploymentPanel;
import org.k8sui.ui.namespace.NameSpacePanel;
import org.k8sui.ui.node.NodePanel;
import org.k8sui.ui.pv.PersistentVolumeClaimPanel;
import org.k8sui.ui.pv.PersistentVolumePanel;
import org.k8sui.ui.secret.SecretPanel;
import org.k8sui.ui.service.ServicePanel;

/**
 * Main Panel
 */
public class MainPanel extends JPanel {

  public MainPanel() {
    super();
    init();
  }

  private void init() {
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    setLayout(new BorderLayout(5, 5));

    var tabbedPane = new JTabbedPane();
    var nameSpacePanel = new NameSpacePanel();
    var nodePanel = new NodePanel();
    var configMapPanel = new ConfigMapPanel();
    var secretsPanel = new SecretPanel();
    var servicePanel = new ServicePanel();
    var deploymentPanel = new DeploymentPanel();
    var persistentVolumePanel = new PersistentVolumePanel();
    var persistentVolumeClaimPanel = new PersistentVolumeClaimPanel();

    nameSpacePanel.addNameSpaceObserver(configMapPanel.getNameSpaceListPanel());
    nameSpacePanel.addNameSpaceObserver(secretsPanel.getNameSpaceListPanel());
    nameSpacePanel.addNameSpaceObserver(servicePanel.getNameSpaceListPanel());
    nameSpacePanel.addNameSpaceObserver(deploymentPanel.getNameSpaceListPanel());
    nameSpacePanel.addNameSpaceObserver(persistentVolumeClaimPanel.getNameSpaceListPanel());

    tabbedPane.addTab("Nodes", nodePanel);
    tabbedPane.addTab("Namespaces", nameSpacePanel);
    tabbedPane.addTab("Persistent Volumes", persistentVolumePanel);
    tabbedPane.addTab("Persistent Volume Claims", persistentVolumeClaimPanel);
    tabbedPane.addTab("Config Maps", configMapPanel);
    tabbedPane.addTab("Secrets", secretsPanel);
    tabbedPane.addTab("Deployments", deploymentPanel);
    tabbedPane.addTab("Services", servicePanel);

    add(tabbedPane, BorderLayout.CENTER);
  }
}
