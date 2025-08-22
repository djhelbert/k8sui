/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.ui;

import static java.util.stream.Collectors.toList;

import io.kubernetes.client.openapi.ApiException;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.extern.log4j.Log4j2;
import org.k8sui.model.NameSpace;
import org.k8sui.service.NameSpaceService;

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
    if (list.getSelectedItem() == null) {
      return null;
    }

    return list.getSelectedItem().toString();
  }

  @Override
  public void nameSpaceChange(String namespace, NameSpaceOperation nameSpaceOperation) {
    if (nameSpaceOperation == NameSpaceOperation.ADD) {
      model.addNameSpace(namespace);
    } else {
      String temp = getNamespace();
      model.removeNameSpace(namespace);
      if (temp != null && temp.equalsIgnoreCase(namespace)) {
        model.setSelectedItem(0);
      }
    }
  }
}
