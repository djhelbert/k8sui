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
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.model.PersistentVolumeClaim;
import org.k8sui.service.PersistentVolumeClaimService;
import org.k8sui.ui.KeyValidator;
import org.k8sui.ui.MapTableModel;
import org.k8sui.ui.NameSpaceListPanel;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Updated;
import org.k8sui.ui.Util;

@Log4j2
public class PersistentVolumeClaimPanel extends JPanel implements ActionListener,
    ListSelectionListener, Updated {

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
      model = new PersistentVolumeClaimModel(
          service.listPersistentVolumeClaims(nameSpaceListPanel.getNamespace()));
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
    table.getColumnModel().getColumn(3).setMaxWidth(110);
    table.getColumnModel().getColumn(3).setPreferredWidth(110);
    table.getColumnModel().getColumn(5).setMaxWidth(90);
    table.getColumnModel().getColumn(5).setPreferredWidth(90);
    table.getSelectionModel().addListSelectionListener(this);
    table.setDefaultRenderer(String.class, new BoundTableCellRenderer());

    var labelTable = new JTable(mapTableModel);
    labelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    var scrollPane = new JScrollPane(labelTable);
    scrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Labels"));

    setLayout(new BorderLayout());
    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(scrollPane, BorderLayout.SOUTH);
  }

  @Override
  public void update() {
    table.clearSelection();

    try {
      model.setPersistentVolumes(
          service.listPersistentVolumeClaims(nameSpaceListPanel.getNamespace()));
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
          service.deletePersistentVolumeClaim(nameSpaceListPanel.getNamespace(), pvc.getName());
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
      dialog.setLayout(new BorderLayout(5, 5));

      JPanel gridPanel = new JPanel(new GridLayout(7, 2, 5, 5));

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
        if (!Util.isQuantity(capacityField.getText())) {
          Util.showError(this, "Invalid Capacity", "Validation Error");
          return;
        }
        if (!KeyValidator.validName(keyField.getText())) {
          Util.showError(this, "Invalid Key Name", "Validation Error");
          return;
        }

        try {
          final Map<String, String> resourceMap = new HashMap<>();
          resourceMap.put("storage", capacityField.getText());

          final Map<String, String> labelMap = new HashMap<>();
          labelMap.put(keyField.getText(), valueField.getText());

          var volumeClaim = new PersistentVolumeClaim();
          volumeClaim.setName(nameField.getText());
          volumeClaim.setStorageClassName(storageClassField.getText());
          volumeClaim.setAccessModes(List.of(accessMode.getSelectedItem().toString()));
          volumeClaim.setCapacities(resourceMap);
          volumeClaim.setNameSpace(nameSpaceListPanel.getNamespace());
          volumeClaim.setLabels(labelMap);

          service.createPersistentVolumeClaim(volumeClaim);
          update();
        } catch (ApiException ex) {
          log.error("PVC Panel", ex);
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
      mapTableModel.setList(model.getPersistentVolumeClaim(row).getLabels());
      mapTableModel.fireTableDataChanged();
    } else {
      mapTableModel.setList(new HashMap<>());
      mapTableModel.fireTableDataChanged();
    }
  }
}
