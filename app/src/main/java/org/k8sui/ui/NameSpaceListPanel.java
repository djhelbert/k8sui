package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.model.NameSpace;
import org.k8sui.service.NameSpaceService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NameSpaceListPanel extends JPanel {
    private JComboBox<Object> list;
    private final NameSpaceService service = new NameSpaceService();

    public NameSpaceListPanel(Updated updatedObject) {
        super();
        update();
        setLayout(new FlowLayout());
        add(new JLabel("Namespace"));
        add(list);

        list.addActionListener(ae -> updatedObject.update());
    }

    public void update() {
        List<String> nameSpaces = null;
        try {
            nameSpaces = service.nameSpaces().stream().map(NameSpace::getNamespace).toList();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        list = new JComboBox<>(nameSpaces.stream().toArray());
        list.setSelectedIndex(0);
        list.revalidate();
        list.repaint();
    }

    public String getNamespace() {
        return list.getSelectedItem().toString();
    }
}
