package org.k8sui;

import io.kubernetes.client.openapi.ApiException;
import org.k8sui.service.DeploymentService;
import org.k8sui.service.ServiceService;

public class TestApp {
    public static void main(String[] args) throws ApiException {
        ServiceService service = new ServiceService();
        var list = service.services("default");
        list.forEach(System.out::println);

        DeploymentService dservice = new DeploymentService();
        var dlist = dservice.listDeployments("default");
        dlist.forEach(System.out::println);
    }
}
