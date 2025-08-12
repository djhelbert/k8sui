package org.k8sui.ui.secret;

import io.kubernetes.client.openapi.ApiException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.model.Secret;
import org.k8sui.model.SecretData;
import org.k8sui.service.SecretService;
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
import java.util.Base64;
import java.util.List;

@Log4j2
public class SecretPanel extends JPanel implements ActionListener, ListSelectionListener, Updated {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new JButton("Add");
    private final JButton addDataButton = new JButton("Add Data");
    private JTable table;
    private SecretModel model;
    private final SecretService service = new SecretService();
    private final SecretDataModel dataModel = new SecretDataModel(new ArrayList<>());
    private final JButton deleteButton = new JButton("Delete");
    @Getter
    private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);

    public SecretPanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new SecretModel(service.secretList(nameSpaceListPanel.getNamespace()));
        } catch (ApiException err) {
            log.error("Node Panel", err);
        }

        // Setup add button
        addButton.setIcon(Util.getImageIcon("add.png"));
        addButton.addActionListener(this);
        deleteButton.setIcon(Util.getImageIcon("delete.png"));
        deleteButton.addActionListener(this);
        deleteButton.setEnabled(false);
        addDataButton.setIcon(Util.getImageIcon("add.png"));
        addDataButton.addActionListener(this);
        addDataButton.setEnabled(false);
        // Refresh button setup
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        refreshButton.addActionListener(this);
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(nameSpaceListPanel);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addDataButton);
        // Table setup
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);

        final JTable dataTable = new JTable(dataModel);
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
            model.setSecrets(service.secretList(nameSpaceListPanel.getNamespace()));
        } catch (ApiException ex) {
            Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }

        model.fireTableDataChanged();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)) {
            try {
                model.setSecrets(service.secretList(nameSpaceListPanel.getNamespace()));
                model.fireTableDataChanged();
            } catch (ApiException ex) {
                Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
            }
        } else if (e.getSource().equals(deleteButton)) {
            int row = table.getSelectedRow();

            if (row != -1) {
                try {
                    service.deleteSecret(model.getSecret(row).getName(), nameSpaceListPanel.getNamespace());
                } catch (ApiException ex) {
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }
            }

            update();
        } else if (e.getSource().equals(addDataButton)) {
            // Create the dialog
            var dialog = new JDialog(App.frame(), "Add Secret Data", true);
            dialog.setLayout(new FlowLayout());

            var keyField = new JTextField(10);
            dialog.add(new JLabel("Key:"));
            dialog.add(keyField);

            var valueField = new JTextField(10);
            dialog.add(new JLabel("Value:"));
            dialog.add(valueField);

            // Create OK and Cancel buttons
            var okButton = new JButton("OK");
            var cancelButton = new JButton("Cancel");

            okButton.addActionListener(e1 -> {
                try {
                    int row = table.getSelectedRow();
                    service.addSecret(model.getSecret(row).getName(), nameSpaceListPanel.getNamespace(), keyField.getText(), base64Encode(valueField.getText()));
                    dataModel.addData(new SecretData(keyField.getText(), base64Encode(valueField.getText())));
                    dataModel.fireTableDataChanged();
                    dialog.dispose();
                } catch (ApiException ex) {
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }
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
        } else if (e.getSource().equals(addButton)) {
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
                if (!NameValidator.validName(nameField.getText())) {
                    Util.showError(this, "Invalid Name", "Validation Error");
                    return;
                }

                try {
                    var newMap = new Secret();
                    newMap.setCreationDate(OffsetDateTime.now());
                    newMap.setNameSpace(nameSpaceListPanel.getNamespace());
                    newMap.setName(nameField.getText());
                    newMap.setData(List.of(new SecretData(keyField.getText(), base64Encode(valueField.getText()))));

                    service.createSecret(newMap);
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
            dataModel.setData(model.getSecret(row).getData());
            dataModel.fireTableDataChanged();
            deleteButton.setEnabled(true);
            addDataButton.setEnabled(true);
        } else {
            dataModel.setData(new ArrayList<>());
            dataModel.fireTableDataChanged();
            deleteButton.setEnabled(false);
            addDataButton.setEnabled(false);
        }
    }

    public byte[] base64Encode(String text) {
        return Base64.getEncoder().encode(text.getBytes());
    }
}