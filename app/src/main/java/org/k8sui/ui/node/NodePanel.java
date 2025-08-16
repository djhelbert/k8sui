package org.k8sui.ui.node;

import io.kubernetes.client.openapi.ApiException;
import lombok.extern.log4j.Log4j2;
import org.k8sui.service.NodeService;
import org.k8sui.ui.MapTableModel;
import org.k8sui.ui.Util;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

@Log4j2
public class NodePanel extends JPanel implements ActionListener, ListSelectionListener {
    private final JPanel buttonPanel = new JPanel();
    private final JButton refreshButton = new JButton("Refresh");
    private JTable table;
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
        }

        // Refresh button setup
        refreshButton.setIcon(Util.getImageIcon("undo.png"));
        refreshButton.addActionListener(this);
        // Button panel setup
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        //  Setup tables
        table = new JTable(nodeModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setMaxWidth(50);
        table.getColumnModel().getColumn(4).setMaxWidth(110);
        table.getColumnModel().getColumn(5).setMaxWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)) {
            try {
                nodeModel.setNodes(service.nodes());
                nodeModel.fireTableDataChanged();
            } catch (ApiException err) {
                throw new RuntimeException(err);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            mapTableModel.setList(nodeModel.getNode(row).getLabels());
            mapTableModel.fireTableDataChanged();
        } else {
            mapTableModel.setList(new HashMap<>());
            mapTableModel.fireTableDataChanged();
        }
    }
}
