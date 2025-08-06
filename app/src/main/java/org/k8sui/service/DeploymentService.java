package org.k8sui.service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.Deployment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeploymentService {

    private final AppsV1Api appsV1Api = CoreApiSupplier.app();

    public List<Deployment> listDeployments(String namespace) throws ApiException {
        var deploymentList = appsV1Api.listNamespacedDeployment(namespace).execute();

        return deploymentList.getItems().stream()
                .map(d -> {
                    Deployment dep = new Deployment(d.getMetadata().getUid(), d.getMetadata().getName(), d.getMetadata().getNamespace());
                    dep.setReplicas(d.getSpec().getReplicas());

                    return dep;
                }).collect(Collectors.toList());
    }

    public V1Deployment addDeployment(Deployment dep) throws ApiException {
        // Create a basic deployment
        V1Deployment deployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(dep.getName());

        // Set the selector to match the labels in the template
        V1LabelSelector selector = new V1LabelSelector();
        selector.setMatchLabels(dep.getLabels());
        V1DeploymentSpec spec = new V1DeploymentSpec();
        spec.setSelector(selector);

        // Set the template with metadata and containers
        V1PodTemplateSpec template = new V1PodTemplateSpec();
        V1ObjectMeta templateMetadata = new V1ObjectMeta();
        templateMetadata.setLabels(dep.getLabels());
        template.setMetadata(templateMetadata);

        var containerList = dep.getContainers().stream().map(c -> {
            V1Container container = new V1Container();
            container.setName(c.getName());
            container.setImage(c.getImage());
            container.setPorts(Collections.singletonList(new V1ContainerPort().containerPort(c.getPorts().getFirst().getContainerPort())));
            return container;
        }).toList();

        // Set other container properties as needed
        template.setSpec(new V1PodSpec().containers(containerList));
        spec.setTemplate(template);

        // Set the deployment's metadata and spec
        deployment.metadata(metadata);
        deployment.spec(spec);

        // Set the number of replicas
        if (dep.getReplicas() != null) {
            if (deployment.getSpec() != null) {
                deployment.getSpec().setReplicas(dep.getReplicas());
            }
        }

        return appsV1Api.createNamespacedDeployment(dep.getNamespace(), deployment).execute();
    }

    public V1Status deleteDeployment(String nameSpace, String name) throws ApiException {
        return appsV1Api.deleteNamespacedDeployment(name, nameSpace).execute();
    }
}
