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
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMapEnvSource;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimVolumeSource;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1SecretEnvSource;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Container;
import org.k8sui.model.ContainerPort;
import org.k8sui.model.Deployment;
import org.k8sui.model.DeploymentVolume;
import org.k8sui.model.VolumeMount;

/**
 * Deployment Service
 */
public class DeploymentService {

  private final AppsV1Api appsV1Api = CoreApiSupplier.app();

  public List<Deployment> listDeployments(String namespace) throws ApiException {
    var deploymentList = appsV1Api.listNamespacedDeployment(namespace).execute();

    return deploymentList.getItems().stream()
        .map(d -> {
          Deployment deployment = new Deployment();

          if (d.getMetadata() != null) {
            deployment = new Deployment(d.getMetadata().getUid(), d.getMetadata().getName(),
                d.getMetadata().getNamespace());
          }

          var status = d.getStatus();

          if (status != null && status.getAvailableReplicas() != null) {
            deployment.setReadyReplicas(status.getAvailableReplicas());
          } else {
            deployment.setReadyReplicas(0);
          }

          if (d.getSpec() != null) {
            deployment.setReplicas(d.getSpec().getReplicas());
          }

          var containerList = d.getSpec().getTemplate().getSpec().getContainers();

          List<Container> containers = containerList.stream().map(c -> {
            Container cont = new Container();
            cont.setName(c.getName());
            cont.setImage(c.getImage());
            cont.setImagePullPolicy(c.getImagePullPolicy());

            List<VolumeMount> volumeMounts = new ArrayList<>();

            if(c.getVolumeMounts() != null) {
              for(V1VolumeMount vm : c.getVolumeMounts()) {
                volumeMounts.add(new VolumeMount(vm.getName(), vm.getMountPath()));
              }
            }

            cont.setVolumeMounts(volumeMounts);

            if (c.getPorts() != null) {
              cont.setPorts(
                  c.getPorts().stream().map(p -> new ContainerPort(p.getContainerPort())).toList());
            }

            if (c.getEnvFrom() != null) {
              Optional<V1EnvFromSource> configMapSrc = c.getEnvFrom().stream()
                  .filter(from -> from.getConfigMapRef() != null).findFirst();
              Optional<V1EnvFromSource> secretSrc = c.getEnvFrom().stream()
                  .filter(from -> from.getSecretRef() != null).findFirst();

              configMapSrc.ifPresent(v1EnvFromSource -> cont.setConfigMapRef(
                  v1EnvFromSource.getConfigMapRef().getName()));
              secretSrc.ifPresent(v1EnvFromSource -> cont.setSecretRef(
                  v1EnvFromSource.getSecretRef().getName()));
            }

            return cont;
          }).collect(toList());

          List<DeploymentVolume> deploymentVolumes = new ArrayList<>();

          if (d.getSpec().getTemplate().getSpec() != null
              && d.getSpec().getTemplate().getSpec().getVolumes() != null) {
            deploymentVolumes = d.getSpec().getTemplate()
                .getSpec().getVolumes()
                .stream().filter(v -> v.getPersistentVolumeClaim() != null)
                .map(v -> new DeploymentVolume(v.getName(),
                    v.getPersistentVolumeClaim().getClaimName()))
                .toList();
          }

          deployment.setVolumes(deploymentVolumes);
          deployment.setContainers(containers);
          deployment.setSelectors(d.getSpec().getSelector().getMatchLabels());
          deployment.setLabels(d.getMetadata().getLabels());

          return deployment;
        }).collect(toList());
  }

  public void addDeployment(Deployment deployment) throws ApiException {
    // Create a deployment
    V1Deployment v1Deployment = new V1Deployment();

    V1ObjectMeta metadata = new V1ObjectMeta();
    metadata.setName(deployment.getName());
    metadata.setCreationTimestamp(OffsetDateTime.now());

    // Set the selector to match the labels
    V1LabelSelector selector = new V1LabelSelector();
    selector.setMatchLabels(deployment.getLabels());

    // Set deployment spec
    var v1DeploymentSpec = new V1DeploymentSpec();
    v1DeploymentSpec.setSelector(selector);

    // Set the template with metadata and containers
    var template = new V1PodTemplateSpec();
    var objectMeta = new V1ObjectMeta();
    objectMeta.setLabels(deployment.getLabels());
    template.setMetadata(objectMeta);
    v1DeploymentSpec.setTemplate(template);

    var v1ContainerList = deployment.getContainers().stream().map(c -> {
      V1Container v1Container = new V1Container();
      v1Container.setName(c.getName());
      v1Container.setImage(c.getImage());
      v1Container.setImagePullPolicy(c.getImagePullPolicy());

      final List<V1EnvFromSource> envFromSources = new ArrayList<>();

      if (c.getConfigMapRef() != null && !c.getConfigMapRef().isEmpty()) {
        var envFromSource = new V1EnvFromSource();
        envFromSource.setConfigMapRef(new V1ConfigMapEnvSource().name(c.getConfigMapRef()));
        envFromSources.add(envFromSource);
      }

      if (c.getSecretRef() != null && !c.getSecretRef().isEmpty()) {
        var envFromSource = new V1EnvFromSource();
        envFromSource.setSecretRef(new V1SecretEnvSource().name(c.getSecretRef()));
        envFromSources.add(envFromSource);
      }

      if (!envFromSources.isEmpty()) {
        v1Container.setEnvFrom(envFromSources);
      }

      v1Container.setPorts(
          c.getPorts().stream().map(p -> new V1ContainerPort().containerPort(p.getContainerPort()))
              .toList());

      if (c.getVolumeMounts() != null && !c.getVolumeMounts().isEmpty()) {
        List<V1VolumeMount> volumeMounts = new ArrayList<>();

        for (VolumeMount vm : c.getVolumeMounts()) {
          var mount = new V1VolumeMount();
          mount.setMountPath(vm.getMountPath());
          mount.setName(vm.getName());
          volumeMounts.add(mount);
        }

        v1Container.setVolumeMounts(volumeMounts);
      }

      return v1Container;
    }).toList();

    if (deployment.getVolumes() != null && !deployment.getVolumes().isEmpty()) {
      List<V1Volume> volumes = new ArrayList<>();

      for (DeploymentVolume dv : deployment.getVolumes()) {
        volumes.add(new V1Volume().name(dv.getName()).persistentVolumeClaim(
            new V1PersistentVolumeClaimVolumeSource().claimName(dv.getClaimName())));
      }

      template.setSpec(new V1PodSpec().containers(v1ContainerList).volumes(volumes));
    }

    // Set the metadata and spec
    v1Deployment.metadata(metadata);
    v1Deployment.spec(v1DeploymentSpec);

    // Set the number of replicas
    if (deployment.getReplicas() != null) {
      if (v1Deployment.getSpec() != null) {
        v1Deployment.getSpec().setReplicas(deployment.getReplicas());
      }
    }

    appsV1Api.createNamespacedDeployment(deployment.getNamespace(), v1Deployment).execute();
  }

  /**
   * Delete Deployment
   *
   * @param nameSpace Name Space
   * @param name      Name
   * @throws ApiException API Exception
   */
  public void deleteDeployment(String nameSpace, String name) throws ApiException {
    appsV1Api.deleteNamespacedDeployment(name, nameSpace).execute();
  }
}
