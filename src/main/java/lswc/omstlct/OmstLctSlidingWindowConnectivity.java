package lswc.omstlct;

import lswc.AbstractSlidingWindowConnectivity;
import lswc.StreamingEdge;
import it.unimi.dsi.fastutil.ints.IntIntPair;

import java.time.Duration;
import java.util.*;

public class OmstLctSlidingWindowConnectivity extends AbstractSlidingWindowConnectivity {
    private final OmstLct lct;
    private final Queue<StreamingEdge> window;

    public OmstLctSlidingWindowConnectivity(Duration range, Duration slide, List<IntIntPair> workload, OmstLct lct) {
        super(range, slide, workload);
        this.lct = lct;
        this.window = new ArrayDeque<>();
    }

    @Override
    public void insert(StreamingEdge streamingEdge) {
        window.add(streamingEdge);

        int s = streamingEdge.source, t = streamingEdge.target;
        LctNode nodeS = lct.getNode(s), nodeT = lct.getNode(t);


        if (!lct.query(nodeS, nodeT)) {// the case of inserting a tree edge
            lct.insertTreeEdge(nodeS, nodeT, streamingEdge.timeStamp);
        } else {
            RepTreeEdge minimumEdge = lct.findMinimum(nodeS, nodeT);

            if (minimumEdge.weight < streamingEdge.timeStamp)
                lct.replace(minimumEdge, nodeS, nodeT, streamingEdge.timeStamp);
        }
    }

    @Override
    public void evict(long lessThan) {
        while (!window.isEmpty() && window.peek().timeStamp < lessThan) {
            StreamingEdge streamingEdge = window.poll();
            if (lct.isTreeEdge(streamingEdge.source, streamingEdge.target))
                lct.deleteTreeEdge(streamingEdge.source, streamingEdge.target);
        }
    }

    @Override
    public boolean query(int source, int target) {
        return lct.query(source, target);
    }

    @Override
    public void query(List<IntIntPair> queries, List<List<Boolean>> outputStreams) {
        for (int i = 0; i < queries.size(); i++) {
            IntIntPair intIntPair = queries.get(i);
            outputStreams.get(i).add(lct.query(intIntPair.firstInt(), intIntPair.secondInt()));
        }
    }

    @Override
    public long memoryConsumption() {
        return lct.memoryConsumption();
    }

    @Override
    public void manage(long timeStamp) {

    }
}
