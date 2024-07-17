package lswc.omstlct;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayDeque;

public class LinkCutTreeUtils {

    public static boolean connected(LctNode u, LctNode v) {
        access(u);
        access(v);
        return u.pathParent != null || u.parent != null;
    }

    // Link-Cut Tree Method used to find Lowest Common Ancestor
    static LctNode access(LctNode v) {
        SplayTreeUtils.splay(v); // Make x be the root of the splay tree containing x

        // If x has a right child, detach it and set its pathParent to x.
        SplayTreeUtils.detachRight(v);

        LctNode last = v;

        // Traverse the path parents and perform splay operations.
        while (v.pathParent != null) {
            LctNode w = v.pathParent;

            SplayTreeUtils.splay(w);

            // Detach the existing right child of pathParent, if any.
            SplayTreeUtils.detachRight(w);
            // Make v the right child of its path parent.
            SplayTreeUtils.setRightChild(w, v);

            last = w;

            // Splay x again to keep it at the root of the auxiliary tree.
            SplayTreeUtils.splay(v);
        }

        return last;
    }

    public static void findMinimumEdgeInCycleOf(LctNode n1, LctNode n2, RepTreeEdge minimumEdge, Int2IntOpenHashMap childParentMap, Int2ObjectOpenHashMap<LctNode> nodeHashMap) {
        minimumEdge.weight = Long.MAX_VALUE;

        access(n1);
        LctNode lca = access(n2);

        LctNode minCh, minPa;

//        LctNode parentOfLca = getRepParent(lca);
        int parentValue = childParentMap.getOrDefault(lca.value, Integer.MIN_VALUE);
        LctNode parentOfLca = parentValue == Integer.MIN_VALUE ? null : nodeHashMap.get(parentValue);

        if (lca == n1) {
            minCh = computeMinimumEdgeOfPath(parentOfLca, lca, n2);
        } else if (lca == n2) {
            minCh = computeMinimumEdgeOfPath(parentOfLca, lca, n1);
        } else {
            LctNode min1 = computeMinimumEdgeOfPath(parentOfLca, lca, n1);
            LctNode min2 = computeMinimumEdgeOfPath(parentOfLca, lca, n2);
            minCh = min1.repTreeEdgeWeight < min2.repTreeEdgeWeight ? min1 : min2;
        }
//        minPa = getRepParent(minCh);

//        int minPaValue = childParentMap.getOrDefault(minCh.value, Integer.MIN_VALUE);
//        minPa = minPaValue == Integer.MIN_VALUE ? null : nodeHashMap.get(minPaValue);

        minPa = nodeHashMap.get(childParentMap.get(minCh.value));

        minimumEdge.source = minCh;
        minimumEdge.target = minPa;
        minimumEdge.weight = minCh.repTreeEdgeWeight;
    }


    private static LctNode computeMinimumEdgeOfPath(LctNode parentOfFrom, LctNode from, LctNode to) {
        access(to);
        // If from is a root in the representing tree, then from is leftmost node in the splay tree representing the path (from, ..., to), where to is the root in the splay tree
        // Such that returning the nodeWithTheMinWeight of node to
        if (parentOfFrom == null)
            return to.nodeWithTheMinWeight;
        // If from is not a root, i.e., from has a parent, then accessing parentOfFrom. Therefore, the path (from, ..., to) will be in a splay tree
        access(parentOfFrom);
        // Splaying from to make from the root of the splay tree. This operation is performed because the minimum edge in the path (from, ..., to) must exclude the information stored in from
        SplayTreeUtils.splay(from); // from is the leftmost node in this splay tree
        return from.right.nodeWithTheMinWeight;
    }

    public static LctNode getRepParent(LctNode child) {
        SplayTreeUtils.splay(child);
        LctNode ret = child.left;
        if (ret == null) {
            if (child.pathParent != null)
                return child.pathParent;
            else
                return null;
        }
        while (ret.right != null)
            ret = ret.right;
        return ret;
    }

    public static LctNode findRoot(LctNode v) {
        access(v);
        LctNode root = SplayTreeUtils.findRoot(v);
        SplayTreeUtils.splay(root); // now v points to the root, splaying v makes the visiting the root faster
        return root;
    }

    public static void link(LctNode v, LctNode w, long weight, Int2IntOpenHashMap childParentMap) {
        if (w.parent == null && w.pathParent == null && w.left == null) { // w is a root in its representing tree
            access(w);// w is the only node in its splay tree
            access(v);

            w.left = v; // w is deeper than v in the splay tree, i.e., w is a child of v in representing tree
            v.parent = w;

            // update path parent pointer
            w.pathParent = v.pathParent;
            v.pathParent = null;

            w.repTreeEdgeWeight = weight;
            SplayTreeUtils.updateAgg(w);

            childParentMap.put(w.value, v.value);
        } else if (v.parent == null && v.pathParent == null && v.left == null) {  // v is a root in its representing tree
            access(v);// v is the only node in its splay tree
            access(w);

            v.left = w; // v is deeper than w in the splay tree, i.e., v is a child of w in representing tree
            w.parent = v;

            // update path parent pointer
            v.pathParent = w.pathParent;
            w.pathParent = null;

            v.repTreeEdgeWeight = weight;
            SplayTreeUtils.updateAgg(v);

            childParentMap.put(v.value, w.value);
        } else {
            access(v);
            access(w);

            LctNode small, large;
            if (v.size > w.size) {
                small = w;
                large = v;
            } else {
                small = v;
                large = w;
            }

            reRoot(small, childParentMap);
            access(small);// small is the only node in its splay tree

            small.left = large; // small is deeper than large in the splay tree, i.e., small is a child of large in representing tree
            large.parent = small;

            // update path parent pointer
            small.pathParent = large.pathParent;
            large.pathParent = null;


            small.repTreeEdgeWeight = weight;
            SplayTreeUtils.updateAgg(small);


            childParentMap.put(small.value, large.value);
        }
    }

