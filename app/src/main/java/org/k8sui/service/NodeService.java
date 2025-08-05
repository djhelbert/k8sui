package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Node;

import java.util.List;
import java.util.stream.Collectors;

public class NodeService {
    private final CoreV1Api coreV1Api = CoreApiSupplier.api();
    private static final String CPU = "cpu";
    private static final String ARCH = "architecture";
    private static final String IMAGE = "osImage";
    private static final String MEMORY = "memory";
    private static final String IP = "InternalIP";

    public List<Node> nodes() throws ApiException {
        return coreV1Api.listNode().execute().getItems().stream().map(item -> {
            var data = item.getMetadata();
            var status = item.getStatus();
            var info = status.getNodeInfo();

            Node n = new Node(data.getUid(), data.getName(), status.getCapacity().get(CPU).getNumber().toString());
            n.setImage(info.getOsImage());
            n.setMemory(status.getCapacity().get(MEMORY).getNumber().toString());

            var ip = status.getAddresses().stream().filter(a -> IP.equals(a.getType())).findFirst();
            n.setIp(ip.orElseGet(V1NodeAddress::new).getAddress());

            return n;
        }).collect(Collectors.toList());
    }
}
