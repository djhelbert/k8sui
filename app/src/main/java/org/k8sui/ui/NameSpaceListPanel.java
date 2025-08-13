package org.k8sui.ui;

import io.kubernetes.client.openapi.ApiException;
import lombok.extern.log4j.Log4j2;
import org.k8sui.model.NameSpace;
import org.k8sui.service.NameSpaceService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Namespace List Panel
 */
@Log4j2
public class NameSpaceListPanel extends JPanel implements NameSpaceObserver {
    private JComboBox<String> list;
    private final NameSpaceService service = new NameSpaceService();
    private NameSpaceComboModel model;

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
            nameSpaces = service.nameSpaces().stream().map(NameSpace::getNamespace).collect(toList());
            model = new NameSpaceComboModel(nameSpaces);
        } catch (ApiException err) {
            log.error("Node Panel", err);
        }

        list = new JComboBox<>(model);
        model.setSelectedItem(0);
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
            model.addNameSpace(namespace);
        } else {
            String temp = getNamespace();
            model.removeNameSpace(namespace);
            if(temp != null && temp.equalsIgnoreCase(namespace)) {
                model.setSelectedItem(0);
            }
        }
    }
}
