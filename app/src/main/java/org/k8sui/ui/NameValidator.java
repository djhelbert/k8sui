package org.k8sui.ui;

/**
 * A general set of rules applies to most resource names.
 * Length        : Typically, names must be no more than 253 characters long.
 * Characters    : Names generally consist of lowercase alphanumeric characters, hyphens (-), and dots (.).
 * Start and End : Names must begin and end with an alphanumeric character.
 */
public class NameValidator {
    private static final String PATTERN = "^[a-z0-9][a-z0-9-.]*[a-z0-9]$";

    private NameValidator() {
    }

    public static boolean validName(String name) {
        return name.length() <= 253 && name.matches(PATTERN);
    }
}
