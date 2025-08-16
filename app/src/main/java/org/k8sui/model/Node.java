package org.k8sui.model;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
@ToString
public class Node implements Comparable<Node> {
    private String uid;
    private String name;
    private String cpu;
    private String image;
    private String ip;
    private String memory;
    private Map<String, String> labels;

    public Node(String uid, String name, String cpu) {
        this.uid = uid;
        this.name = name;
        this.cpu = cpu;
    }

    @Override
    public int compareTo(@NotNull Node node) {
        return name.compareTo(node.getName());
    }
}
