package lswc.omst;

public interface OptimizedMaximumSpanningTree {
    boolean query(int u, int v);

    OptimizedNode findRoot(OptimizedNode u);

    OptimizedNode getNode(int u);

    // Insert an edge into the spanning tree
    void insertTreeEdge(OptimizedNode u, OptimizedNode v, long weight);

    // Delete an edge from the spanning tree
    void deleteTreeEdge(int u, int v);

    void replace(OptimizedTreeEdge minimumEdge, OptimizedNode newEdgeSource, OptimizedNode newEdgeTarget, long newEdgeWeight);

    void reshape(OptimizedNode u, OptimizedNode v, long weight);

    boolean isTreeEdge(int u, int v);

    // Find the minimum edge from the following two paths:
    // Path u to u's root
    // Path v to v's root
    OptimizedTreeEdge findMinimum(OptimizedNode u, OptimizedNode v);

    long memoryConsumption();
}
