/**
 * k8sui Copyright (C) 2025 This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details. You should have received a copy of the GNU General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.k8sui.service;

import static java.util.stream.Collectors.toList;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Service;
import org.k8sui.model.ServicePort;

/**
 * Service API Service
 */
public class ServiceService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();

  public List<Service> services(String nameSpace) throws ApiException {
    var serviceList = coreV1Api.listNamespacedService(nameSpace).execute();

    return serviceList.getItems().stream()
        .map(s -> {
              Service service = new Service(s.getMetadata().getUid(), s.getMetadata().getName(),
                  s.getMetadata().getNamespace());

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
                }).collect(toList());

                service.setServicePorts(servicePortList);
              }

              return service;
            }
        )
        .collect(toList());
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
    v1ServicePort.setName(svc.getServicePorts().getFirst().getName());
    v1ServicePort.setPort(svc.getServicePorts().getFirst().getPort());
    v1ServicePort.setTargetPort(new IntOrString(svc.getServicePorts().getFirst().getTargetPort()));
    v1ServicePort.setProtocol(svc.getServicePorts().getFirst().getProtocol());
    v1ServicePort.setNodePort(svc.getServicePorts().getFirst().getNodePort());

    v1ServiceSpec.setPorts(Collections.singletonList(v1ServicePort));
    v1Service.setSpec(v1ServiceSpec);

    return coreV1Api.createNamespacedService(svc.getNamespace(), v1Service).execute();
  }

  /**
   * Delete Service
   * @param name Service Name
   * @param namespace Name Space
   * @return V1Service
   * @throws ApiException API Exception
   */
  public V1Service deleteService(String name, String namespace) throws ApiException {
    return coreV1Api.deleteNamespacedService(name, namespace).execute();
  }
}
