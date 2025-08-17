package org.k8sui.service;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSource;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeSpec;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.PersistentVolume;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class PersistentVolumeService {
    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

    public List<PersistentVolume> listPersistentVolumes() throws ApiException {
        var list = coreV1Api.listPersistentVolume().execute();

        return list.getItems().stream().map(pv -> {
            var persistentVolume = new PersistentVolume();
            persistentVolume.setUid(pv.getMetadata().getUid());
            persistentVolume.setName(pv.getMetadata().getName());
            persistentVolume.setStorageClassName(pv.getSpec().getStorageClassName());
            persistentVolume.setPersistentVolumeReclaimPolicy(pv.getSpec().getPersistentVolumeReclaimPolicy());
            persistentVolume.setLabels(pv.getMetadata().getLabels());

            final Map<String, String> capacities = new HashMap<>();
            final Map<String, Quantity> capacityMap = pv.getSpec().getCapacity();

            for (String key : capacityMap.keySet()) {
                capacities.put(key, capacityMap.get(key).toSuffixedString());
            }

            persistentVolume.setCapacities(capacities);
            persistentVolume.setStatus(pv.getStatus().getPhase());

            List<String> accessModes = pv.getSpec().getAccessModes();
            persistentVolume.setAccessModes(accessModes);

            var hostPath = pv.getSpec().getHostPath();
            if(hostPath != null) {
                persistentVolume.setHostPath(hostPath.getPath());
            }
            return persistentVolume;
        }).collect(toList());
    }

    public void createPersistentVolume(PersistentVolume volume) throws ApiException {
        var meta = new V1ObjectMeta();
        meta.setName(volume.getName());
        meta.setCreationTimestamp(OffsetDateTime.now());
        meta.setLabels(volume.getLabels());

        var spec = new V1PersistentVolumeSpec();
        spec.setAccessModes(volume.getAccessModes());
        spec.setPersistentVolumeReclaimPolicy(volume.getPersistentVolumeReclaimPolicy());
        spec.setStorageClassName(volume.getStorageClassName());

        var hostPath = new V1HostPathVolumeSource();
        hostPath.setPath(volume.getHostPath());

        spec.setHostPath(hostPath);

        var v1pv = new V1PersistentVolume();
        v1pv.setMetadata(meta);
        v1pv.setSpec(spec);

        final Map<String, Quantity> capacityMap = new HashMap<>();

        for (String key : volume.getCapacities().keySet()) {
            capacityMap.put(key, new Quantity(volume.getCapacities().get(key)));
        }

        spec.setCapacity(capacityMap);

        coreV1Api.createPersistentVolume(v1pv).execute();
    }

    public void deletePersistentVolume(String name) throws ApiException {
        coreV1Api.deletePersistentVolume(name).execute();
    }
}
