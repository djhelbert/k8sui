package org.k8sui.ui.deployment;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.App;
import org.k8sui.model.Container;
import org.k8sui.model.ContainerPort;
import org.k8sui.model.Deployment;
import org.k8sui.service.DeploymentService;
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

public class DeploymentPanel extends JPanel implements ActionListener, ListSelectionListener, Updated {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new JButton("Add");
    private final JButton deleteButton = new JButton("Delete");
    private JTable table;
    private JTable containerTable;
    private DeploymentModel model;
    private final ContainerModel containerModel = new ContainerModel(new ArrayList<>());
    private final DeploymentService service = new DeploymentService();
    private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);

    public DeploymentPanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new DeploymentModel(service.listDeployments(nameSpaceListPanel.getNamespace()));
        } catch (ApiException err) {
            err.printStackTrace();
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
        buttonPanel.add(nameSpaceListPanel);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
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

    @Override
    public void update() {
        table.clearSelection();

        try {
            model.setDeployments(service.listDeployments(nameSpaceListPanel.getNamespace()));
        } catch (ApiException e) {
            e.printStackTrace();
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
                    service.deleteDeployment(dp.getNamespace(), dp.getName());
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

                if(!NameValidator.validName(nameField.getText())) {
                    Util.showError(this, "Invalid Name", "Validation Error");
                    return;
                }

                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("app", nameField.getText());

                    Deployment newDeployment = new Deployment(null, input, nameSpaceListPanel.getNamespace());
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
                    Util.showError(this, Util.getValue(ex.getResponseBody(),"reason"), "Error");
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
