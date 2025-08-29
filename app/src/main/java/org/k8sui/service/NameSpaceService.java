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
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.NameSpace;

public class NameSpaceService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();

  /**
   * Get Name Space List
   *
   * @return Name Space List
   * @throws ApiException API Exception
   */
  public List<NameSpace> nameSpaces() throws ApiException {
    return coreV1Api.listNamespace().execute().getItems().stream().map(item -> {
      V1ObjectMeta meta = item.getMetadata();
      V1NamespaceStatus status = item.getStatus();

      var nameSpace = new NameSpace(meta == null ? "" : meta.getUid(),
          meta == null ? "" : meta.getName(),
          meta == null ? OffsetDateTime.now() : meta.getCreationTimestamp(),
          status == null ? "" : status.getPhase());

      if (meta != null) {
        nameSpace.setLabels(meta.getLabels());
      }

      if (meta != null) {
        nameSpace.setAnnotations(meta.getAnnotations());
      }

      return nameSpace;
    }).collect(Collectors.toList());
  }

  /**
   * Create Name Space
   *
   * @param name   Name
   * @param labels Label Map
   * @throws ApiException API Exception
   */
  public void createNamespace(String name, Map<String, String> labels) throws ApiException {
    var namespace = new V1Namespace();
    namespace.metadata(new V1ObjectMeta().name(name).labels(labels));
    coreV1Api.createNamespace(namespace).execute();
  }

  /**
   * Delete Name Space
   *
   * @param name Name
   * @throws ApiException API Exception
   */
  public void deleteNamespace(String name) throws ApiException {
    coreV1Api.deleteNamespace(name).execute();
  }
}
