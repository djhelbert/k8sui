package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.service.NameSpaceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NameSpacePanel extends JPanel implements ActionListener {
    JPanel buttonPanel = new JPanel();
    JButton refreshButton = new JButton("Refresh");
    JTable table;
    NameSpaceModel nameSpaceModel;
    NameSpaceService service = new NameSpaceService();

    public NameSpacePanel() {
        super();
        init();
    }

    private void init() {
        try {
            nameSpaceModel = new NameSpaceModel(service.nameSpaces());
        } catch (ApiException err) {
            throw new RuntimeException(err);
        }

        // Refresh button setup
        refreshButton.addActionListener(this);
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);

        table = new JTable(nameSpaceModel);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)) {
            try {
                nameSpaceModel.setNodes(service.nameSpaces());
                nameSpaceModel.fireTableDataChanged();
            } catch (ApiException err) {
                throw new RuntimeException(err);
            }
        }
    }
}
