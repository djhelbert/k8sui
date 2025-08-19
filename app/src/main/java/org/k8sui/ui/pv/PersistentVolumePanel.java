package org.k8sui.ui.pv;

import io.kubernetes.client.openapi.ApiException;
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.model.PersistentVolume;
import org.k8sui.service.PersistentVolumeService;
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
public class PersistentVolumePanel extends JPanel implements ActionListener, ListSelectionListener, Updated {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new JButton("Add");
    private final JButton deleteButton = new JButton("Delete");
    private JTable table;
    private PersistentVolumeModel model;
    private final MapTableModel mapTableModel = new MapTableModel();
    private final PersistentVolumeService service = new PersistentVolumeService();

    public PersistentVolumePanel() {
        super();
        init();
    }

    private void init() {
        try {
            model = new PersistentVolumeModel(service.listPersistentVolumes());
        } catch (ApiException err) {
            log.error("PV Panel", err);
            model = new PersistentVolumeModel(new ArrayList<>());
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
        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(1000);
        table.getColumnModel().getColumn(5).setMaxWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setMaxWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getSelectionModel().addListSelectionListener(this);

        JTable labelTable = new JTable(mapTableModel);
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
            model.setPersistentVolumes(service.listPersistentVolumes());
        } catch (ApiException err) {
            log.error("PV Panel", err);
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
                var pv = model.getPersistentVolume(row);

                try {
                    service.deletePersistentVolume(pv.getName());
                } catch (ApiException ex) {
                    log.error("PV Panel", ex);
                    Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
                }
                update();
            }
        }
        if (e.getSource().equals(addButton)) {
            // Create the dialog
            JDialog dialog = new JDialog(App.frame(), "Add Persistent Volume", true);
            dialog.setLayout(new BorderLayout(5,5));

            JPanel gridPanel = new JPanel(new GridLayout(8,2, 5,5));

            // Create text field
            JTextField nameField = new JTextField(10);
            gridPanel.add(new JLabel("Name:"));
            gridPanel.add(nameField);

            // Create text field
            JTextField capacityField = new JTextField("1Gi", 3);
            gridPanel.add(new JLabel("Capacity:"));
            gridPanel.add(capacityField);

            JTextField storageClassField = new JTextField("standard", 20);
            gridPanel.add(new JLabel("Storage Class Name:"));
            gridPanel.add(storageClassField);

            JComboBox<String> accessMode = new JComboBox<>(new String[]{"ReadWriteOnce", "ReadOnlyMany", "ReadWriteMany", "ReadWriteOncePod"});
            accessMode.setSelectedIndex(0);
            gridPanel.add(new JLabel("Access Mode:"));
            gridPanel.add(accessMode);

            JComboBox<String> reclaimField = new JComboBox<>(new String[]{"Delete", "Retain"});
            reclaimField.setSelectedIndex(0);
            gridPanel.add(new JLabel("Reclaim Policy:"));
            gridPanel.add(reclaimField);

            JTextField hostPathField = new JTextField("/tmp", 8);
            gridPanel.add(new JLabel("Host Path:"));
            gridPanel.add(hostPathField);

            JTextField keyField = new JTextField("", 8);
            gridPanel.add(new JLabel("Label Key:"));
            gridPanel.add(keyField);

            JTextField valueField = new JTextField("", 8);
            gridPanel.add(new JLabel("Label Value:"));
            gridPanel.add(valueField);

            dialog.add(gridPanel, BorderLayout.CENTER);

            // Create OK and Cancel buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            // OK button action
            okButton.addActionListener(e1 -> {
                String nameFieldText = nameField.getText();

                if (!NameValidator.validName(nameFieldText)) {
                    Util.showError(this, "Invalid Name", "Validation Error");
                    return;
                }
                if (!Util.isValidPath(hostPathField.getText())) {
                    Util.showError(this, "Invalid Host Path", "Validation Error");
                    return;
                }
                if (!Util.isQuantity(capacityField.getText())) {
                    Util.showError(this, "Invalid Capacity", "Validation Error");
                    return;
                }
                if (!KeyValidator.validName(keyField.getText())) {
                    Util.showError(this, "Invalid Key Name", "Validation Error");
                    return;
                }

                try {
                    final Map<String, String> capacityMap = new HashMap<>();
                    capacityMap.put("storage", capacityField.getText());

                    final Map<String, String> labelMap = new HashMap<>();
                    labelMap.put(keyField.getText(), valueField.getText());

                    var newPV = new PersistentVolume();
                    newPV.setName(nameField.getText());
                    newPV.setPersistentVolumeReclaimPolicy(reclaimField.getSelectedItem().toString());
                    newPV.setStorageClassName(storageClassField.getText());
                    newPV.setAccessModes(List.of(accessMode.getSelectedItem().toString()));
                    newPV.setCapacities(capacityMap);
                    newPV.setHostPath(hostPathField.getText());
                    newPV.setLabels(labelMap);

                    service.createPersistentVolume(newPV);
                    update();
                } catch (ApiException ex) {
                    log.error("PV Panel", ex);
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
            mapTableModel.setList(model.getPersistentVolume(row).getLabels());
            mapTableModel.fireTableDataChanged();
        } else {
            mapTableModel.setList(new HashMap<>());
            mapTableModel.fireTableDataChanged();
        }
    }
}
