package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.ServicePort;
import org.k8sui.model.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceService {

    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

    public List<Service> services(String nameSpace) throws ApiException {
        var serviceList = coreV1Api.listNamespacedService(nameSpace).execute();

        return serviceList.getItems().stream()
                .map(s -> {
                            Service service = new Service(s.getMetadata().getUid(), s.getMetadata().getName(), s.getMetadata().getNamespace());

                            Map<String, String> map = s.getSpec().getSelector();
                            service.setSelectors(map);
                            service.setType(s.getSpec().getType());
                            service.setClusterIp(s.getSpec().getClusterIP());

                            List<V1ServicePort> v1ServicePorts = s.getSpec().getPorts();

                            if (v1ServicePorts != null) {
                                List<ServicePort> servicePortList = v1ServicePorts.stream().map(p -> {
                                    ServicePort servicePort = new ServicePort();
                                    servicePort.setName(p.getName());
                                    servicePort.setPort(p.getPort());
                                    servicePort.setProtocol(p.getProtocol());
                                    servicePort.setNodePort(p.getNodePort());
                                    servicePort.setAppProtocol(p.getAppProtocol());

                                    if(p.getTargetPort() != null) {
                                        servicePort.setTargetPort(p.getTargetPort().toString());
                                    }

                                    return servicePort;
                                }).toList();

                                service.setServicePorts(servicePortList);
                            }

                            return service;
                        }
                )
                .collect(Collectors.toList());
    }

    public V1Service createService(String name, String kind, Integer port) throws ApiException {
        V1Service v1Service = new V1Service();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        v1Service.setMetadata(metadata);

        var serviceSpec = new V1ServiceSpec();

        var servicePort = new V1ServicePort();
        servicePort.setPort(port);
        serviceSpec.setPorts(Collections.singletonList(servicePort));
        v1Service.setSpec(serviceSpec);

        V1Service body = new V1Service();
        body.setKind(kind);

        return coreV1Api.createNamespacedService(name, body).execute();
    }

    public V1Service deleteService(String name, String namespace) throws ApiException {
        return coreV1Api.deleteNamespacedService(name, namespace).execute();
    }
}
