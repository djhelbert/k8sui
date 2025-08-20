package org.k8sui.ui.service;

import io.kubernetes.client.openapi.ApiException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.model.Service;
import org.k8sui.model.ServicePort;
import org.k8sui.service.ServiceService;
import org.k8sui.ui.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class ServicePanel extends JPanel implements ActionListener, ListSelectionListener, Updated {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new JButton("Add");
    private JTable table;
    private ServiceModel model;
    private final ServiceService service = new ServiceService();
    private final ServicePortModel servicePortModel = new ServicePortModel(new ArrayList<>());
    private final JButton deleteButton = new JButton("Delete");
    @Getter
    private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);
    private static final String CLUSTER_IP = "ClusterIP";
    private static final String NODE_PORT = "NodePort";
    private static final String LB = "LoadBalancer";

    public ServicePanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new ServiceModel(service.services(nameSpaceListPanel.getNamespace()));
        } catch (ApiException err) {
            log.error("Service Panel", err);
            model = new ServiceModel(new ArrayList<>());
        }

        // Setup add button
        addButton.setIcon(Util.getImageIcon("add.png"));
        addButton.addActionListener(this);
        deleteButton.setIcon(Util.getImageIcon("delete.png"));
        deleteButton.addActionListener(this);
        deleteButton.setEnabled(false);
        // Refresh button setup
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        refreshButton.addActionListener(this);
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(nameSpaceListPanel);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        // Table setup
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setMaxWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setMaxWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setMaxWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setMaxWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getSelectionModel().addListSelectionListener(this);

        // Setup service port table
        JTable servicePortTable = new JTable(servicePortModel);
        servicePortTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        var scrollPane = new JScrollPane(servicePortTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Service Ports"));

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    @Override
    public void update() {
        table.clearSelection();

        try {
            model.setServices(service.services(nameSpaceListPanel.getNamespace()));
        } catch (ApiException ex) {
            log.error("Service Panel", ex);
            Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }

        model.fireTableDataChanged();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)) {
            try {
                model.setServices(service.services(nameSpaceListPanel.getNamespace()));
                model.fireTableDataChanged();
            } catch (ApiException ex) {
                log.error("Service Panel", ex);
                Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
            }
        }
        else if (e.getSource().equals(deleteButton)) {
            int row = table.getSelectedRow();

            if(row != -1) {
                try {
                    service.deleteService(model.getService(row).getName(), nameSpaceListPanel.getNamespace());
                } catch (ApiException ex) {
                    log.error("Service Panel", ex);
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }
            }

            update();
        }
        else if (e.getSource().equals(addButton)) {
            // Create the dialog
            var dialog = new JDialog(App.frame(), "Add Service", true);
            dialog.setLayout(new FlowLayout());

            // Create text field
            var nameField = new JTextField(10);
            dialog.add(new JLabel("Service Name:"));
            dialog.add(nameField);

            dialog.add(new JLabel("Type:"));
            JComboBox<String> types = new JComboBox<>(new String[]{CLUSTER_IP, NODE_PORT, LB});
            types.setSelectedIndex(0);
            dialog.add(types);

            var selectorField = new JTextField(20);
            dialog.add(new JLabel("Selector (app):"));
            dialog.add(selectorField);

            var portField = new JTextField("80", 4);
            dialog.add(new JLabel("Service Port:"));
            dialog.add(portField);

            var targetPortField = new JTextField("80", 4);
            dialog.add(new JLabel("Target Port:"));
            dialog.add(targetPortField);

            var nodePortField = new JTextField("", 4);
            dialog.add(new JLabel("Node Port:"));
            dialog.add(nodePortField);

            // Create OK and Cancel buttons
            var okButton = new JButton("OK");
            var cancelButton = new JButton("Cancel");

            // Add buttons to dialog
            dialog.add(okButton);
            dialog.add(cancelButton);

            // OK button action
            okButton.addActionListener(e1 -> {
                if(!NameValidator.validName(nameField.getText())) {
                    Util.showError(this, "Invalid Name", "Validation Error");
                    return;
                }

                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("app", selectorField.getText());

                    var newService = new Service(null, nameField.getText(), nameSpaceListPanel.getNamespace());
                    newService.setSelectors(map);
                    newService.setType(types.getSelectedItem() == null ? null : types.getSelectedItem().toString());

                    var servicePort = new ServicePort();
                    servicePort.setProtocol("TCP");
                    servicePort.setName("port");

                    if (portField.getText() != null && !portField.getText().isEmpty()) {
                        servicePort.setPort(Integer.valueOf(portField.getText()));
                    }

                    if (targetPortField.getText() != null && !targetPortField.getText().isEmpty()) {
                        servicePort.setTargetPort(Integer.valueOf(targetPortField.getText()));
                    }

                    if(NODE_PORT.equals(newService.getType())) {
                        if (nodePortField.getText() != null && !nodePortField.getText().isEmpty()) {
                            servicePort.setNodePort(Integer.valueOf(nodePortField.getText()));
                        }
                    } else {
                        servicePort.setNodePort(null);
                    }

                    newService.setServicePorts(List.of(servicePort));

                    service.addService(newService);
                    update();
                } catch (ApiException ex) {
                    log.error("Service Panel", ex);
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }

                dialog.dispose();
            });

            // Cancel button action
            cancelButton.addActionListener(e1 -> dialog.dispose());

            // Center the dialog relative to the frame
            dialog.pack();
            Util.centerComponent(dialog);
            dialog.setVisible(true);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = table.getSelectedRow();

        if (row != -1) {
            servicePortModel.setServicePorts(model.getService(row).getServicePorts());
            servicePortModel.fireTableDataChanged();
            deleteButton.setEnabled(true);
        } else {
            servicePortModel.setServicePorts(new ArrayList<>());
            servicePortModel.fireTableDataChanged();
            deleteButton.setEnabled(false);
        }
    }
}
