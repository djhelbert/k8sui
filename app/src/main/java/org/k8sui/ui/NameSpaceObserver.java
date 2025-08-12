package org.k8sui.ui;

@FunctionalInterface
public interface NameSpaceObserver {
    void nameSpaceChange(String namespace, NameSpaceOperation nameSpaceOperation);
}
