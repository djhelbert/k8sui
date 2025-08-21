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
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.ConfigMap;
import org.k8sui.model.ConfigMapData;

public class ConfigMapService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();

  public List<String> configMapListNames(String namespace) throws ApiException {
    var list = coreV1Api.listNamespacedConfigMap(namespace).execute();
    return list.getItems().stream().map(cm -> cm.getMetadata().getName()).toList();
  }

  public List<ConfigMap> configMapList(String namespace) throws ApiException {
    var list = coreV1Api.listNamespacedConfigMap(namespace).execute();

    return list.getItems().stream().map(cm -> {
      var configMap = new ConfigMap();
      configMap.setUid(cm.getMetadata().getUid());
      configMap.setNameSpace(cm.getMetadata().getNamespace());
      configMap.setName(cm.getMetadata() == null ? null : cm.getMetadata().getName());
      configMap.setCreationDate(cm.getMetadata().getCreationTimestamp());

      var map = cm.getData();

      List<ConfigMapData> configMapDataList = map.keySet().stream()
          .map(k -> new ConfigMapData(k, map.get(k)))
          .collect(toList());

      configMap.setData(configMapDataList);

      return configMap;
    }).collect(toList());
  }

  public void createConfigMap(ConfigMap configMap) throws ApiException {
    var body = new V1ConfigMap();
    var meta = new V1ObjectMeta();
    meta.setName(configMap.getName());
    meta.setCreationTimestamp(OffsetDateTime.now());
    meta.setNamespace(configMap.getNameSpace());
    body.setMetadata(meta);

    final Map<String, String> map = new HashMap<>();

    for (ConfigMapData d : configMap.getData()) {
      map.put(d.getKey(), d.getValue());
    }

    body.setData(map);

    coreV1Api.createNamespacedConfigMap(configMap.getNameSpace(), body).execute();
  }

  public void addData(String name, String nameSpace, String key, String value) throws ApiException {
    var list = coreV1Api.listNamespacedConfigMap(nameSpace).execute();
    var option = list.getItems().stream()
        .filter(i -> name.equalsIgnoreCase(i.getMetadata().getName())).findFirst();
    var v1ConfigMap = option.get();

    v1ConfigMap.getData().put(key, value);

    coreV1Api.replaceNamespacedConfigMap(name, nameSpace, v1ConfigMap).execute();
  }

  /**
   * Delete Config Map
   * @param name Name
   * @param nameSpace Name Space
   * @throws ApiException API Exception
   */
  public void deleteConfigMap(String name, String nameSpace) throws ApiException {
    coreV1Api.deleteNamespacedConfigMap(name, nameSpace).execute();
  }
}
