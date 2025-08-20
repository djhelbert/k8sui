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
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.NameSpace;

public class NameSpaceService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();

  public List<NameSpace> nameSpaces() throws ApiException {
    return coreV1Api.listNamespace().execute().getItems().stream().map(item -> {
      V1ObjectMeta meta = item.getMetadata();
      V1NamespaceStatus status = item.getStatus();

      var nameSpace = new NameSpace(meta.getUid(), meta.getName(), meta.getCreationTimestamp(),
          status.getPhase());
      nameSpace.setLabels(meta.getLabels());

      return nameSpace;
    }).collect(Collectors.toList());
  }

  public V1Namespace createNamespace(String name, Map<String, String> labels) throws ApiException {
    var namespace = new V1Namespace();
    namespace.metadata(new V1ObjectMeta().name(name).labels(labels));
    return coreV1Api.createNamespace(namespace).execute();
  }

  public V1Status deleteNamespace(String name) throws ApiException {
    V1DeleteOptions deleteOptions = new V1DeleteOptions();
    return coreV1Api.deleteNamespace(name).execute();
  }
}
