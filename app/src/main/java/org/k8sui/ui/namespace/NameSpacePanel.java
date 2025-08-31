/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.namespace;

import io.kubernetes.client.openapi.ApiException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
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
import lombok.extern.log4j.Log4j2;
import org.k8sui.App;
import org.k8sui.service.NameSpaceService;
import org.k8sui.ui.MapTableModel;
import org.k8sui.ui.NameSpaceObserver;
import org.k8sui.ui.NameSpaceOperation;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Util;

@Log4j2
public class NameSpacePanel extends JPanel implements ActionListener, ListSelectionListener {

  private final JPanel buttonPanel = new JPanel();
  private final JButton refreshButton = new JButton("Refresh");
  private final JButton addButton = new JButton("Add");
  private final JButton deleteButton = new JButton("Delete");
  JTable namespaceTable;
  NameSpaceModel nameSpaceModel;
  private final NameSpaceService service = new NameSpaceService();
  private final List<NameSpaceObserver> nameSpaceObservers = new ArrayList<>();
  private final MapTableModel labelTableModel = new MapTableModel();
  private final MapTableModel annotationTableModel = new MapTableModel();

  public NameSpacePanel() {
    super();
    init();
  }

  private void init() {
    try {
      nameSpaceModel = new NameSpaceModel(service.nameSpaces());
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
    namespaceTable = new JTable(nameSpaceModel);
    namespaceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    Util.tableColumnSize(namespaceTable, 3, 80);
    namespaceTable.setDefaultRenderer(String.class, new StatusTableCellRenderer());
    namespaceTable.getSelectionModel().addListSelectionListener(this);

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

    setLayout(new BorderLayout(5, 5));
    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(namespaceTable), BorderLayout.CENTER);
    add(southPanel, BorderLayout.SOUTH);
  }

  public void addNameSpaceObserver(NameSpaceObserver nso) {
    nameSpaceObservers.add(nso);
  }

  private void nameSpaceChange(NameSpaceOperation operation, String nameSpace) {
    for (NameSpaceObserver nso : nameSpaceObservers) {
      nso.nameSpaceChange(nameSpace, operation);
    }
  }

  private void update() {
    deleteButton.setEnabled(false);
    namespaceTable.clearSelection();

    try {
      nameSpaceModel.setNodes(service.nameSpaces());
    } catch (ApiException ex) {
      log.error("Name Space Panel", ex);
    }

    nameSpaceModel.fireTableDataChanged();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(refreshButton)) {
      update();
    }
    if (e.getSource().equals(deleteButton)) {
      int row = namespaceTable.getSelectedRow();

      if (row != -1) {
        var ns = nameSpaceModel.getNameSpace(row);

        try {
          service.deleteNamespace(ns.getNamespace());
        } catch (ApiException ex) {
          log.error("Name Space Panel", ex);
          Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }
        update();
        nameSpaceChange(NameSpaceOperation.REMOVE, ns.getNamespace());
      }
    }
    if (e.getSource().equals(addButton)) {
      // Create the dialog
      JDialog dialog = new JDialog(App.frame(), "Add Namespace", true);
      dialog.setLayout(new FlowLayout());

      JTextField nameField = new JTextField(20);
      dialog.add(new JLabel("Name:"));
      dialog.add(nameField);

      JTextField labelKeyField = new JTextField(20);
      dialog.add(new JLabel("Label Key:"));
      dialog.add(labelKeyField);

      JTextField labelValueField = new JTextField(20);
      dialog.add(new JLabel("Label Value:"));
      dialog.add(labelValueField);

      // Create OK and Cancel buttons
      JButton okButton = new JButton("OK");
      JButton cancelButton = new JButton("Cancel");

      // OK button action
      okButton.addActionListener(e1 -> {
        var nameSpace = nameField.getText();

        HashMap<String, String> map = new HashMap<>();
        map.put(labelKeyField.getText(), labelValueField.getText());

        if (!NameValidator.validName(nameField.getText())) {
          Util.showError(this, "Invalid Name", "Validation Error");
          return;
        }

        if (!NameValidator.validName(labelKeyField.getText())) {
          Util.showError(this, "Invalid Key Name", "Validation Error");
          return;
        }

        try {
          service.createNamespace(nameSpace, map);
          update();
          nameSpaceChange(NameSpaceOperation.ADD, nameSpace);
        } catch (ApiException ex) {
          log.error("Name Space Panel", ex);
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
    int row = namespaceTable.getSelectedRow();

    if (row == -1) {
      deleteButton.setEnabled(false);
    } else {
      deleteButton.setEnabled(
          !"default".equalsIgnoreCase(nameSpaceModel.getNameSpace(row).getNamespace()));
      labelTableModel.setList(nameSpaceModel.getNameSpace(row).getLabels());
      annotationTableModel.setList(nameSpaceModel.getNameSpace(row).getAnnotations());
    }
    labelTableModel.fireTableDataChanged();
    annotationTableModel.fireTableDataChanged();
  }
}
