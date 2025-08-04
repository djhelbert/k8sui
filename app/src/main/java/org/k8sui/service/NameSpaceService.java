package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import org.k8sui.ApiUtil;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.NameSpace;

import java.util.List;
import java.util.stream.Collectors;

public class NameSpaceService {

    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

    public List<NameSpace> nameSpaces() throws ApiException {
        return ApiUtil.nodeList().getItems().stream().map(item -> {
            V1ObjectMeta data = item.getMetadata();
            return new NameSpace(data.getUid(), data.getName(), data.getCreationTimestamp());
        }).collect(Collectors.toList());
    }

    public V1Namespace createNamespace(String name) throws ApiException {
        var namespace = new V1Namespace();
        namespace.metadata(new V1ObjectMeta().name(name));
        return coreV1Api.createNamespace(namespace).execute();
    }

    public V1Status deleteNamespace(String name) throws ApiException {
        V1DeleteOptions deleteOptions = new V1DeleteOptions();
        return coreV1Api.deleteNamespace(name).execute();
    }
}
