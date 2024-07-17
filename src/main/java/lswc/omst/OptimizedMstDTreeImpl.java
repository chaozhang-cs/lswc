package lswc.omst;


import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.openjdk.jol.info.GraphLayout;

// Implementation of optimized dynamic trees without the need of maintaining non-tree edges and children
// The implementation has the techniques: (1) re-rooting; (2) maintaining distance to root
public class OptimizedMstDTreeImpl implements OptimizedMaximumSpanningTree {
    private final Int2ObjectOpenHashMap<OptimizedNode> nodeHashMap;
    private final OptimizedTreeEdge weightedSpanningTreeEdge;

    public OptimizedMstDTreeImpl() {
        this.nodeHashMap = new Int2ObjectOpenHashMap<>();
        this.weightedSpanningTreeEdge = new OptimizedTreeEdge();
    }

    @Override
    public boolean query(int u, int v) {
        OptimizedNode nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);

        if (nodeU == null || nodeV == null)
            return false;

        return query(nodeU, nodeV);
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
        OptimizedNode nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);
        if (!(nodeU == null || nodeV == null))
            TreeUtils.delete(nodeU, nodeV);
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
        reshapeIml(u, v, weight);
    }

    @Override
    public boolean isTreeEdge(int u, int v) {
        OptimizedNode nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);
        if (!(nodeU == null || nodeV == null)) {
            // neither u nor v is null
            return nodeU.parent == nodeV || nodeV.parent == nodeU;
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
    // Specific to the optimized d-tree because of re-rooting
    private static boolean query(OptimizedNode n_u, OptimizedNode n_v) {
        OptimizedNode d_u = null;
        while (n_u.parent != null) {
            d_u = n_u;
            n_u = n_u.parent;
        }

        OptimizedNode d_v = null;
        while (n_v.parent != null) {
            d_v = n_v;
            n_v = n_v.parent;
        }

        boolean ret = n_u.equals(n_v);

        if (d_u != null && d_u.size > n_u.size / 2)
            TreeUtils.reRoot(d_u);

        if (d_v != null && d_v.size > n_v.size / 2)
            TreeUtils.reRoot(d_v);

        return ret;
    }

    // Specific to the optimized d-tree because of re-rooting
    private static void link(OptimizedNode n_u, OptimizedNode r_u, OptimizedNode n_v, long edgeWeight) {
        n_v.parent = n_u;
        n_v.treeEdgeWeight = edgeWeight;

        OptimizedNode c = n_u;
        OptimizedNode new_root = null;
        while (c != null) {
            c.size += n_v.size;

            if (c.size > (r_u.size + n_v.size) / 2 && new_root == null && c.parent != null)
                new_root = c;

            c = c.parent;
        }
        if (new_root != null)
            TreeUtils.reRoot(new_root);
    }

    // assuming u and v are not connected, i.e., the case of inserting tree edge
    // Specific of the optimized d-tree because of the link operation
    private static void insert(OptimizedNode n_u, OptimizedNode n_v, long edgeWeight) {
        OptimizedNode r_u = TreeUtils.findRoot(n_u), r_v = TreeUtils.findRoot(n_v);
        // T1 includes v, T2 includes u
        if (r_v.size < r_u.size)
            link(n_u, r_u, TreeUtils.reRoot(n_v), edgeWeight);
        else
            link(n_v, r_v, TreeUtils.reRoot(n_u), edgeWeight);
    }

    private static void reshapeIml(OptimizedNode n_u, OptimizedNode n_v, long weight) {
        Pair<OptimizedNode, Integer> pairU = TreeUtils.findRootWithDist(n_u), pairV = TreeUtils.findRootWithDist(n_v);
        int dist_u = pairU.second(), dist_v = pairV.second();
        OptimizedNode r = pairU.first();

        if (Math.abs(dist_u - dist_v) < 2)   // No changes to BFS spanning tree
            return;

        OptimizedNode longNode, shortNode;
        if (dist_u < dist_v) {
            longNode = n_v;
            shortNode = n_u;
        } else {
            longNode = n_u;
            shortNode = n_v;
        }

        int delta = Math.abs(dist_u - dist_v) - 2;
        OptimizedNode c = longNode;

        for (int i = 1; i < delta; i++)
            c = c.parent;

        TreeUtils.unlink(c);
        link(shortNode, r, TreeUtils.reRoot(longNode), weight);
    }
}
