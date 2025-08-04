package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Node;

import java.util.List;
import java.util.stream.Collectors;

public class NodeService {
    private final CoreV1Api coreV1Api = CoreApiSupplier.api();
    private final String CPU = "cpu";

    public List<Node> nodes() throws ApiException {
        return coreV1Api.listNode().execute().getItems().stream().map(item -> {
            var data = item.getMetadata();
            var status = item.getStatus();
            return new Node(data.getUid(), data.getName(), status.getCapacity().get(CPU).getNumber().toString());
        }).collect(Collectors.toList());
    }
}
