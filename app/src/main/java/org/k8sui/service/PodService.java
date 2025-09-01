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

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import java.util.List;
import java.util.stream.Collectors;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Condition;
import org.k8sui.model.EnvVar;
import org.k8sui.model.Pod;
import org.k8sui.model.PodContainer;

/**
 * Pod Service
 */
public class PodService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();

  /**
   * List Pods by Name Space
   *
   * @param nameSpace Name Space
   * @return Pod List
   * @throws ApiException API Exception
   */
  public List<Pod> listPods(String nameSpace) throws ApiException {
    return coreV1Api.listNamespacedPod(nameSpace).execute().getItems().stream()
        .map(vp -> {
          var pod = new Pod();
          pod.setNamespace(nameSpace);

          if (vp.getStatus() != null) {
            List<Condition> conditions = null;
            if (vp.getStatus().getConditions() != null) {
              conditions = vp.getStatus().getConditions().stream()
                  .map(c -> new Condition(c.getType(), c.getStatus(), c.getLastTransitionTime(),
                      c.getReason(), c.getMessage()))
                  .collect(Collectors.toList());
            }
            pod.setConditions(conditions);
          }

          if (vp.getStatus() != null) {
            pod.setStatus(vp.getStatus().getPhase());
            pod.setIp(vp.getStatus().getPodIP());
          }

          if (vp.getMetadata() != null) {
            pod.setUid(vp.getMetadata().getUid());
            pod.setName(vp.getMetadata().getName());
          }

          if (vp.getMetadata() != null) {
            pod.setCreation(vp.getMetadata().getCreationTimestamp());
          }

          if (vp.getMetadata() != null) {
            pod.setLabels(vp.getMetadata().getLabels());
          }

          if (vp.getSpec() != null) {
            List<PodContainer> containers = vp.getSpec().getContainers().stream().map(c -> {
              var pc = new PodContainer();
              pc.setImage(c.getImage());
              pc.setName(c.getName());

              if (c.getEnv() != null) {
                List<EnvVar> vars = c.getEnv().stream()
                    .map(ev -> new EnvVar(null, ev.getName(), ev.getValue())).collect(toList());
                pc.setVariables(vars);
              }

              if (c.getVolumeMounts() != null) {
                List<String> mounts = c.getVolumeMounts().stream().map(V1VolumeMount::getMountPath)
                    .collect(toList());
                pc.setMounts(mounts);
              }

              return pc;
            }).collect(toList());
            pod.setContainers(containers);
          }

          return pod;
        }).collect(toList());
  }
}
