package org.k8sui.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class MainPanel extends JPanel {
    static final JLabel label = new JLabel(" ");

    public MainPanel() {
        super();
        init();
    }

    public static void setLabelText(String text) {
        label.setText(text);
    }

    private void init() {
        label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nodes", new NodePanel());
        tabbedPane.addTab("Namespaces", new NameSpacePanel());
        tabbedPane.addTab("Deployments", new DeploymentPanel());
        tabbedPane.addTab("Services", new ServicePanel());
        add(tabbedPane, BorderLayout.CENTER);
        add(label, BorderLayout.SOUTH);
    }
}
