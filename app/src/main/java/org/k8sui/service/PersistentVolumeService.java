package org.k8sui.service;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.PersistentVolume;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class PersistentVolumeService {
    private final CoreV1Api coreV1Api = CoreApiSupplier.api();

    public List<PersistentVolume> listPersistentVolumes() throws ApiException {
        var list = coreV1Api.listPersistentVolume().execute();

        final List<PersistentVolume> volumes = list.getItems().stream().map(pv -> {
            var persistentVolume = new PersistentVolume();
            persistentVolume.setUid(pv.getMetadata().getUid());
            persistentVolume.setName(pv.getMetadata().getName());
            persistentVolume.setStorageClassName(pv.getSpec().getStorageClassName());
            persistentVolume.setNameSpace(pv.getMetadata().getNamespace());
            persistentVolume.setPersistentVolumeReclaimPolicy(pv.getSpec().getPersistentVolumeReclaimPolicy());

            final Map<String, String> capacities = new HashMap<>();
            final Map<String, Quantity> capacityMap = pv.getSpec().getCapacity();

            for (String key : capacityMap.keySet()) {
                capacities.put(key, capacityMap.get(key).toSuffixedString());
            }

            persistentVolume.setCapacities(capacities);

            List<String> accessModes = pv.getSpec().getAccessModes();
            persistentVolume.setAccessModes(accessModes);

            var hostPath = pv.getSpec().getHostPath();
            persistentVolume.setHostPath(hostPath.getPath());

            return persistentVolume;
        }).collect(toList());

        return volumes;
    }

    public void deletePersistentVolume(String name) throws ApiException {
        coreV1Api.deletePersistentVolume(name).execute();
    }
}
