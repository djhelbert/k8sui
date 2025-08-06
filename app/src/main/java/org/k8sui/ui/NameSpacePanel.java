package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.App;
import org.k8sui.service.NameSpaceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NameSpacePanel extends JPanel implements ActionListener {
    JPanel buttonPanel = new JPanel();
    JButton refreshButton = new JButton("Refresh");
    JButton addButton = new JButton("Add");
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

        // Add button setup
        addButton.setIcon(Util.getImageIcon("add.png"));
        addButton.addActionListener(this);
        // Refresh button setup
        refreshButton.addActionListener(this);
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        // Table setup
        table = new JTable(nameSpaceModel);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void update() {
        try {
            nameSpaceModel.setNodes(service.nameSpaces());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        nameSpaceModel.fireTableDataChanged();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)) {
            update();
        }
        if (e.getSource().equals(addButton)) {
            // Create the dialog
            JDialog dialog = new JDialog(App.frame(), "Add Namespace", true);
            dialog.setLayout(new FlowLayout());

            // Create text field
            JTextField textField = new JTextField(20);
            dialog.add(new JLabel("Name:"));
            dialog.add(textField);

            // Create OK and Cancel buttons
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            // OK button action
            okButton.addActionListener(e1 -> {
                String input = textField.getText();

                // contain at most 63 characters
                // contain only lowercase alphanumeric characters or '-'
                // start with an alphanumeric character
                // end with an alphanumeric character
                try {
                    service.createNamespace(input);
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
