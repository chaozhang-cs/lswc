package lswc.baselines.naive;

import lswc.AbstractSlidingWindowConnectivity;
import lswc.StreamingEdge;
import lswc.baselines.utils.UnionFindTree;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.openjdk.jol.info.GraphLayout;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class RecalculatingWindowConnectivity extends AbstractSlidingWindowConnectivity {
    private final Queue<StreamingEdge> window;
    private UnionFindTree ufts;

    public RecalculatingWindowConnectivity(Duration range, Duration slide) {
        super(range, slide);
        this.window = new ArrayDeque<>();
    }

    public RecalculatingWindowConnectivity(Duration range, Duration slide, List<IntIntPair> workload) {
        super(range, slide, workload);
        this.window = new ArrayDeque<>();
    }

    @Override
    public void insert(StreamingEdge streamingEdge) {
        window.add(streamingEdge);
    }

    @Override
    public void evict(long lessThan) {
        while (!window.isEmpty() && window.peek().timeStamp < lessThan)
            window.poll();
    }

    @Override
    public boolean query(int source, int target) {
        buildUfts();
        return this.ufts.connected(source, target);
    }

    @Override
    public void query(List<IntIntPair> queries, List<List<Boolean>> outputStreams) {
        buildUfts();
        for (int i = 0, num = queries.size(); i < num; i++) {
            IntIntPair intIntPair = queries.get(i);
            outputStreams.get(i).add(this.ufts.connected(intIntPair.firstInt(), intIntPair.secondInt()));
        }
    }

    private void buildUfts(){
        UnionFindTree incrementalConnectivity = new UnionFindTree();
        for (StreamingEdge e : window)
            incrementalConnectivity.union(e.source, e.target);
        this.ufts = incrementalConnectivity;
    }

    @Override
    public long memoryConsumption() {
        return GraphLayout.parseInstance(ufts).totalSize();
    }

    @Override
    public void manage(long timeStamp) {

    }
}
