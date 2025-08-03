package org.k8sui;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class ApiClientSupplier {
    private static final ApiClient client;

    static {
        try {
            client = Config.defaultClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApiClient client() {
        return client;
    }
}
