package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.App;
import org.k8sui.model.Service;
import org.k8sui.model.ServicePort;
import org.k8sui.service.ServiceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicePanel extends JPanel implements ActionListener  {
    JPanel buttonPanel = new JPanel();
    JButton refreshButton = new JButton("Refresh");
    JButton addButton = new JButton("Add");
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

        // Setup add button
        addButton.setIcon(Util.getImageIcon("add.png"));
        addButton.addActionListener(this);
        // Refresh button setup
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        refreshButton.addActionListener(this);
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        // Table setup
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setMaxWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        setLayout(new BorderLayout());

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void update() {
        table.clearSelection();

        try {
            model.setServices(service.services("default"));
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        model.fireTableDataChanged();
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

        if (e.getSource().equals(addButton)) {
            // Create the dialog
            var dialog = new JDialog(App.frame(), "Add Service", true);
            dialog.setLayout(new FlowLayout());

            // Create text field
            var nameField = new JTextField(10);
            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);

            JComboBox<String> types = new JComboBox<>(new String[] {"ClusterIP", "NodePort", "LoadBalancer"});
            types.setSelectedIndex(1);
            dialog.add(types);

            var selectorField = new JTextField(20);
            dialog.add(new JLabel("Selector:"));
            dialog.add(selectorField);

            var portField = new JTextField("80", 4);
            dialog.add(new JLabel("Port:"));
            dialog.add(portField);

            var targetPortField = new JTextField("80", 4);
            dialog.add(new JLabel("Target Port:"));
            dialog.add(targetPortField);

            var nodePortField = new JTextField("30080", 4);
            dialog.add(new JLabel("Node Port:"));
            dialog.add(nodePortField);

            // Create OK and Cancel buttons
            var okButton = new JButton("OK");
            var cancelButton = new JButton("Cancel");

            // OK button action
            okButton.addActionListener(e1 -> {
                // contain at most 63 characters
                // contain only lowercase alphanumeric characters or '-'
                // start with an alphanumeric character
                // end with an alphanumeric character
                try {
                    Map<String,String> map = new HashMap<>();
                    map.put("app", selectorField.getText());

                    var newService = new Service(null, nameField.getText(), "default");
                    newService.setSelectors(map);
                    newService.setType(types.getSelectedItem().toString());

                    var servicePort = new ServicePort();
                    servicePort.setPort(Integer.valueOf(portField.getText()));
                    servicePort.setTargetPort(Integer.valueOf(targetPortField.getText()));
                    servicePort.setProtocol("TCP");
                    servicePort.setNodePort(Integer.valueOf(nodePortField.getText()));

                    newService.setServicePorts(List.of(servicePort));

                    service.addService(newService);
                    update();
                } catch (ApiException ex) {
                    throw new RuntimeException(ex);
                }

                dialog.dispose();
            });

            // Cancel button action
            cancelButton.addActionListener(e1 -> dialog.dispose());

            // Add buttons to dialog
            dialog.add(okButton);
            dialog.add(cancelButton);

            // Center the dialog relative to the frame
            dialog.pack();
            Util.centerComponent(dialog);
            dialog.setVisible(true);
        }
    }
}
