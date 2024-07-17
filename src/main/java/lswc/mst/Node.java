package lswc.mst;

import java.util.HashSet;
import java.util.Set;

public class Node {
    Node parent;
    Set<Node> children;
    Set<Node> nte;
    final int val;
    int size;
    long treeEdgeWeight;

    public Node(int v) {
        this.parent = null;
        this.children = new HashSet<>();
        this.nte = new HashSet<>();
        this.val = v;
        this.size = 1;
        this.treeEdgeWeight = Long.MAX_VALUE; // default weight
    }

    @Override
    public String toString() {
        return "DTNode{" +
                "val=" + val +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node dtNode = (Node) o;

        return val == dtNode.val;
    }

    @Override
    public int hashCode() {
        return val;
    }
}
