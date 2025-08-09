package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.App;
import org.k8sui.model.Container;
import org.k8sui.model.ContainerPort;
import org.k8sui.model.Deployment;
import org.k8sui.service.DeploymentService;

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

public class DeploymentPanel extends JPanel implements ActionListener, ListSelectionListener {
    JPanel buttonPanel = new JPanel();
    JButton refreshButton = new JButton("Refresh");
    JButton addButton = new JButton("Add");
    JButton deleteButton = new JButton("Delete");
    JTable table;
    JTable containerTable;
    DeploymentModel model;
    ContainerModel containerModel = new ContainerModel(new ArrayList<>());
    DeploymentService service = new DeploymentService();

    public DeploymentPanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new DeploymentModel(service.listDeployments("default"));
        } catch (ApiException err) {
            throw new RuntimeException(err);
        }

        // Add button setup
        addButton.setIcon(Util.getImageIcon("add.png"));
        addButton.addActionListener(this);
        deleteButton.setIcon(Util.getImageIcon("delete.png"));
        deleteButton.addActionListener(this);
        // Refresh button setup
        refreshButton.addActionListener(this);
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        // Table setup
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getSelectionModel().addListSelectionListener(this);
        containerTable = new JTable(containerModel);
        containerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(new JScrollPane(containerTable), BorderLayout.SOUTH);
    }

    private void update() {
        table.clearSelection();

        try {
            model.setDeployments(service.listDeployments("default"));
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        model.fireTableDataChanged();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)) {
            update();
        }
        if (e.getSource().equals(deleteButton)) {
            int row = table.getSelectedRow();
            if (row != -1) {
                Deployment dp = model.getDeployment(row);
                try {
                    service.deleteDeployment("default", dp.getName());
                } catch (ApiException ex) {
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }
                update();
            }
        }
        if (e.getSource().equals(addButton)) {
            // Create the dialog
            JDialog dialog = new JDialog(App.frame(), "Add Deployment", true);
            dialog.setLayout(new FlowLayout());

            // Create text field
            JTextField nameField = new JTextField(10);
            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);

            JTextField imageField = new JTextField(20);
            dialog.add(new JLabel("Image:"));
            dialog.add(imageField);

            JComboBox<Integer> replicas = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
            replicas.setSelectedIndex(2);
            dialog.add(replicas);

            JTextField portField = new JTextField("80", 4);
            dialog.add(new JLabel("Port:"));
            dialog.add(portField);

            // Create OK and Cancel buttons
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            // OK button action
            okButton.addActionListener(e1 -> {
                String input = nameField.getText();

                // contain at most 63 characters
                // contain only lowercase alphanumeric characters or '-'
                // start with an alphanumeric character
                // end with an alphanumeric character
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("app", nameField.getText());

                    Deployment newDeployment = new Deployment(null, input, "default");
                    newDeployment.setReplicas((Integer) replicas.getSelectedItem());
                    newDeployment.setLabels(map);
                    newDeployment.setSelectors(map);

                    Container container = new Container();
                    container.setName(nameField.getText());
                    container.setImage(imageField.getText());
                    container.setPorts(List.of(new ContainerPort(Integer.parseInt(portField.getText()))));
                    newDeployment.setContainers(List.of(container));

                    service.addDeployment(newDeployment);
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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            containerModel.setContainers(model.getDeployment(row).getContainers());
            containerModel.fireTableDataChanged();
        } else {
            containerModel.setContainers(new ArrayList<>());
            containerModel.fireTableDataChanged();
        }
    }
}
