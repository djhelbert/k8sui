/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.pod;

import io.kubernetes.client.openapi.ApiException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.k8sui.service.PodService;
import org.k8sui.ui.NameSpaceListPanel;
import org.k8sui.ui.Updated;
import org.k8sui.ui.Util;

@Log4j2
public class PodPanel extends JPanel implements ActionListener, ListSelectionListener, Updated {

  private final JPanel buttonPanel = new JPanel();
  private final JButton refreshButton = new JButton("Refresh");
  private JTable podTable;
  private PodModel podModel;
  private final ConditionModel conditionModel = new ConditionModel(new ArrayList<>());
  private final PodService service = new PodService();
  private final PodContainerModel podContainerModel = new PodContainerModel(new ArrayList<>());
  @Getter
  private final NameSpaceListPanel nameSpaceListPanel = new NameSpaceListPanel(this);

  /**
   * Constructor
   */
  public PodPanel() {
    super();
    init();
  }

  private void init() {
    try {
      podModel = new PodModel(service.listPods(nameSpaceListPanel.getNamespace()));
    } catch (ApiException err) {
      log.error("Pod Panel", err);
      podModel = new PodModel(new ArrayList<>());
    }

    // Refresh button setup
    refreshButton.setIcon(Util.getImageIcon("undo.png"));
    refreshButton.addActionListener(this);

    // Button panel setup
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.add(nameSpaceListPanel);
    buttonPanel.add(refreshButton);

    // Table setup
    podTable = new JTable(podModel);
    podTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    podTable.setDefaultRenderer(String.class, new RunningTableCellRenderer());
    Util.tableColumnSize(podTable, 3, 80);
    Util.tableColumnSize(podTable, 4, 100);
    podTable.getSelectionModel().addListSelectionListener(this);

    var southPanel = new JPanel(new GridLayout(1, 2));

    // Setup pod containers table
    var podContainerTable = new JTable(podContainerModel);
    podContainerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    var podContainerScrollPane = new JScrollPane(podContainerTable);
    podContainerScrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Containers"));

    var conditionTable = new JTable(conditionModel);
    conditionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    Util.tableColumnSize(conditionTable, 1, 80);
    var conditionScrollPane = new JScrollPane(conditionTable);
    conditionScrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Conditions"));

    southPanel.add(podContainerScrollPane);
    southPanel.add(conditionScrollPane);

    setLayout(new BorderLayout());
    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(podTable), BorderLayout.CENTER);
    add(southPanel, BorderLayout.SOUTH);
  }

  @Override
  public void update() {
    podTable.clearSelection();

    try {
      podModel.setPods(service.listPods(nameSpaceListPanel.getNamespace()));
    } catch (ApiException ex) {
      log.error("Pod Panel", ex);
      Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
    }

    podModel.fireTableDataChanged();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(refreshButton)) {
      try {
        podModel.setPods(service.listPods(nameSpaceListPanel.getNamespace()));
        podModel.fireTableDataChanged();
      } catch (ApiException ex) {
        log.error("Pod Panel", ex);
        Util.showError(this, Util.getValue(ex.getResponseBody(), "reason"), "Error");
      }
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    int row = podTable.getSelectedRow();

    if (row != -1) {
      podContainerModel.setContainers(podModel.getPod(row).getContainers());
      podContainerModel.fireTableDataChanged();
      conditionModel.setConditions(podModel.getPod(row).getConditions());
      conditionModel.fireTableDataChanged();
    } else {
      podContainerModel.setContainers(new ArrayList<>());
      podContainerModel.fireTableDataChanged();
      conditionModel.setConditions(new ArrayList<>());
      conditionModel.fireTableDataChanged();
    }
  }
}
