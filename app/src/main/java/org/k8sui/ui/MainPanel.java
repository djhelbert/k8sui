package org.k8sui.ui;

import org.k8sui.ui.configmap.ConfigMapPanel;
import org.k8sui.ui.deployment.DeploymentPanel;
import org.k8sui.ui.namespace.NameSpacePanel;
import org.k8sui.ui.node.NodePanel;
import org.k8sui.ui.pv.PersistentVolumeClaimPanel;
import org.k8sui.ui.pv.PersistentVolumePanel;
import org.k8sui.ui.secret.SecretPanel;
import org.k8sui.ui.service.ServicePanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

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
        setLayout(new BorderLayout(5,5));

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
        nameSpacePanel.addNameSpaceObserver(persistentVolumePanel.getNameSpaceListPanel());
        nameSpacePanel.addNameSpaceObserver(persistentVolumeClaimPanel.getNameSpaceListPanel());

        tabbedPane.addTab("Nodes", nodePanel);
        tabbedPane.addTab("Namespaces", nameSpacePanel);
        tabbedPane.addTab("Config Maps", configMapPanel);
        tabbedPane.addTab("Secrets", secretsPanel);
        tabbedPane.addTab("Persistent Volumes", persistentVolumePanel);
        tabbedPane.addTab("Persistent Volume Claims", persistentVolumeClaimPanel);
        tabbedPane.addTab("Deployments", deploymentPanel);
        tabbedPane.addTab("Services", servicePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
