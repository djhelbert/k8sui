package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import lombok.extern.log4j.Log4j2;
import org.k8sui.model.NameSpace;
import org.k8sui.service.NameSpaceService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Namespace List Panel
 */
@Log4j2
public class NameSpaceListPanel extends JPanel implements NameSpaceObserver {
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
        List<String> nameSpaces = new ArrayList<>();

        try {
            nameSpaces = service.nameSpaces().stream().map(NameSpace::getNamespace).toList();
        } catch (ApiException err) {
            log.error("Node Panel", err);
        }

        list = new JComboBox<>(nameSpaces.toArray());
        list.revalidate();
        list.repaint();
        list.setSelectedIndex(0);
    }

    public String getNamespace() {
        if(list.getSelectedItem() == null) {
            return null;
        }

        return list.getSelectedItem().toString();
    }

    @Override
    public void nameSpaceChange(String namespace, NameSpaceOperation nameSpaceOperation) {
        if(nameSpaceOperation == NameSpaceOperation.ADD) {
            list.addItem(namespace);
        } else {
            list.removeItem(namespace);
        }

        update();
    }
}
