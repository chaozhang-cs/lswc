package lswc.omstlct;

public class LctNode {
    int value;

    LctNode left, right, parent, pathParent;
    long repTreeEdgeWeight; // weight is for the edge of the node to its parent in the represented tree

    boolean revert;

    LctNode nodeWithTheMinWeight;

    int size;

    public LctNode(int value) {
        this.value = value;

        this.left = null;
        this.right = null;
        this.parent = null;

        this.pathParent = null;

        this.repTreeEdgeWeight = Long.MAX_VALUE;

        this.revert = false;

        this.nodeWithTheMinWeight = null;

        this.size = 1;
    }

//    public LctNode(int value, long repTreeEdgeWeight) {
//        this.value = value;
//
//        this.left = null;
//        this.right = null;
//        this.parent = null;
//
//        this.pathParent = null;
//
//        this.repTreeEdgeWeight = repTreeEdgeWeight;
//
//        this.revert = false;
//
//        this.nodeWithTheMinWeight = null;
//    }

    public void resetEdgeWeight(){
        this.repTreeEdgeWeight = Long.MAX_VALUE;
        this.nodeWithTheMinWeight = null;
    }
}
