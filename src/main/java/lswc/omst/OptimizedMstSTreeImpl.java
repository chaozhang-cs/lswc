package lswc.omst;


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.openjdk.jol.info.GraphLayout;


// Implementation of optimized dynamic trees without the need of maintaining non-tree edges
public class OptimizedMstSTreeImpl implements OptimizedMaximumSpanningTree {
    private final Int2ObjectOpenHashMap<OptimizedNode> nodeHashMap;
    private final OptimizedTreeEdge weightedSpanningTreeEdge;

    public OptimizedMstSTreeImpl() {
        this.nodeHashMap = new Int2ObjectOpenHashMap<>();
        this.weightedSpanningTreeEdge = new OptimizedTreeEdge();
    }

    @Override
    public boolean query(int u, int v) {
        OptimizedNode optimizedNodeU = nodeHashMap.get(u), optimizedNodeV = nodeHashMap.get(v);

        if (optimizedNodeU == null || optimizedNodeV == null)
            return false;

        return query(optimizedNodeU, optimizedNodeV);
    }

    @Override
    public OptimizedNode findRoot(OptimizedNode u) {
        return TreeUtils.findRoot(u);
    }

    @Override
    public OptimizedNode getNode(int u) {
        return nodeHashMap.computeIfAbsent(u, k -> new OptimizedNode(u));
    }

    @Override
    public void insertTreeEdge(OptimizedNode u, OptimizedNode v, long weight) {
        insert(u, v, weight);
    }

    @Override
    public void deleteTreeEdge(int u, int v) {
        OptimizedNode optimizedNodeU = nodeHashMap.get(u), optimizedNodeV = nodeHashMap.get(v);
        if (!(optimizedNodeU == null || optimizedNodeV == null))
            TreeUtils.delete(optimizedNodeU, optimizedNodeV);
    }

    @Override
    public void replace(OptimizedTreeEdge minimumEdge, OptimizedNode newEdgeSource, OptimizedNode newEdgeTarget, long newEdgeWeight) {
        // delete the minimum edge from the maximum spanning tree
        TreeUtils.delete(minimumEdge.source, minimumEdge.target);
        // insert the new edge as a tree edge into the MST
        insert(newEdgeSource, newEdgeTarget, newEdgeWeight);
    }

    @Override
    public void reshape(OptimizedNode u, OptimizedNode v, long weight) { // reshaping with a non-tree edge, d-tree specific
    }

    @Override
    public boolean isTreeEdge(int u, int v) {
        OptimizedNode optimizedNodeU = nodeHashMap.get(u), optimizedNodeV = nodeHashMap.get(v);
        if (!(optimizedNodeU == null || optimizedNodeV == null)) {
            // neither u nor v is null
            return optimizedNodeU.parent == optimizedNodeV || optimizedNodeV.parent == optimizedNodeU;
        }
        return false;
    }

    @Override
    public OptimizedTreeEdge findMinimum(OptimizedNode u, OptimizedNode v) {
        TreeUtils.findLCA(u, v, weightedSpanningTreeEdge);
        return weightedSpanningTreeEdge;
    }

    @Override
    public long memoryConsumption() {
        return GraphLayout.parseInstance(nodeHashMap).totalSize();
    }

    // Optimized DTree implementation with tree edge weight stored
    // Specific to the optimized simple tree because of no need of re-rooting
    private static boolean query(OptimizedNode n_u, OptimizedNode n_v) {
        while (n_u.parent != null)
            n_u = n_u.parent;

        while (n_v.parent != null)
            n_v = n_v.parent;

        return n_u == n_v;
    }

    // Specific to the optimized simple tree because of no need of re-rooting
    private static void link(OptimizedNode n_u, OptimizedNode n_v, long edgeWeight) {
        n_v.parent = n_u;
        n_v.treeEdgeWeight = edgeWeight;
        OptimizedNode c = n_u;

        while (c != null) {
            c.size += n_v.size;
            c = c.parent;
        }
    }

    // assuming u and v are not connected, i.e., the case of inserting tree edge
    // Specific of the simple tree because of the link operation
    private static void insert(OptimizedNode n_u, OptimizedNode n_v, long edgeWeight) {
        OptimizedNode r_u = TreeUtils.findRoot(n_u), r_v = TreeUtils.findRoot(n_v);
        // T1 includes v, T2 includes u
        if (r_v.size < r_u.size)
            link(n_u, TreeUtils.reRoot(n_v), edgeWeight);
        else
            link(n_v, TreeUtils.reRoot(n_u), edgeWeight);
    }
}