    private static void reRoot(LctNode v, Int2IntOpenHashMap childParentMap) {
        access(v); // v is the root of the splay tree; v's right child is null
        // after access, v is the deepest node in v's preferred path
        // then, reverse the splay tree containing v will make v the root
        SplayTreeUtils.reverseTree(v);

        // updating the tree
        inorder(v, childParentMap);   // updating reTreeEdgeWeight
        postorder(v); // postorder traversal for updating agg
    }

    private static void inorder(LctNode v, Int2IntOpenHashMap childParent) {
        // inorder traversal for shifting repTreeEdgeWeight
        ArrayDeque<LctNode> stack = new ArrayDeque<>();
        LctNode curr = v, prev = null;
        long prevWeight = Long.MAX_VALUE, temp;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                SplayTreeUtils.push(curr);
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();
            temp = curr.repTreeEdgeWeight;
            curr.repTreeEdgeWeight = prevWeight;
            prevWeight = temp;

            // ***start*** change the child parent relationship
            if (prev != null) {
                // before re-rooting: child-parent is prev-curr, which needs to be deleted
                childParent.remove(prev.value, curr.value);
                // after re-rooting: child-parent is curr-prev, which needs to be added
                childParent.put(curr.value, prev.value);
            }
            prev = curr;
            // ***end*** change the child parent relationship

            curr = curr.right;
        }
    }

    private static void postorder(LctNode root) {
        ArrayDeque<LctNode> stack = new ArrayDeque<>(), result = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            LctNode node = stack.pop();
            result.push(node);

            if (node.left != null)
                stack.push(node.left);

            if (node.right != null)
                stack.push(node.right);
        }

        while (!result.isEmpty()) {
            SplayTreeUtils.updateAgg(result.pop());
        }
    }

    // cut node from node's parent
    public static void cut(LctNode v) {
        access(v);
        if (v.left != null) {
            v.left.parent = null;
            v.left = null; // v is the only node in its splay tree

            v.resetEdgeWeight();
            v.nodeWithTheMinWeight = null;
        }
    }

    private static class SplayTreeUtils {
        static void rotate(LctNode x) {
            LctNode parent = x.parent;

            if (parent == null)
                return;

            LctNode grandParent = parent.parent;

            if (x == parent.left) { // zig (right rotation)
                parent.left = x.right;

                if (x.right != null)
                    x.right.parent = parent;

                x.right = parent;
            } else { // zag (left rotation)
                parent.right = x.left;

                if (x.left != null)
                    x.left.parent = parent;

                x.left = parent;
            }

            parent.parent = x;

            if (grandParent == null) {
                x.parent = null;
            } else {
                x.parent = grandParent;
                if (grandParent.left == parent)
                    grandParent.left = x;
                else
                    grandParent.right = x;
            }

            if (parent.pathParent != null) {
                x.pathParent = parent.pathParent;
                parent.pathParent = null;
            }
            updateAgg(parent);
        }

        static void splay(LctNode x) {
            while (x.parent != null) {
                LctNode xParent = x.parent;
                LctNode xGrandParent = xParent.parent;

                // zig-zig: left-left or right-right
                // zig-zag: left-right or right-left
                if (xGrandParent != null)
                    rotate((x == xParent.left) == (xParent == xGrandParent.left) ? xParent /*zig-zig*/ : x /*zig-zag*/);
                rotate(x);
            }
            updateAgg(x);
        }

        private static void push(LctNode x) {
            if (x.revert) {
                x.revert = false;
                LctNode temp = x.left;
                x.left = x.right;
                x.right = temp;
                if (x.left != null)
                    x.left.revert = !x.left.revert;
                if (x.right != null)
                    x.right.revert = !x.right.revert;
            }
        }

        static void reverseTree(LctNode v) {
            v.revert = !v.revert;
            push(v);
        }

        static LctNode findRoot(LctNode v) {
            while (v.left != null)
                v = v.left;
            return v;
        }

        static void updateAgg(LctNode x) {
            x.nodeWithTheMinWeight = x;
            x.size = 1;
            if (x.left != null) {
                x.nodeWithTheMinWeight = x.left.nodeWithTheMinWeight.repTreeEdgeWeight < x.nodeWithTheMinWeight.repTreeEdgeWeight ? x.left.nodeWithTheMinWeight : x.nodeWithTheMinWeight;
                x.size += x.left.size;
            }

            if (x.right != null) {
                x.nodeWithTheMinWeight = x.right.nodeWithTheMinWeight.repTreeEdgeWeight < x.nodeWithTheMinWeight.repTreeEdgeWeight ? x.right.nodeWithTheMinWeight : x.nodeWithTheMinWeight;
                x.size += x.right.size;
            }
        }

        static void detachRight(LctNode node) {
            if (node.right != null) {
                node.right.pathParent = node;
                node.right.parent = null;
                node.right = null;
            }
        }

        static void setRightChild(LctNode node, LctNode right) {
            node.right = right;
            right.parent = node;
            right.pathParent = null;
        }
    }
}
