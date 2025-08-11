package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Secret;
import org.k8sui.model.SecretData;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SecretService {

    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

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

    public void addSecret(String name, String nameSpace, String key, byte[] value) throws ApiException {
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

    public void deleteSecret(String name, String nameSpace) throws ApiException {
        coreV1Api.deleteNamespacedSecret(name, nameSpace).execute();
    }
}
