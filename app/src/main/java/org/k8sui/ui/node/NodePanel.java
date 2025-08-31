/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui.node;

import io.kubernetes.client.openapi.ApiException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lombok.extern.log4j.Log4j2;
import org.k8sui.service.NodeService;
import org.k8sui.ui.MapTableModel;
import org.k8sui.ui.Util;

@Log4j2
public class NodePanel extends JPanel implements ActionListener, ListSelectionListener {

  private final JPanel buttonPanel = new JPanel();
  private final JButton refreshButton = new JButton("Refresh");
  private JTable nodeTable;
  private NodeModel nodeModel;
  private final NodeService service = new NodeService();
  private final MapTableModel mapTableModel = new MapTableModel();

  public NodePanel() {
    super();
    init();
  }

  private void init() {
    try {
      nodeModel = new NodeModel(service.nodes());
    } catch (ApiException err) {
      log.error("Node Panel", err);
      nodeModel = new NodeModel(new ArrayList<>());
    }

    // Refresh button setup
    refreshButton.setIcon(Util.getImageIcon("undo.png"));
    refreshButton.addActionListener(this);
    // Button panel setup
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.add(refreshButton);
    //  Setup tables
    nodeTable = new JTable(nodeModel);
    nodeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    Util.tableColumnSize(nodeTable, 3, 50);
    Util.tableColumnSize(nodeTable, 4, 110);
    Util.tableColumnSize(nodeTable, 5, 110);
    nodeTable.getSelectionModel().addListSelectionListener(this);

    var labelTable = new JTable(mapTableModel);
    labelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    var scrollPane = new JScrollPane(labelTable);
    scrollPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Labels"));

    setLayout(new BorderLayout());
    add(buttonPanel, BorderLayout.NORTH);
    add(new JScrollPane(nodeTable), BorderLayout.CENTER);
    add(scrollPane, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(refreshButton)) {
      try {
        nodeModel.setNodes(service.nodes());
        nodeModel.fireTableDataChanged();
      } catch (ApiException err) {
        log.error("Node Panel", err);
      }
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    int row = nodeTable.getSelectedRow();

    if (row != -1) {
      mapTableModel.setList(nodeModel.getNode(row).getLabels());
      mapTableModel.fireTableDataChanged();
    } else {
      mapTableModel.setList(new HashMap<>());
      mapTableModel.fireTableDataChanged();
    }
  }
}
