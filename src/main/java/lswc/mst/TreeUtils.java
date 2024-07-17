package lswc.mst;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;

public class TreeUtils {
    static boolean query(Node n_u, Node n_v) {
        Node d_u = null;
        while (n_u.parent != null) {
            d_u = n_u;
            n_u = n_u.parent;
        }

        Node d_v = null;
        while (n_v.parent != null) {
            d_v = n_v;
            n_v = n_v.parent;
        }

        boolean ret = n_u.equals(n_v);

        if (d_u != null && d_u.size > n_u.size / 2)
            reRoot(d_u);

        if (d_v != null && d_v.size > n_v.size / 2)
            reRoot(d_v);

        return ret;
    }

    static Node reRoot(Node n_w) {
        if (n_w.parent == null)
            return n_w;

        Node ch = n_w;

        Node cur = n_w.parent;
        long chWeight = n_w.treeEdgeWeight;

        n_w.parent = null;
        n_w.treeEdgeWeight = -1L;

        while (cur != null) {
            Node curParent = cur.parent;
            long curWeight = cur.treeEdgeWeight;

            cur.parent = ch;
            cur.treeEdgeWeight = chWeight;

            cur.children.remove(ch);
            ch.children.add(cur);

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

    static Node findRoot(Node u) {
        while (u.parent != null)
            u = u.parent;
        return u;
    }

    static Pair<Node, Integer> findRootWithDist(Node node) {
        int dist = 0;
        while (node.parent != null) {
            node = node.parent;
            dist++;
        }
        return new ObjectObjectMutablePair<>(node, dist);
    }

    static void link(Node n_u, Node r_u, Node n_v, long edgeWeight) {
        n_v.parent = n_u;
        n_u.children.add(n_v);

        n_v.treeEdgeWeight = edgeWeight;

        Node c = n_u;
        Node new_root = null;
        while (c != null) {
            c.size += n_v.size;

            if (c.size > (r_u.size + n_v.size) / 2 && new_root == null && c.parent != null)
                new_root = c;

            c = c.parent;
        }
        if (new_root != null)
            reRoot(new_root);
    }

    // unlinking v from its parent
    static void unlink(Node n_v) {
        // n_v is a non-root node
        Node c = n_v;
        while (c.parent != null) {
            c = c.parent;
            c.size -= n_v.size;
        }
        assert n_v.parent != null;
        n_v.parent.children.remove(n_v);
        n_v.parent = null;
        n_v.treeEdgeWeight = -1L;
    }

    // assuming u and v are not connected, i.e., the case of inserting tree edge
    static void insertTreeEdge(Node n_u, Node n_v, long edgeWeight) {
        Node r_u = findRoot(n_u), r_v = findRoot(n_v);
        // T1 includes v, T2 includes u
        if (r_v.size < r_u.size)
            link(n_u, r_u, reRoot(n_v), edgeWeight);
        else
            link(n_v, r_v, reRoot(n_u), edgeWeight);
    }

    static void insertNonTreeEdgeWithoutReshaping(Node n_u, Node n_v) {
        n_u.nte.add(n_v);
        n_v.nte.add(n_u);
    }

    static void insertNonTreeEdgeWithReshaping(Node n_u, Node n_v, long weight) {
        if (n_u.nte.contains(n_v) && n_v.nte.contains(n_u))
            return;

        Pair<Node, Integer> pairU = findRootWithDist(n_u), pairV = findRootWithDist(n_v);
        int dist_u = pairU.second(), dist_v = pairV.second();
        Node r = pairU.first();

        if (Math.abs(dist_u - dist_v) < 2) {  // No changes to BFS spanning tree
            n_u.nte.add(n_v);
            n_v.nte.add(n_u);
        } else {
            Node longNode, shortNode;
            if (dist_u < dist_v) {
                longNode = n_v;
                shortNode = n_u;
            } else {
                longNode = n_u;
                shortNode = n_v;
            }

            int delta = Math.abs(dist_u - dist_v) - 2;
            Node c = longNode;

            for (int i = 1; i < delta; i++)
                c = c.parent;

            c.parent.nte.add(c);
            c.nte.add(c.parent);
            unlink(c);

            link(shortNode, r, reRoot(longNode), weight);
        }

    }

    static void deleteTreeEdge(Node n_u, Node n_v) {
        Node ch;
        if (n_u.parent == n_v)
            ch = n_u;
        else
            ch = n_v;
        unlink(ch);
    }

    static void deleteNonTreeEdge(Node n_u, Node n_v) {
        n_u.nte.remove(n_v);
        n_v.nte.remove(n_u);
    }

    // Method to find the depth of a node
    static int findDepth(Node node) {
        int depth = 0;
        while (node.parent != null) {
            node = node.parent;
            depth++;
        }
        return depth;
    }

    private static void compareEdgeWeight(Node ch, TreeEdge edge) {
        if (ch.treeEdgeWeight < edge.weight) {
            edge.weight = ch.treeEdgeWeight;
            edge.source = ch;
            edge.target = ch.parent;
        }
    }

    // Method to find LCA without preprocessing
    static void findLca(Node n1, Node n2, TreeEdge minimumEdge) {
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
