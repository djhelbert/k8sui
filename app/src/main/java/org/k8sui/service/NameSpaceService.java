package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.NameSpace;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NameSpaceService {

    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

    public List<NameSpace> nameSpaces() throws ApiException {
        return coreV1Api.listNamespace().execute().getItems().stream().map(item -> {
            V1ObjectMeta meta = item.getMetadata();
            V1NamespaceStatus status = item.getStatus();

            var nameSpace = new NameSpace(meta.getUid(), meta.getName(), meta.getCreationTimestamp(), status.getPhase());
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
