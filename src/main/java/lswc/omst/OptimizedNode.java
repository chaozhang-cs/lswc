package lswc.omst;

public class OptimizedNode {
    OptimizedNode parent;
    final int val;
    int size;
    long treeEdgeWeight;

    public OptimizedNode(int v) {
        this.parent = null;
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

        OptimizedNode dtNode = (OptimizedNode) o;

        return val == dtNode.val;
    }

    @Override
    public int hashCode() {
        return val;
    }
}