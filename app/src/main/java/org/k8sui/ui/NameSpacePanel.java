package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.App;
import org.k8sui.model.NameSpace;
import org.k8sui.service.NameSpaceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NameSpacePanel extends JPanel implements ActionListener {
    JPanel buttonPanel = new JPanel();
    JButton refreshButton = new JButton("Refresh");
    JButton addButton = new JButton("Add");
    JButton deleteButton = new JButton("Delete");
    JTable table;
    NameSpaceModel model;
    NameSpaceService service = new NameSpaceService();

    public NameSpacePanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new NameSpaceModel(service.nameSpaces());
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
        buttonPanel.add(deleteButton);
        // Table setup
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.setDefaultRenderer(String.class, new StatusTableCellRenderer());

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void update() {
        table.clearSelection();

        try {
            model.setNodes(service.nameSpaces());
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
            if(row != -1) {
                NameSpace ns = model.get(row);
                try {
                    service.deleteNamespace(ns.getNamespace());
                } catch (ApiException ex) {
                    Util.showError(this, Util.getValue(ex.getResponseBody(),"reason"), "Error");
                }
                update();
            }
        }
        if (e.getSource().equals(addButton)) {
            // Create the dialog
            JDialog dialog = new JDialog(App.frame(), "Add Namespace", true);
            dialog.setLayout(new FlowLayout());

            // Create text field
            JTextField nameField = new JTextField(20);
            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);

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
                    service.createNamespace(input);
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
}
