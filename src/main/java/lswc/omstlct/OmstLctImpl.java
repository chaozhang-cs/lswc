package lswc.omstlct;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.openjdk.jol.info.GraphLayout;


public class OmstLctImpl implements OmstLct {
    private final Int2ObjectOpenHashMap<LctNode> nodeHashMap;
    private final RepTreeEdge weightedSpanningTreeEdge;

    private final Int2IntOpenHashMap childParentMap;

    public OmstLctImpl() {
        this.nodeHashMap = new Int2ObjectOpenHashMap<>();
        this.weightedSpanningTreeEdge = new RepTreeEdge();

        this.childParentMap = new Int2IntOpenHashMap();
    }

    @Override
    public boolean query(int u, int v) {
        LctNode nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);

        if (nodeU == null || nodeV == null)
            return false;

        return query(nodeU, nodeV);
    }

    @Override
    public boolean query(LctNode u, LctNode v) {
        return LinkCutTreeUtils.connected(u, v);
    }

    @Override
    public LctNode getNode(int u) {
        return nodeHashMap.computeIfAbsent(u, k -> new LctNode(u));
    }

    @Override
    public void insertTreeEdge(LctNode u, LctNode v, long weight) {
        LinkCutTreeUtils.link(u, v, weight, childParentMap);
    }

    @Override
    public void deleteTreeEdge(int u, int v) {
        LctNode nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);
        if (!(nodeU == null || nodeV == null)) {
            deleteTreeEdge(nodeU, nodeV);
        }
    }

    void deleteTreeEdge(LctNode nodeU, LctNode nodeV) {
        if (childParentMap.getOrDefault(nodeU.value, Integer.MIN_VALUE) == nodeV.value) {
            LinkCutTreeUtils.cut(nodeU);
            childParentMap.remove(nodeU.value);
        } else {
            LinkCutTreeUtils.cut(nodeV);
            childParentMap.remove(nodeV.value);
        }
    }

    @Override
    public void replace(RepTreeEdge minimumEdge, LctNode newEdgeSource, LctNode newEdgeTarget, long newEdgeWeight) {
        deleteTreeEdge(minimumEdge.source, minimumEdge.target);
        insertTreeEdge(newEdgeSource, newEdgeTarget, newEdgeWeight);
    }

    @Override
    public boolean isTreeEdge(int u, int v) {
        return childParentMap.getOrDefault(u, Integer.MIN_VALUE) == v || childParentMap.getOrDefault(v, Integer.MIN_VALUE) == u;
    }


    @Override
    public RepTreeEdge findMinimum(LctNode u, LctNode v) {
        LinkCutTreeUtils.findMinimumEdgeInCycleOf(u, v, weightedSpanningTreeEdge, childParentMap, nodeHashMap);
        return weightedSpanningTreeEdge;
    }

    @Override
    public long memoryConsumption() {
        return GraphLayout.parseInstance(nodeHashMap).totalSize() + GraphLayout.parseInstance(childParentMap).totalSize();
    }
}
