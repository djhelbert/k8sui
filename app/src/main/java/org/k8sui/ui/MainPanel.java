package org.k8sui.ui;

import org.k8sui.ui.deployment.DeploymentPanel;
import org.k8sui.ui.namespace.NameSpacePanel;
import org.k8sui.ui.node.NodePanel;
import org.k8sui.ui.service.ServicePanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class MainPanel extends JPanel {

    public MainPanel() {
        super();
        init();
    }

    private void init() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setLayout(new BorderLayout(5,5));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nodes", new NodePanel());
        tabbedPane.addTab("Namespaces", new NameSpacePanel());
        tabbedPane.addTab("Services", new ServicePanel());
        tabbedPane.addTab("Deployments", new DeploymentPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }
}
