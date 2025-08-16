package org.k8sui.ui.namespace;

import io.kubernetes.client.openapi.ApiException;
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.model.NameSpace;
import org.k8sui.service.NameSpaceService;
import org.k8sui.ui.NameSpaceObserver;
import org.k8sui.ui.NameSpaceOperation;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Util;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
public class NameSpacePanel extends JPanel implements ActionListener, ListSelectionListener {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new JButton("Add");
    private final JButton deleteButton = new JButton("Delete");
    JTable table;
    NameSpaceModel model;
    private final NameSpaceService service = new NameSpaceService();
    private final List<NameSpaceObserver> nameSpaceObservers = new ArrayList<>();

    public NameSpacePanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new NameSpaceModel(service.nameSpaces());
        } catch (ApiException err) {
            log.error("Node Panel", err);
        }

        // Add button setup
        addButton.setIcon(Util.getImageIcon("add.png"));
        addButton.addActionListener(this);
        deleteButton.setIcon(Util.getImageIcon("delete.png"));
        deleteButton.addActionListener(this);
        deleteButton.setEnabled(false);
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
        table.getSelectionModel().addListSelectionListener(this);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void addNameSpaceObserver(NameSpaceObserver nso) {
        nameSpaceObservers.add(nso);
    }

    private void nameSpaceChange(NameSpaceOperation operation, String nameSpace) {
        for(NameSpaceObserver nso : nameSpaceObservers) {
            nso.nameSpaceChange(nameSpace, operation);
        }
    }

    private void update() {
        deleteButton.setEnabled(false);
        table.clearSelection();

        try {
            model.setNodes(service.nameSpaces());
        } catch (ApiException ex) {
            log.error("Name Space Panel", ex);
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
                nameSpaceChange(NameSpaceOperation.REMOVE, ns.getNamespace());
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
                var nameSpace = nameField.getText();

                if(!NameValidator.validName(nameField.getText())) {
                    Util.showError(this, "Invalid Name", "Validation Error");
                    return;
                }

                try {
                    service.createNamespace(nameSpace, new HashMap<>());
                    update();
                    nameSpaceChange(NameSpaceOperation.ADD, nameSpace);
                } catch (ApiException ex) {
                    log.error("Name Space Panel", ex);
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

        if(row == -1) {
            deleteButton.setEnabled(false);
        } else {
            deleteButton.setEnabled(!"default".equalsIgnoreCase(model.get(row).getNamespace()));
        }
    }
}
