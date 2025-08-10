package org.k8sui.service;

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.ConfigMap;
import org.k8sui.model.ConfigMapData;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class ConfigMapService {

    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

    public List<ConfigMap> configMapList(String namespace) throws ApiException {
        var list = coreV1Api.listNamespacedConfigMap(namespace).execute();

        return list.getItems().stream().map(cm -> {
            var configMap = new ConfigMap();
            configMap.setName(cm.getMetadata().getName());
            configMap.setUid(cm.getMetadata().getUid());
            configMap.setCreationDate(cm.getMetadata().getCreationTimestamp());
            configMap.setNameSpace(cm.getMetadata().getNamespace());

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
        var option = list.getItems().stream().filter(i -> name.equalsIgnoreCase(i.getMetadata().getName())).findFirst();
        var v1ConfigMap = option.get();
        v1ConfigMap.getData().put(key, value);

        coreV1Api.replaceNamespacedConfigMap(name, nameSpace, v1ConfigMap).execute();
    }

    public void deleteConfigMap(String name, String nameSpace) throws ApiException {
        coreV1Api.deleteNamespacedConfigMap(name, nameSpace).execute();
    }
}
