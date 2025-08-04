package org.k8sui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.service.NameSpaceService;

public class App {
    public static void main(String[] args) throws ApiException {
        NameSpaceService service = new NameSpaceService();
        var test = service.createNamespace("test");
        service.nameSpaces().stream().forEach(i -> System.out.println(i));
        service.deleteNamespace("test");
    }
}
