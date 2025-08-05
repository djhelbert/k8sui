package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.service.NodeService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NodePanel extends JPanel implements ActionListener  {
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
        } catch (ApiException err) {
            throw new RuntimeException(err);
        }

        // Refresh button setup
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        refreshButton.addActionListener(this);
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);

        table = new JTable(nodeModel);

        setLayout(new BorderLayout());

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(refreshButton)) {
            try {
                nodeModel.setNodes(service.nodes());
                nodeModel.fireTableDataChanged();
            } catch (ApiException err) {
                throw new RuntimeException(err);
            }
        }
    }
}
