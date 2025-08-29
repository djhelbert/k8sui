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

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec;
import io.kubernetes.client.openapi.models.V1VolumeResourceRequirements;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.k8sui.CoreApiSupplier;
import org.k8sui.model.PersistentVolumeClaim;

/**
 * Persistent Volume Claim Service
 */
@Log4j2
public class PersistentVolumeClaimService {

  private final CoreV1Api coreV1Api = CoreApiSupplier.api();

  /**
   * List Persistent Volume Claim Names
   *
   * @param nameSpace Name Space
   * @return String List
   */
  public List<String> listPersistentVolumeClaimNames(String nameSpace) {
    V1PersistentVolumeClaimList list = null;

    try {
      list = coreV1Api.listNamespacedPersistentVolumeClaim(nameSpace).execute();
    } catch (ApiException e) {
      log.error("PersistentVolumeClaimService", e);
    }

    if (list != null) {
      return list.getItems().stream().map(pv -> pv.getMetadata() != null ? pv.getMetadata()
          .getName() : null).toList();
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * List Persistent Volume Claims by Name Space
   *
   * @param nameSpace Name Space
   * @return PVC List
   * @throws ApiException API Exception
   */
  public List<PersistentVolumeClaim> listPersistentVolumeClaims(String nameSpace)
      throws ApiException {
    var list = coreV1Api.listNamespacedPersistentVolumeClaim(nameSpace).execute();

    return list.getItems().stream().map(pv -> {
      var persistentVolumeClaim = new PersistentVolumeClaim();

      if (pv.getMetadata() != null) {
        persistentVolumeClaim.setUid(pv.getMetadata().getUid());
        persistentVolumeClaim.setName(pv.getMetadata().getName());
        persistentVolumeClaim.setNameSpace(pv.getMetadata().getNamespace());
        persistentVolumeClaim.setLabels(pv.getMetadata().getLabels());
        persistentVolumeClaim.setAnnotations(pv.getMetadata().getAnnotations());
        persistentVolumeClaim.setCreation(pv.getMetadata().getCreationTimestamp());
      }

      if (pv.getSpec() != null) {
        persistentVolumeClaim.setStorageClassName(pv.getSpec().getStorageClassName());

        final Map<String, String> resources = new HashMap<>();
        final Map<String, Quantity> resourceMap;

        if (pv.getSpec().getResources() != null) {
          resourceMap = pv.getSpec().getResources().getRequests();

          if (resourceMap != null) {
            for (String key : resourceMap.keySet()) {
              resources.put(key, resourceMap.get(key).toSuffixedString());
            }
          }
        }

        persistentVolumeClaim.setCapacities(resources);
      }

      if (pv.getStatus() != null) {
        persistentVolumeClaim.setStatus(pv.getStatus().getPhase());
      }

      List<String> accessModes = pv.getSpec().getAccessModes();
      persistentVolumeClaim.setAccessModes(accessModes);

      return persistentVolumeClaim;
    }).collect(toList());
  }

  /**
   * Create Persistent Volume Claim
   *
   * @param claim PVC
   * @throws ApiException API Exception
   */
  public void createPersistentVolumeClaim(PersistentVolumeClaim claim) throws ApiException {
    var meta = new V1ObjectMeta();
    meta.setName(claim.getName());
    meta.setNamespace(claim.getNameSpace());
    meta.setCreationTimestamp(OffsetDateTime.now());
    meta.setLabels(claim.getLabels());

    var spec = new V1PersistentVolumeClaimSpec();
    spec.setAccessModes(claim.getAccessModes());

    final Map<String, Quantity> resourceMap = new HashMap<>();

    for (String key : claim.getCapacities().keySet()) {
      resourceMap.put(key, new Quantity(claim.getCapacities().get(key)));
    }

    var requirements = new V1VolumeResourceRequirements();
    requirements.setRequests(resourceMap);

    spec.setResources(requirements);

    var volumeClaim = new V1PersistentVolumeClaim();
    volumeClaim.setMetadata(meta);
    volumeClaim.setSpec(spec);

    coreV1Api.createNamespacedPersistentVolumeClaim(claim.getNameSpace(), volumeClaim).execute();
  }

  /**
   * Delete Persistent Volume Claim
   *
   * @param nameSpace Name Space
   * @param name      Name
   * @throws ApiException API Exception
   */
  public void deletePersistentVolumeClaim(String nameSpace, String name) throws ApiException {
    coreV1Api.deleteNamespacedPersistentVolumeClaim(name, nameSpace).execute();
  }
}
