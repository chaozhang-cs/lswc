package lswc.omst;

import lswc.AbstractSlidingWindowConnectivity;
import lswc.StreamingEdge;

import it.unimi.dsi.fastutil.ints.IntIntPair;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;


// Optimized implementation based on dynamic trees (DTree)
public class OptimizedMstSlidingWindowConnectivity extends AbstractSlidingWindowConnectivity {
    private final OptimizedMaximumSpanningTree mst;
    private final Queue<StreamingEdge> window;

    public OptimizedMstSlidingWindowConnectivity(Duration range, Duration slide, List<IntIntPair> workload, OptimizedMaximumSpanningTree mst) {
        super(range, slide, workload);
        this.mst = mst;
        this.window = new ArrayDeque<>();
    }

    @Override
    public void insert(StreamingEdge streamingEdge) {
        window.add(streamingEdge);

        // process the edge insertion
        int s = streamingEdge.source, t = streamingEdge.target;
        OptimizedNode nodeS = mst.getNode(s), nodeT = mst.getNode(t);

        if (mst.findRoot(nodeS) != mst.findRoot(nodeT)) // the case of inserting a tree edge
            mst.insertTreeEdge(nodeS, nodeT, streamingEdge.timeStamp);
        else { // the case of inserting a non-tree edge
            OptimizedTreeEdge minimumEdge = mst.findMinimum(nodeS, nodeT);

            if (minimumEdge.weight < streamingEdge.timeStamp) // the weight of the new edge is larger than the weight of the minimum one in the mst
                mst.replace(minimumEdge, nodeS, nodeT, streamingEdge.timeStamp);
            else
                mst.reshape(nodeS, nodeT, streamingEdge.timeStamp);
        }
    }


    @Override
    public void evict(long lessThan) {
        while (!window.isEmpty() && window.peek().timeStamp < lessThan) {
            StreamingEdge streamingEdge = window.poll();
            if (mst.isTreeEdge(streamingEdge.source, streamingEdge.target))
                mst.deleteTreeEdge(streamingEdge.source, streamingEdge.target);
        }
    }

    @Override
    public boolean query(int source, int target) {
        return mst.query(source, target);
    }

    @Override
    public void query(List<IntIntPair> queries, List<List<Boolean>> outputStreams) {
        for (int i = 0; i < queries.size(); i++) {
            IntIntPair intIntPair = queries.get(i);
            outputStreams.get(i).add(mst.query(intIntPair.firstInt(), intIntPair.secondInt()));
        }
    }

    @Override
    public long memoryConsumption() {
        return mst.memoryConsumption();
    }
}
