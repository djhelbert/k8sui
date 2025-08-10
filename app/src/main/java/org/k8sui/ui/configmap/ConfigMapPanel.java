package org.k8sui.ui.configmap;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.App;
import org.k8sui.model.ConfigMap;
import org.k8sui.model.ConfigMapData;
import org.k8sui.service.ConfigMapService;
import org.k8sui.ui.NameSpaceListPanel;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Updated;
import org.k8sui.ui.Util;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConfigMapPanel extends JPanel implements ActionListener, ListSelectionListener, Updated {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new JButton("Add");
    private JTable table;
    private ConfigMapModel model;
    private final ConfigMapService service = new ConfigMapService();
    private JTable dataTable;
    private final ConfigMapDataModel dataModel = new ConfigMapDataModel(new ArrayList<>());
    private final JButton deleteButton = new JButton("Delete");
    private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);

    public ConfigMapPanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new ConfigMapModel(service.configMapList(nameSpaceListPanel.getNamespace()));
        } catch (ApiException err) {
            err.printStackTrace();
        }

        // Setup add button
        addButton.setIcon(Util.getImageIcon("add.png"));
        addButton.addActionListener(this);
        deleteButton.setIcon(Util.getImageIcon("delete.png"));
        deleteButton.addActionListener(this);
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
        table.getSelectionModel().addListSelectionListener(this);
        dataTable = new JTable(dataModel);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        var scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Data"));

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    @Override
    public void update() {
        table.clearSelection();

        try {
            model.setMaps(service.configMapList(nameSpaceListPanel.getNamespace()));
        } catch (ApiException ex) {
            Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }

        model.fireTableDataChanged();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)) {
            try {
                model.setMaps(service.configMapList(nameSpaceListPanel.getNamespace()));
                model.fireTableDataChanged();
            } catch (ApiException ex) {
                Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
            }
        }
        else if (e.getSource().equals(deleteButton)) {
            int row = table.getSelectedRow();

            if(row != -1) {
                try {
                    service.deleteConfigMap(model.getConfigMap(row).getName(), nameSpaceListPanel.getNamespace());
                } catch (ApiException ex) {
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }
            }

            update();
        }
        else if (e.getSource().equals(addButton)) {
            // Create the dialog
            var dialog = new JDialog(App.frame(), "Add Config Map", true);
            dialog.setLayout(new FlowLayout());

            // Create text field
            var nameField = new JTextField(10);
            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);

            var keyField = new JTextField(10);
            dialog.add(new JLabel("Key:"));
            dialog.add(keyField);

            var valueField = new JTextField(10);
            dialog.add(new JLabel("Value:"));
            dialog.add(valueField);

            // Create OK and Cancel buttons
            var okButton = new JButton("OK");
            var cancelButton = new JButton("Cancel");

            // OK button action
            okButton.addActionListener(e1 -> {
                if(!NameValidator.validName(nameField.getText())) {
                    Util.showError(this, "Invalid Name", "Validation Error");
                    return;
                }

                try {
                    var newMap = new ConfigMap();
                    newMap.setCreationDate(OffsetDateTime.now());
                    newMap.setNameSpace(nameSpaceListPanel.getNamespace());
                    newMap.setName(nameField.getText());
                    newMap.setData(List.of(new ConfigMapData(keyField.getText(), valueField.getText())));

                    service.createConfigMap(newMap);
                    update();
                } catch (ApiException ex) {
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
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
            dataModel.setData(model.getConfigMap(row).getData());
            dataModel.fireTableDataChanged();
        } else {
            dataModel.setData(new ArrayList<>());
            dataModel.fireTableDataChanged();
        }
    }
}