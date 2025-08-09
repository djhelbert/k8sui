package org.k8sui.service;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Service;
import org.k8sui.model.ServicePort;

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
                                    servicePort.setName(p.getName() == null ? "" : p.getName());
                                    servicePort.setPort(p.getPort());
                                    servicePort.setProtocol(p.getProtocol());
                                    servicePort.setNodePort(p.getNodePort());
                                    servicePort.setAppProtocol(p.getAppProtocol());

                                    if (p.getTargetPort() != null) {
                                        servicePort.setTargetPort(p.getTargetPort().getIntValue());
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

    public V1Service addService(Service svc) throws ApiException {
        V1Service v1Service = new V1Service();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(svc.getName());
        v1Service.setMetadata(metadata);

        var v1ServiceSpec = new V1ServiceSpec();
        v1ServiceSpec.setType(svc.getType());
        v1ServiceSpec.setSelector(svc.getSelectors());

        var v1ServicePort = new V1ServicePort();
        v1ServicePort.setName(svc.getServicePorts().get(0).getName());
        v1ServicePort.setPort(svc.getServicePorts().get(0).getPort());
        v1ServicePort.setTargetPort(new IntOrString(svc.getServicePorts().get(0).getTargetPort()));
        v1ServicePort.setProtocol(svc.getServicePorts().get(0).getProtocol());
        v1ServicePort.setNodePort(svc.getServicePorts().get(0).getNodePort());

        v1ServiceSpec.setPorts(Collections.singletonList(v1ServicePort));
        v1Service.setSpec(v1ServiceSpec);

        return coreV1Api.createNamespacedService(svc.getNamespace(), v1Service).execute();
    }

    public V1Service deleteService(String name, String namespace) throws ApiException {
        return coreV1Api.deleteNamespacedService(name, namespace).execute();
    }
}
