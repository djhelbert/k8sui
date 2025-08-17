package org.k8sui.service;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec;
import io.kubernetes.client.openapi.models.V1VolumeResourceRequirements;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.PersistentVolumeClaim;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class PersistentVolumeClaimService {
    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

    public List<PersistentVolumeClaim> listPersistentVolumeClaims(String nameSpace) throws ApiException {
        var list = coreV1Api.listNamespacedPersistentVolumeClaim(nameSpace).execute();

        final List<PersistentVolumeClaim> volumes = list.getItems().stream().map(pv -> {
            var persistentVolumeClaim = new PersistentVolumeClaim();
            persistentVolumeClaim.setUid(pv.getMetadata().getUid());
            persistentVolumeClaim.setName(pv.getMetadata().getName());
            persistentVolumeClaim.setStorageClassName(pv.getSpec().getStorageClassName());
            persistentVolumeClaim.setNameSpace(pv.getMetadata().getNamespace());
            persistentVolumeClaim.setLabels(pv.getMetadata().getLabels());

            final Map<String, String> resources = new HashMap<>();
            final Map<String, Quantity> resourceMap = pv.getSpec().getResources().getRequests();

            for (String key : resourceMap.keySet()) {
                resources.put(key, resourceMap.get(key).toSuffixedString());
            }

            persistentVolumeClaim.setResources(resources);

            List<String> accessModes = pv.getSpec().getAccessModes();
            persistentVolumeClaim.setAccessModes(accessModes);

            return persistentVolumeClaim;
        }).collect(toList());

        return volumes;
    }

    public void createPersistentVolumeClaim(PersistentVolumeClaim claim) throws ApiException {
        var meta = new V1ObjectMeta();
        meta.setName(claim.getName());
        meta.setNamespace(claim.getNameSpace());
        meta.setCreationTimestamp(OffsetDateTime.now());
        meta.setLabels(claim.getLabels());

        var spec = new V1PersistentVolumeClaimSpec();
        spec.setAccessModes(claim.getAccessModes());

        final Map<String, Quantity> resourceMap = new HashMap<>();

        for (String key : claim.getResources().keySet()) {
            resourceMap.put(key, new Quantity(claim.getResources().get(key)));
        }

        var requirements = new V1VolumeResourceRequirements();
        requirements.setRequests(resourceMap);

        spec.setResources(requirements);

        var volumeClaim = new V1PersistentVolumeClaim();
        volumeClaim.setMetadata(meta);
        volumeClaim.setSpec(spec);

        coreV1Api.createNamespacedPersistentVolumeClaim(claim.getNameSpace(), volumeClaim).execute();
    }

    public void deletePersistentVolume(String name) throws ApiException {
        coreV1Api.deletePersistentVolume(name).execute();
    }
}
