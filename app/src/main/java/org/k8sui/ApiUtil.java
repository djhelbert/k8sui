package org.k8sui;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1NodeList;

public class ApiUtil {
    private ApiUtil() {
    }

    public static V1NodeList nodeList() throws ApiException {
        return CoreApiSupplier.api().listNode().execute();
    }
}
