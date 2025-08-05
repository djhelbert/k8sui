package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.service.NodeService;

import javax.swing.*;
import java.awt.*;

public class NodePanel extends JPanel {
    JPanel buttonPanel = new JPanel();
    JButton refreshButton = new JButton("Refresh");
    JTable table;
    NodeModel nodeModel;
    NodeService service = new NodeService();

    public NodePanel() {
        super();
        init();
    }

    private void init() {
        try {
            nodeModel = new NodeModel(service.nodes());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        refreshButton.setIcon(Util.getImageIcon("undo.png"));

        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);

        table = new JTable(nodeModel);

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
