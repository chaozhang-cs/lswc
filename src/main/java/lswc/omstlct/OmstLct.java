package lswc.omstlct;

public interface OmstLct {
    boolean query(int u, int v);

    boolean query(LctNode u, LctNode v);

    LctNode getNode(int u);

    void insertTreeEdge(LctNode u, LctNode v, long weight);

    void deleteTreeEdge(int u, int v);

    void replace(RepTreeEdge minimumEdge, LctNode newEdgeSource, LctNode newEdgeTarget, long newEdgeWeight);

    boolean isTreeEdge(int u, int v);

    RepTreeEdge findMinimum(LctNode u, LctNode v);

    long memoryConsumption();
}
