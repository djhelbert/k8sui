package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.service.ServiceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServicePanel extends JPanel implements ActionListener  {
    JPanel buttonPanel = new JPanel();
    JButton refreshButton = new JButton("Refresh");
    JTable table;
    ServiceModel model;
    ServiceService service = new ServiceService();

    public ServicePanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new ServiceModel(service.services("default"));
        } catch (ApiException err) {
            throw new RuntimeException(err);
        }

        // Refresh button setup
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        refreshButton.addActionListener(this);
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout());

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(refreshButton)) {
            try {
                model.setServices(service.services("default"));
                model.fireTableDataChanged();
            } catch (ApiException err) {
                throw new RuntimeException(err);
            }
        }
    }
}
