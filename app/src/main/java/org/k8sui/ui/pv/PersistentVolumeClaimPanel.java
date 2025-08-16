package org.k8sui.ui.pv;

import io.kubernetes.client.openapi.ApiException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.model.PersistentVolumeClaim;
import org.k8sui.service.PersistentVolumeClaimService;
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
public class PersistentVolumeClaimPanel extends JPanel implements ActionListener, ListSelectionListener, Updated {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new JButton("Add");
    private final JButton deleteButton = new JButton("Delete");
    private JTable table;
    private PersistentVolumeClaimModel model;
    private final MapTableModel mapTableModel = new MapTableModel();
    private final PersistentVolumeClaimService service = new PersistentVolumeClaimService();
    @Getter
    private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);

    public PersistentVolumeClaimPanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new PersistentVolumeClaimModel(service.listPersistentVolumeClaims(nameSpaceListPanel.getNamespace()));
        } catch (ApiException err) {
            log.error("PVC Panel", err);
            model = new PersistentVolumeClaimModel(new ArrayList<>());
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

        var labelTable = new JTable(mapTableModel);
        labelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        var scrollPane = new JScrollPane(labelTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Labels"));

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    @Override
    public void update() {
        table.clearSelection();

        try {
            model.setPersistentVolumes(service.listPersistentVolumeClaims(nameSpaceListPanel.getNamespace()));
        } catch (ApiException err) {
            log.error("PVC Panel", err);
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
                PersistentVolumeClaim pvc = model.getPersistentVolumeClaim(row);

                try {
                    service.deletePersistentVolume(pvc.getName());
                } catch (ApiException ex) {
                    log.error("PVC Panel", ex);
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }
                update();
            }
        }
        if (e.getSource().equals(addButton)) {
            // Create the dialog
            JDialog dialog = new JDialog(App.frame(), "Add Persistent Volume Claim", true);
            dialog.setLayout(new FlowLayout());

            // Create text field
            JTextField nameField = new JTextField(10);
            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);

            // Create text field
            JTextField capacityField = new JTextField("1Gi", 3);
            dialog.add(new JLabel("Capacity:"));
            dialog.add(capacityField);

            JTextField storageClassField = new JTextField("standard", 20);
            dialog.add(new JLabel("Storage Class Name:"));
            dialog.add(storageClassField);

            JComboBox<String> accessMode = new JComboBox<>(new String[]{"ReadWriteOnce", "ReadOnlyMany", "ReadWriteMany", "ReadWriteOncePod"});
            accessMode.setSelectedIndex(0);
            dialog.add(new JLabel("Access Mode:"));
            dialog.add(accessMode);

            JTextField reclaimField = new JTextField("Retain", 8);
            dialog.add(new JLabel("Reclaim Policy:"));
            dialog.add(reclaimField);

            // Create OK and Cancel buttons
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            // OK button action
            okButton.addActionListener(e1 -> {
                String nameFieldText = nameField.getText();

                if (!NameValidator.validName(nameField.getText())) {
                    Util.showError(this, "Invalid Name", "Validation Error");
                    return;
                }

                try {
                    final Map<String, String> map = new HashMap<>();
                    map.put("requests", capacityField.getText());

                    PersistentVolumeClaim newPV = new PersistentVolumeClaim();
                    newPV.setName(nameField.getText());
                    newPV.setStorageClassName(storageClassField.getText());
                    newPV.setAccessModes(List.of(accessMode.getSelectedItem().toString()));
                    newPV.setResources(map);

                    service.createPersistentVolumeClaim(newPV);
                    update();
                } catch (ApiException ex) {
                    log.error("PVC Panel", ex);
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
            mapTableModel.setList(model.getPersistentVolumeClaim(row).getLabels());
            mapTableModel.fireTableDataChanged();
        } else {
            mapTableModel.setList(new HashMap<>());
            mapTableModel.fireTableDataChanged();
        }
    }
}
