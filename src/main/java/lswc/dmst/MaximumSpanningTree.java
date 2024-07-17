package lswc.dmst;

public interface MaximumSpanningTree {
    boolean query(int u, int v);

    Node getNode(int u);

    Node findRoot(Node u);

    // Insert an edge into the spanning tree
    void insertTreeEdge(Node u, Node v, long weight);

    // Delete an edge from the spanning tree
    void deleteTreeEdge(int u, int v);

    void insertNonTreeEdge(Node u, Node v, long weight);

    void deleteNonTreeEdge(int u, int v);

    void replace(TreeEdge minimumEdge, Node newEdgeSource, Node newEdgeTarget, long newEdgeWeight);

    boolean isTreeEdge(int u, int v);

    // Find the minimum edge from the following two paths:
    // Path u to u's root
    // Path v to v's root
    TreeEdge findMinimum(Node u, Node v);

    long memoryConsumption();
}
