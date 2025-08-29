/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import io.kubernetes.client.openapi.models.V1NodeSystemInfo;
import java.util.List;
import java.util.stream.Collectors;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Node;

public class NodeService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();
  private static final String CPU = "cpu";
  private static final String MEMORY = "memory";
  private static final String IP = "InternalIP";

  /**
   * Get Node List
   *
   * @return Node List
   * @throws ApiException API Exception
   */
  public List<Node> nodes() throws ApiException {
    return coreV1Api.listNode().execute().getItems().stream().map(item -> {
      Node node = new Node();

      var data = item.getMetadata();
      var status = item.getStatus();

      if (data != null) {
        node.setLabels(data.getLabels());
        node.setName(data.getName());
        node.setUid(data.getUid());
      }

      V1NodeSystemInfo info = null;
      if (status != null) {
        info = status.getNodeInfo();
      }

      if (info != null) {
        node.setImage(info.getOsImage());
      }

      if (status != null) {
        if (status.getCapacity() != null) {
          node.setCpu(status.getCapacity().get(CPU).getNumber().toString());
          node.setMemory(status.getCapacity().get(MEMORY).getNumber().toString());
        }

        if (status.getAddresses() != null) {
          var ip = status.getAddresses().stream().filter(a -> IP.equals(a.getType())).findFirst();
          node.setIp(ip.orElseGet(V1NodeAddress::new).getAddress());
        }
      }

      return node;
    }).collect(Collectors.toList());
  }
}
