/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.configmap;

import io.kubernetes.client.openapi.ApiException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import org.k8sui.model.ConfigMap;
import org.k8sui.model.ConfigMapData;
import org.k8sui.service.ConfigMapService;
import org.k8sui.ui.KeyValidator;
import org.k8sui.ui.NameSpaceListPanel;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Updated;
import org.k8sui.ui.Util;

@Log4j2
public class ConfigMapPanel extends JPanel implements ActionListener, ListSelectionListener,
    Updated {

  private final JPanel buttonPanel = new JPanel();
  private final JButton refreshButton = new JButton("Refresh");
  private final JButton addButton = new JButton("Add");
  private final JButton addDataButton = new JButton("Add Data");
  private JTable table;
  private ConfigMapModel model;
  private final ConfigMapService service = new ConfigMapService();
  private final ConfigMapDataModel dataModel = new ConfigMapDataModel(new ArrayList<>());
  private final JButton deleteButton = new JButton("Delete");
  @Getter
  private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);

  public ConfigMapPanel() {
    super();
    init();
  }

  private void init() {
    try {
      model = new ConfigMapModel(service.configMapList(nameSpaceListPanel.getNamespace()));
    } catch (ApiException ex) {
      log.error("ConfigMap Panel", ex);
      model = new ConfigMapModel(new ArrayList<>());
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
    dataTable.getColumnModel().getColumn(0).setMaxWidth(250);
    dataTable.getColumnModel().getColumn(0).setPreferredWidth(250);
    var scrollPane = new JScrollPane(dataTable);
    scrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Data"));

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
    } else if (e.getSource().equals(deleteButton)) {
      int row = table.getSelectedRow();

      if (row != -1) {
        try {
          service.deleteConfigMap(model.getConfigMap(row).getName(),
              nameSpaceListPanel.getNamespace());
        } catch (ApiException ex) {
          log.error("ConfigMap Panel", ex);
          Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }
      }

      update();
    } else if (e.getSource().equals(addDataButton)) {
      // Create the dialog
      var dialog = new JDialog(App.frame(), "Add Config Map Data", true);
      dialog.setLayout(new FlowLayout());

      var keyField = new JTextField(10);
      dialog.add(new JLabel("Map Key:"));
      dialog.add(keyField);

      var valueField = new JTextField(10);
      dialog.add(new JLabel("Map Value:"));
      dialog.add(valueField);

      // Create OK and Cancel buttons
      var okButton = new JButton("OK");
      var cancelButton = new JButton("Cancel");

      okButton.addActionListener(e1 -> {
        if (!KeyValidator.validName(keyField.getText())) {
          Util.showError(this, "Invalid Key Name", "Validation Error");
          return;
        }

        try {
          int row = table.getSelectedRow();
          service.addData(model.getConfigMap(row).getName(), nameSpaceListPanel.getNamespace(),
              keyField.getText(), valueField.getText());
          dataModel.addData(new ConfigMapData(keyField.getText(), valueField.getText()));
          dataModel.fireTableDataChanged();
          dialog.dispose();
        } catch (ApiException ex) {
          log.error("ConfigMap Panel", ex);
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
      dialog.add(new JLabel("Map Key:"));
      dialog.add(keyField);

      var valueField = new JTextField(10);
      dialog.add(new JLabel("Map Value:"));
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

        if (!KeyValidator.validName(keyField.getText())) {
          Util.showError(this, "Invalid Key Name", "Validation Error");
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
          log.error("ConfigMap Panel", ex);
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
      deleteButton.setEnabled(true);
      addDataButton.setEnabled(true);
    } else {
      dataModel.setData(new ArrayList<>());
      dataModel.fireTableDataChanged();
      deleteButton.setEnabled(false);
      addDataButton.setEnabled(false);
    }
  }
}