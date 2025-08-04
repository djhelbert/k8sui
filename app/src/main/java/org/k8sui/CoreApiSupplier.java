package org.k8sui;

import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

public class CoreApiSupplier {
    private static final CoreV1Api api = new CoreV1Api(ApiClientSupplier.client());
    private static final AppsV1Api apps = new AppsV1Api(ApiClientSupplier.client());

    private CoreApiSupplier() {
    }

    public static CoreV1Api api() {
        return api;
    }

    public static AppsV1Api app() {
        return apps;
    }
}
