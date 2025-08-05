package org.k8sui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.service.NameSpaceService;
import org.k8sui.service.NodeService;

public class App {
    public static void main(String[] args) throws ApiException {
        NodeService nodeService = new NodeService();
        nodeService.nodes().stream().forEach(System.out::println);

        NameSpaceService service = new NameSpaceService();
        service.nameSpaces().stream().forEach(System.out::println);
    }
}
