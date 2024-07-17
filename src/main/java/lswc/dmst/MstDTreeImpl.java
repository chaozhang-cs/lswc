package lswc.dmst;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.openjdk.jol.info.GraphLayout;

public class MstDTreeImpl implements MaximumSpanningTree {
    private final Int2ObjectOpenHashMap<Node> nodeHashMap;
    private final TreeEdge treeEdge;

    public MstDTreeImpl() {
        this.nodeHashMap = new Int2ObjectOpenHashMap<>();
        this.treeEdge = new TreeEdge();
    }

    @Override
    public boolean query(int u, int v) {
        Node nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);

        if (nodeU == null || nodeV == null)
            return false;

        return TreeUtils.query(nodeU, nodeV);
    }

    @Override
    public Node getNode(int u) {
        return nodeHashMap.computeIfAbsent(u, k -> new Node(u));
    }

    @Override
    public Node findRoot(Node u) {
        return TreeUtils.findRoot(u);
    }


    @Override
    public void insertTreeEdge(Node u, Node v, long weight) {
        TreeUtils.insertTreeEdge(u, v, weight);
    }

    @Override
    public void insertNonTreeEdge(Node u, Node v, long weight) {
        TreeUtils.insertNonTreeEdgeWithReshaping(u, v, weight);
    }

    @Override
    public void deleteTreeEdge(int u, int v) {
        Node nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);
        if (!(nodeU == null || nodeV == null)) {
            TreeUtils.deleteTreeEdge(nodeU, nodeV);

            cleanUpNode(nodeU);
            cleanUpNode(nodeV);
        }
    }

    @Override
    public void deleteNonTreeEdge(int u, int v) {
        Node nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);
        if (!(nodeU == null || nodeV == null)) {
            TreeUtils.deleteNonTreeEdge(nodeU, nodeV);

            cleanUpNode(nodeU);
            cleanUpNode(nodeV);
        }
    }

    private void cleanUpNode(Node node){
        if (node.parent == null && node.children.isEmpty() && node.nte.isEmpty())
            nodeHashMap.remove(node.val, node);
    }

    @Override
    public void replace(TreeEdge minimumEdge, Node newEdgeSource, Node newEdgeTarget, long newEdgeWeight) {
        // delete the minimum edge from the maximum spanning tree
        // insert the deleted edge as non-tree edge
        TreeUtils.deleteTreeEdge(minimumEdge.source, minimumEdge.target);
        TreeUtils.insertNonTreeEdgeWithoutReshaping(minimumEdge.source, minimumEdge.target);

        // insert the new edge as a tree edge into the MST
        insertTreeEdge(newEdgeSource, newEdgeTarget, newEdgeWeight);
    }

    @Override
    public boolean isTreeEdge(int u, int v) {
        Node nodeU = nodeHashMap.get(u), nodeV = nodeHashMap.get(v);
        if (!(nodeU == null || nodeV == null)) {
            // neither u nor v is null
            return nodeU.parent == nodeV || nodeV.parent == nodeU;
        }
        return false;
    }

    @Override
    public TreeEdge findMinimum(Node u, Node v) {
        TreeUtils.findLca(u, v, treeEdge);
        return treeEdge;
    }

    @Override
    public long memoryConsumption() {
        return GraphLayout.parseInstance(nodeHashMap).totalSize();
    }
}
