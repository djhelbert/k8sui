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

import static java.util.stream.Collectors.toList;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Secret;
import org.k8sui.model.SecretData;

/**
 * Secret Service
 */
@Log4j2
public class SecretService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();

  public List<String> secretListNames(String namespace) {
    V1SecretList list = null;

    try {
      list = coreV1Api.listNamespacedSecret(namespace).execute();
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }

    return list.getItems().stream()
        .filter(s -> s.getMetadata() != null && s.getMetadata().getName() != null)
        .map(s -> s.getMetadata().getName()).toList();
  }

  public List<Secret> secretList(String namespace) throws ApiException {
    var list = coreV1Api.listNamespacedSecret(namespace).execute();

    return list.getItems().stream().map(s -> {
      var secret = new Secret();
      secret.setName(s.getMetadata() == null ? null : s.getMetadata().getName());
      secret.setUid(s.getMetadata().getUid());
      secret.setCreationDate(s.getMetadata().getCreationTimestamp());
      secret.setNameSpace(s.getMetadata().getNamespace());

      var map = s.getData();

      List<SecretData> secretDataList = map.keySet().stream()
          .map(key -> new SecretData(key, map.get(key)))
          .collect(toList());

      secret.setData(secretDataList);

      return secret;
    }).collect(toList());
  }

  public void createSecret(Secret secret) throws ApiException {
    var body = new V1Secret();
    var meta = new V1ObjectMeta();
    meta.setName(secret.getName());
    meta.setCreationTimestamp(OffsetDateTime.now());
    meta.setNamespace(secret.getNameSpace());

    body.setMetadata(meta);

    final Map<String, byte[]> map = new HashMap<>();

    for (SecretData secretData : secret.getData()) {
      map.put(secretData.getKey(), secretData.getValue());
    }

    body.setData(map);

    coreV1Api.createNamespacedSecret(secret.getNameSpace(), body).execute();
  }

  public void addSecret(String name, String nameSpace, String key, byte[] value)
      throws ApiException {
    var list = coreV1Api.listNamespacedSecret(nameSpace).execute();

    var option = list.getItems().stream()
        .filter(vs -> name.equalsIgnoreCase(vs.getMetadata().getName()))
        .findFirst();

    if (option.isPresent()) {
      var v1Secret = option.get();

      v1Secret.getData().put(key, value);

      coreV1Api.replaceNamespacedSecret(name, nameSpace, v1Secret).execute();
    }
  }

  /**
   * Delete Secret
   * @param name Secret Name
   * @param nameSpace Name Space
   * @throws ApiException API Exception
   */
  public void deleteSecret(String name, String nameSpace) throws ApiException {
    coreV1Api.deleteNamespacedSecret(name, nameSpace).execute();
  }
}
