package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Port;
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
                            Service svc = new Service(s.getMetadata().getUid(), s.getMetadata().getName(), s.getMetadata().getNamespace());

                            Map<String, String> map = s.getSpec().getSelector();
                            svc.setSelectors(map);
                            svc.setType(s.getSpec().getType());

                            List<V1ServicePort> ports = s.getSpec().getPorts();

                            if (ports != null) {
                                List<Port> portList = ports.stream().map(p -> {
                                    Port port = new Port();
                                    port.setName(p.getName());
                                    port.setPort(p.getPort());
                                    port.setProtocol(p.getProtocol());
                                    port.setTargetPort(p.getTargetPort().toString());
                                    return port;
                                }).toList();

                                svc.setPorts(portList);
                            }

                            return svc;
                        }
                )
                .collect(Collectors.toList());
    }

    public V1Service createService(String name, String kind, Integer port) throws ApiException {
        V1Service service = new V1Service();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        service.setMetadata(metadata);

        var serviceSpec = new V1ServiceSpec();

        var servicePort = new V1ServicePort();
        servicePort.setPort(port);
        serviceSpec.setPorts(Collections.singletonList(servicePort));
        service.setSpec(serviceSpec);

        V1Service body = new V1Service();
        body.setKind(kind);

        return coreV1Api.createNamespacedService(name, body).execute();
    }

    public V1Service deleteService(String name, String namespace) throws ApiException {
        return coreV1Api.deleteNamespacedService(name, namespace).execute();
    }
}
