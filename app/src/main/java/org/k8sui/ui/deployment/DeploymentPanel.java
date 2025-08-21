/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.deployment;

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
import org.k8sui.model.Container;
import org.k8sui.model.ContainerPort;
import org.k8sui.model.Deployment;
import org.k8sui.service.DeploymentService;
import org.k8sui.ui.NameSpaceListPanel;
import org.k8sui.ui.NameValidator;
import org.k8sui.ui.Updated;
import org.k8sui.ui.Util;

@Log4j2
public class DeploymentPanel extends JPanel implements ActionListener, ListSelectionListener,
    Updated {

  private final JPanel buttonPanel = new JPanel();
  private final JButton refreshButton = new JButton("Refresh");
  private final JButton addButton = new JButton("Add");
  private final JButton deleteButton = new JButton("Delete");
  private JTable table;
  private DeploymentModel model;
  private final ContainerModel containerModel = new ContainerModel(new ArrayList<>());
  private final DeploymentService service = new DeploymentService();
  @Getter
  private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);

  public DeploymentPanel() {
    super();
    init();
  }

  private void init() {
    try {
      model = new DeploymentModel(service.listDeployments(nameSpaceListPanel.getNamespace()));
    } catch (ApiException err) {
      log.error("Node Panel", err);
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

    JTable containerTable = new JTable(containerModel);
    containerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    setLayout(new BorderLayout());
    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(new JScrollPane(containerTable), BorderLayout.SOUTH);
  }

  @Override
  public void update() {
    table.clearSelection();

    try {
      model.setDeployments(service.listDeployments(nameSpaceListPanel.getNamespace()));
    } catch (ApiException err) {
      log.error("Deployment Panel", err);
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
        Deployment dp = model.getDeployment(row);
        try {
          service.deleteDeployment(dp.getNamespace(), dp.getName());
        } catch (ApiException ex) {
          log.error("Node Panel", ex);
          Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }
        update();
      }
    }
    if (e.getSource().equals(addButton)) {
      // Create the dialog
      JDialog dialog = new JDialog(App.frame(), "Add Deployment", true);
      dialog.setLayout(new BorderLayout(5, 5));

      var gridPanel = new JPanel(new GridLayout(7, 2, 5, 5));

      // Create text field
      var nameField = new JTextField(30);
      gridPanel.add(new JLabel("Name:"));
      gridPanel.add(nameField);

      var selectorField = new JTextField(30);
      gridPanel.add(new JLabel("Selector (app):"));
      gridPanel.add(selectorField);

      var imageField = new JTextField();
      gridPanel.add(new JLabel("Image:"));
      gridPanel.add(imageField);

      JComboBox<String> imgPullPolicyList = new JComboBox<>(new String[]{"Always", "IfNotPresent"});
      gridPanel.add(new JLabel("Pull Policy:"));
      gridPanel.add(imgPullPolicyList);

      gridPanel.add(new JLabel("Replicas:"));
      JComboBox<Integer> replicas = new JComboBox<>(
          new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13});
      replicas.setSelectedIndex(2);
      gridPanel.add(replicas);

      var portField = new JTextField("80");
      gridPanel.add(new JLabel("Port:"));
      gridPanel.add(portField);

      var mountPathField = new JTextField(30);
      gridPanel.add(new JLabel("Mount Path:"));
      gridPanel.add(mountPathField);

      dialog.add(gridPanel, BorderLayout.CENTER);

      // Create OK and Cancel buttons
      JPanel buttonPanel = new JPanel(new FlowLayout());
      JButton okButton = new JButton("OK");
      JButton cancelButton = new JButton("Cancel");
      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);
      dialog.add(buttonPanel, BorderLayout.SOUTH);

      // OK button action
      okButton.addActionListener(e1 -> {
        String input = nameField.getText();

        if (!NameValidator.validName(nameField.getText())) {
          Util.showError(this, "Invalid Name", "Validation Error");
          return;
        }

        try {
          Map<String, String> map = new HashMap<>();
          map.put("app", selectorField.getText());

          var newDeployment = new Deployment(null, input, nameSpaceListPanel.getNamespace());
          newDeployment.setReplicas((Integer) replicas.getSelectedItem());
          newDeployment.setLabels(map);
          newDeployment.setSelectors(map);

          var container = new Container();
          container.setName(nameField.getText());
          container.setImage(imageField.getText());
          container.setPorts(List.of(new ContainerPort(Integer.parseInt(portField.getText()))));
          newDeployment.setContainers(List.of(container));

          service.addDeployment(newDeployment);
          update();
        } catch (ApiException ex) {
          log.error("Node Panel", ex);
          Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
        }

        dialog.dispose();
      });

      // Cancel button action
      cancelButton.addActionListener(ae -> dialog.dispose());

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
      containerModel.setContainers(model.getDeployment(row).getContainers());
      containerModel.fireTableDataChanged();
    } else {
      containerModel.setContainers(new ArrayList<>());
      containerModel.fireTableDataChanged();
    }
  }
}
