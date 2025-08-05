package org.k8sui.model;

import org.jetbrains.annotations.NotNull;

public class Node implements Comparable<Node> {
    private String uid;
    private String name;
    private String cpu;
    private String image;
    private String ip;
    private String memory;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "Node[" + getUid() + ":" + getName() + "(" + getCpu() + " cores," + memory + ")]";
    }

    @Override
    public int compareTo(@NotNull Node n) {
        return n.getName().compareTo(name);
    }
}
