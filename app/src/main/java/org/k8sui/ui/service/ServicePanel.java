/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.service;

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
import org.k8sui.model.Service;
import org.k8sui.model.ServicePort;
import org.k8sui.service.ServiceService;
import org.k8sui.ui.MapTableModel;
import org.k8sui.ui.NameSpaceListPanel;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Updated;
import org.k8sui.ui.Util;

@Log4j2
public class ServicePanel extends JPanel implements ActionListener, ListSelectionListener, Updated {

  private final JPanel buttonPanel = new JPanel();
  private final JButton refreshButton = new JButton("Refresh");
  private final JButton addButton = new JButton("Add");
  private JTable serviceTable;
  private ServiceModel serviceModel;
  private final MapTableModel labelTableModel = new MapTableModel();
  private final ServiceService service = new ServiceService();
  private final ServicePortModel servicePortModel = new ServicePortModel(new ArrayList<>());
  private final JButton deleteButton = new JButton("Delete");
  @Getter
  private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);
  private static final String CLUSTER_IP = "ClusterIP";
  private static final String NODE_PORT = "NodePort";
  private static final String LB = "LoadBalancer";

  /**
   * Constructor
   */
  public ServicePanel() {
    super();
    init();
  }

  private void init() {
    try {
      serviceModel = new ServiceModel(service.services(nameSpaceListPanel.getNamespace()));
    } catch (ApiException err) {
      log.error("Service Panel", err);
      serviceModel = new ServiceModel(new ArrayList<>());
    }

    // Setup add button
    addButton.setIcon(Util.getImageIcon("add.png"));
    addButton.addActionListener(this);
    deleteButton.setIcon(Util.getImageIcon("delete.png"));
    deleteButton.addActionListener(this);
    deleteButton.setEnabled(false);
    // Refresh button setup
    refreshButton.setIcon(Util.getImageIcon("undo.png"));
    refreshButton.addActionListener(this);
    // Button panel setup
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.add(nameSpaceListPanel);
    buttonPanel.add(refreshButton);
    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);
    // Table setup
    serviceTable = new JTable(serviceModel);
    serviceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    serviceTable.getColumnModel().getColumn(1).setMaxWidth(120);
    serviceTable.getColumnModel().getColumn(1).setPreferredWidth(120);
    serviceTable.getColumnModel().getColumn(2).setMaxWidth(110);
    serviceTable.getColumnModel().getColumn(2).setPreferredWidth(110);
    serviceTable.getColumnModel().getColumn(3).setMaxWidth(90);
    serviceTable.getColumnModel().getColumn(3).setPreferredWidth(90);
    serviceTable.getColumnModel().getColumn(4).setMaxWidth(110);
    serviceTable.getColumnModel().getColumn(4).setPreferredWidth(110);
    serviceTable.getSelectionModel().addListSelectionListener(this);

    var southPanel = new JPanel(new GridLayout(1, 2));

    // Setup service port table
    JTable servicePortTable = new JTable(servicePortModel);
    servicePortTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    var servicePortScrollPane = new JScrollPane(servicePortTable);
    servicePortScrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Service Ports"));

    var labelTable = new JTable(labelTableModel);
    labelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    var labelScrollPane = new JScrollPane(labelTable);
    labelScrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Labels"));

    southPanel.add(servicePortScrollPane);
    southPanel.add(labelScrollPane);

    setLayout(new BorderLayout());
    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(serviceTable), BorderLayout.CENTER);
    add(southPanel, BorderLayout.SOUTH);
  }

  @Override
  public void update() {
    serviceTable.clearSelection();

    try {
      serviceModel.setServices(service.services(nameSpaceListPanel.getNamespace()));
    } catch (ApiException ex) {
      log.error("Service Panel", ex);
      Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
    }

    serviceModel.fireTableDataChanged();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(refreshButton)) {
      try {
        serviceModel.setServices(service.services(nameSpaceListPanel.getNamespace()));
        serviceModel.fireTableDataChanged();
      } catch (ApiException ex) {
        log.error("Service Panel", ex);
        Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
      }
    } else if (e.getSource().equals(deleteButton)) {
      int row = serviceTable.getSelectedRow();

      if (row != -1) {
        try {
          service.deleteService(serviceModel.getService(row).getName(), nameSpaceListPanel.getNamespace());
        } catch (ApiException ex) {
          log.error("Service Panel", ex);
          Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }
      }

      update();
    } else if (e.getSource().equals(addButton)) {
      // Create the dialog
      var dialog = new JDialog(App.frame(), "Add Service", true);

      dialog.setLayout(new BorderLayout(5, 5));

      var gridPanel = new JPanel(new GridLayout(8, 2, 5, 5));
      var buttonPanel = new JPanel(new FlowLayout());

      // Create text field
      var nameField = new JTextField(9);
      gridPanel.add(new JLabel("Service Name:"));
      gridPanel.add(nameField);

      gridPanel.add(new JLabel("Type:"));
      JComboBox<String> types = new JComboBox<>(new String[]{CLUSTER_IP, NODE_PORT, LB});
      types.setSelectedIndex(0);
      gridPanel.add(types);

      var selectorField = new JTextField(10);
      gridPanel.add(new JLabel("Selector (app):"));
      gridPanel.add(selectorField);

      var portField = new JTextField("80", 4);
      gridPanel.add(new JLabel("Service Port:"));
      gridPanel.add(portField);

      var targetPortField = new JTextField("80", 4);
      gridPanel.add(new JLabel("Target Port:"));
      gridPanel.add(targetPortField);

      var nodePortField = new JTextField("", 4);
      gridPanel.add(new JLabel("Node Port:"));
      gridPanel.add(nodePortField);

      JTextField labelKeyField = new JTextField(20);
      gridPanel.add(new JLabel("Label Key:"));
      gridPanel.add(labelKeyField);

      JTextField labelValueField = new JTextField(20);
      gridPanel.add(new JLabel("Label Value:"));
      gridPanel.add(labelValueField);

      // Create OK and Cancel buttons
      var okButton = new JButton("OK");
      var cancelButton = new JButton("Cancel");

      // Add buttons to dialog
      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);
      dialog.add(buttonPanel, BorderLayout.SOUTH);
      dialog.add(gridPanel, BorderLayout.CENTER);

      // OK button action
      okButton.addActionListener(e1 -> {
        if (!NameValidator.validName(nameField.getText())) {
          Util.showError(this, "Invalid Name", "Validation Error");
          return;
        }

        if (!NameValidator.validName(labelKeyField.getText())) {
          Util.showError(this, "Invalid Key Name", "Validation Error");
          return;
        }

        try {
          Map<String, String> selectorMap = new HashMap<>();
          selectorMap.put("app", selectorField.getText());

          HashMap<String, String> labelMap = new HashMap<>();
          labelMap.put(labelKeyField.getText(), labelValueField.getText());

          var newService = new Service(null, nameField.getText(),
              nameSpaceListPanel.getNamespace());
          newService.setSelectors(selectorMap);
          newService.setLabels(labelMap);
          newService.setType(
              types.getSelectedItem() == null ? null : types.getSelectedItem().toString());

          var servicePort = new ServicePort();
          servicePort.setProtocol("TCP");
          servicePort.setName("port");

          if (portField.getText() != null && !portField.getText().isEmpty()) {
            servicePort.setPort(Integer.valueOf(portField.getText()));
          }

          if (targetPortField.getText() != null && !targetPortField.getText().isEmpty()) {
            servicePort.setTargetPort(Integer.valueOf(targetPortField.getText()));
          }

          if (NODE_PORT.equals(newService.getType())) {
            if (nodePortField.getText() != null && !nodePortField.getText().isEmpty()) {
              servicePort.setNodePort(Integer.valueOf(nodePortField.getText()));
            }
          } else {
            servicePort.setNodePort(null);
          }

          newService.setServicePorts(List.of(servicePort));

          service.addService(newService);
          update();
        } catch (ApiException ex) {
          log.error("Service Panel", ex);
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
    int row = serviceTable.getSelectedRow();

    if (row != -1) {
      servicePortModel.setServicePorts(serviceModel.getService(row).getServicePorts());
      servicePortModel.fireTableDataChanged();
      labelTableModel.setList(serviceModel.getService(row).getLabels());
      labelTableModel.fireTableDataChanged();
      deleteButton.setEnabled(true);
    } else {
      servicePortModel.setServicePorts(new ArrayList<>());
      servicePortModel.fireTableDataChanged();
      labelTableModel.setList(new HashMap<>());
      labelTableModel.fireTableDataChanged();
      deleteButton.setEnabled(false);
    }
  }
}
