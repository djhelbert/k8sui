package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Container;
import org.k8sui.model.ContainerPort;
import org.k8sui.model.Deployment;

import java.time.OffsetDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DeploymentService {

    private final AppsV1Api appsV1Api = CoreApiSupplier.app();

    public List<Deployment> listDeployments(String namespace) throws ApiException {
        var deploymentList = appsV1Api.listNamespacedDeployment(namespace).execute();

        return deploymentList.getItems().stream()
                .map(d -> {
                    Deployment deployment = new Deployment(d.getMetadata().getUid(), d.getMetadata().getName(), d.getMetadata().getNamespace());

                    var status = d.getStatus();
                    if(status != null) {
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

                        if (c.getPorts() != null) {
                            cont.setPorts(c.getPorts().stream().map(p -> {
                                return new ContainerPort(p.getContainerPort());
                            }).toList());
                        }

                        return cont;
                    }).collect(toList());

                    deployment.setContainers(containers);

                    deployment.setSelectors(d.getSpec().getSelector().getMatchLabels());
                    deployment.setLabels(d.getMetadata().getLabels());

                    return deployment;
                }).collect(toList());
    }

    public V1Deployment addDeployment(Deployment dep) throws ApiException {
        // Create a deployment
        V1Deployment v1Deployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(dep.getName());
        metadata.setCreationTimestamp(OffsetDateTime.now());

        // Set the selector to match the labels
        V1LabelSelector selector = new V1LabelSelector();
        selector.setMatchLabels(dep.getLabels());

        // Set deployment spec
        V1DeploymentSpec v1DeploymentSpec = new V1DeploymentSpec();
        v1DeploymentSpec.setSelector(selector);

        // Set the template with metadata and containers
        V1PodTemplateSpec template = new V1PodTemplateSpec();
        V1ObjectMeta templateMetadata = new V1ObjectMeta();
        templateMetadata.setLabels(dep.getLabels());
        template.setMetadata(templateMetadata);

        var containerList = dep.getContainers().stream().map(c -> {
            V1Container v1Container = new V1Container();
            v1Container.setName(c.getName());
            v1Container.setImage(c.getImage());
            v1Container.setPorts(c.getPorts().stream().map(p -> new V1ContainerPort().containerPort(p.getContainerPort())).toList());
            return v1Container;
        }).toList();

        template.setSpec(new V1PodSpec().containers(containerList));
        v1DeploymentSpec.setTemplate(template);

        // Set the metadata and spec
        v1Deployment.metadata(metadata);
        v1Deployment.spec(v1DeploymentSpec);

        // Set the number of replicas
        if (dep.getReplicas() != null) {
            if (v1Deployment.getSpec() != null) {
                v1Deployment.getSpec().setReplicas(dep.getReplicas());
            }
        }

        return appsV1Api.createNamespacedDeployment(dep.getNamespace(), v1Deployment).execute();
    }

    public V1Status deleteDeployment(String nameSpace, String name) throws ApiException {
        return appsV1Api.deleteNamespacedDeployment(name, nameSpace).execute();
    }
}
