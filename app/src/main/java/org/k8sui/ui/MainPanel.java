package org.k8sui.ui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class MainPanel extends JPanel {
    JLabel label = new JLabel(" ");

    public MainPanel() {
        super();
        init();
    }

    private void init() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Nodes", new NodePanel());
        tabbedPane.addTab("Namespaces", new NameSpacePanel());
        add(tabbedPane, BorderLayout.CENTER);
        add(label, BorderLayout.SOUTH);
    }
}
