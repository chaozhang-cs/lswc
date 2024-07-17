package lswc.omst;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;

public class TreeUtils {
    static OptimizedNode findRoot(OptimizedNode u) {
        while (u.parent != null)
            u = u.parent;
        return u;
    }

    static Pair<OptimizedNode, Integer> findRootWithDist(OptimizedNode node) {
        int dist = 0;
        while (node.parent != null) {
            node = node.parent;
            dist++;
        }
        return new ObjectObjectMutablePair<>(node, dist);
    }

    static OptimizedNode reRoot(OptimizedNode n_w) {
        if (n_w.parent == null)
            return n_w;

        OptimizedNode ch = n_w;

        OptimizedNode cur = n_w.parent;
        long chWeight = n_w.treeEdgeWeight;

        n_w.parent = null;
        n_w.treeEdgeWeight = -1L;

        while (cur != null) {
            OptimizedNode curParent = cur.parent;
            long curWeight = cur.treeEdgeWeight;

            cur.parent = ch;
            cur.treeEdgeWeight = chWeight;

            ch = cur;
            chWeight = curWeight;
            cur = curParent;
        }

        while (ch.parent != null) {
            ch.size -= ch.parent.size;
            ch.parent.size += ch.size;
            ch = ch.parent;
        }

        return n_w;
    }

    static void unlink(OptimizedNode n_v) {
        // n_v is a non-root node
        OptimizedNode c = n_v;
        while (c.parent != null) {
            c = c.parent;
            c.size -= n_v.size;
        }
        assert n_v.parent != null;
//        n_v.parent.children.remove(n_v);    // deprecated for optimized implementation
        n_v.parent = null;
        n_v.treeEdgeWeight = -1L;
    }

    static void delete(OptimizedNode n_u, OptimizedNode n_v) {
        OptimizedNode ch;
        if (n_u.parent == n_v)
            ch = n_u;
        else
            ch = n_v;
        unlink(ch);
    }

    // Method to find the depth of a node
    static int findDepth(OptimizedNode node) {
        int depth = 0;
        while (node.parent != null) {
            node = node.parent;
            depth++;
        }
        return depth;
    }

    private static void compareEdgeWeight(OptimizedNode ch, OptimizedTreeEdge edge) {
        if (ch.treeEdgeWeight < edge.weight) {
            edge.weight = ch.treeEdgeWeight;
            edge.source = ch;
            edge.target = ch.parent;
        }
    }

    // Method to find LCA without preprocessing
    static void findLCA(OptimizedNode n1, OptimizedNode n2, OptimizedTreeEdge minimumEdge) {
        minimumEdge.weight = Long.MAX_VALUE;
        // Find the depths of the two nodes
        int depth1 = findDepth(n1);
        int depth2 = findDepth(n2);

        // Bring n1 and n2 to the same depth
        while (depth1 > depth2) {
            compareEdgeWeight(n1, minimumEdge);
            n1 = n1.parent;
            depth1--;
        }
        while (depth2 > depth1) {
            compareEdgeWeight(n2, minimumEdge);
            n2 = n2.parent;
            depth2--;
        }

        // Move both nodes up until they meet
        while (n1 != n2) {
            compareEdgeWeight(n1, minimumEdge);
            n1 = n1.parent;

            compareEdgeWeight(n2, minimumEdge);
            n2 = n2.parent;
        }
    }
}
