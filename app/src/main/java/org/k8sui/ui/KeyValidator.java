package org.k8sui.ui;

public class KeyValidator {
    private static final String KEY_PATTERN = "^[a-zA-Z0-9-._]+$";

    private KeyValidator() {
    }

    public static boolean validName(String name) {
        return name.length() <= 253 && name.matches(KEY_PATTERN);
    }
}
