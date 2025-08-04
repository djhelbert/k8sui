package org.k8sui.model;

public class Node {
    private String uid;
    private String name;
    private String cpu;

    public Node(String uid, String name, String cpu) {
        this.uid = uid;
        this.name = name;
        this.cpu = cpu;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    @Override
    public String toString() {
        return "Node[" + getUid() + ":" + getName() + "(" + getCpu() + " cores)]";
    }
}
