/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.pv;

import io.kubernetes.client.openapi.ApiException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.model.PersistentVolume;
import org.k8sui.service.PersistentVolumeService;
import org.k8sui.ui.KeyValidator;
import org.k8sui.ui.MapTableModel;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Updated;
import org.k8sui.ui.Util;

@Log4j2
public class PersistentVolumePanel extends JPanel implements ActionListener, ListSelectionListener,
    Updated {

  private final JPanel buttonPanel = new JPanel();
  private final JButton refreshButton = new JButton("Refresh");
  private final JButton addButton = new JButton("Add");
  private final JButton deleteButton = new JButton("Delete");
  private JTable pvTable;
  private PersistentVolumeModel pvModel;
  private final MapTableModel labelTableModel = new MapTableModel();
  private final PersistentVolumeService service = new PersistentVolumeService();
  private final MapTableModel annotationTableModel = new MapTableModel();

  public PersistentVolumePanel() {
    super();
    init();
  }

  private void init() {
    try {
      pvModel = new PersistentVolumeModel(service.listPersistentVolumes());
    } catch (ApiException err) {
      log.error("PV Panel", err);
      pvModel = new PersistentVolumeModel(new ArrayList<>());
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
    pvTable = new JTable(pvModel);
    pvTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    Util.tableColumnSize(pvTable, 3, 100);
    Util.tableColumnSize(pvTable, 5, 120);
    Util.tableColumnSize(pvTable, 6, 90);
    pvTable.getSelectionModel().addListSelectionListener(this);
    pvTable.setDefaultRenderer(String.class, new BoundTableCellRenderer());

    var southPanel = new JPanel(new GridLayout(1, 2));

    var labelTable = new JTable(labelTableModel);
    labelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    var labelScrollPane = new JScrollPane(labelTable);
    labelScrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Labels"));

    var annotationTable = new JTable(annotationTableModel);
    annotationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    var annotationScrollPane = new JScrollPane(annotationTable);
    annotationScrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Annotations"));

    southPanel.add(labelScrollPane);
    southPanel.add(annotationScrollPane);

    setLayout(new BorderLayout());
    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(pvTable), BorderLayout.CENTER);
    add(southPanel, BorderLayout.SOUTH);
  }

  @Override
  public void update() {
    pvTable.clearSelection();

    try {
      pvModel.setPersistentVolumes(service.listPersistentVolumes());
    } catch (ApiException err) {
      log.error("PV Panel", err);
    }

    pvModel.fireTableDataChanged();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(refreshButton)) {
      update();
    }
    if (e.getSource().equals(deleteButton)) {
      int row = pvTable.getSelectedRow();

      if (row != -1) {
        var pv = pvModel.getPersistentVolume(row);

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
      var dialog = new JDialog(App.frame(), "Add Persistent Volume", true);
      dialog.setLayout(new BorderLayout(5, 5));

      var gridPanel = new JPanel(new GridLayout(8, 2, 5, 5));

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

      JComboBox<String> accessMode = new JComboBox<>(
          new String[]{"ReadWriteOnce", "ReadOnlyMany", "ReadWriteMany", "ReadWriteOncePod"});
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
      var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      var okButton = new JButton("OK");
      var cancelButton = new JButton("Cancel");
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
    int row = pvTable.getSelectedRow();

    if (row != -1) {
      labelTableModel.setList(pvModel.getPersistentVolume(row).getLabels());
      labelTableModel.fireTableDataChanged();
      annotationTableModel.setList(pvModel.getPersistentVolume(row).getAnnotations());
      annotationTableModel.fireTableDataChanged();
    } else {
      labelTableModel.setList(new HashMap<>());
      labelTableModel.fireTableDataChanged();
      annotationTableModel.setList(new HashMap<>());
      annotationTableModel.fireTableDataChanged();
    }
  }
}
