package org.k8sui.ui;

/**
 * Name Space Observer
 */
@FunctionalInterface
public interface NameSpaceObserver {
    /**
     * Name Space Change
     * @param namespace Name Space Name
     * @param nameSpaceOperation Operation
     */
    void nameSpaceChange(String namespace, NameSpaceOperation nameSpaceOperation);
}
