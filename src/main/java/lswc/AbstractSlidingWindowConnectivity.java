package lswc;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.jgrapht.alg.util.Pair;

import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public abstract class AbstractSlidingWindowConnectivity {
    protected final long range, slide;
    protected final boolean isRangeMultipleOfSlide;

    protected List<IntIntPair> workload;

    @Deprecated
    public AbstractSlidingWindowConnectivity(Duration range, Duration slide) {
        this.range = range.toMillis();
        this.slide = slide.toMillis();
        this.isRangeMultipleOfSlide = (range.toMillis() % slide.toMillis() == 0);
    }

    public AbstractSlidingWindowConnectivity(Duration range, Duration slide, List<IntIntPair> workload) {
        this.range = range.toMillis();
        this.slide = slide.toMillis();
        this.isRangeMultipleOfSlide = (range.toMillis() % slide.toMillis() == 0);
        this.workload = workload;
    }

    // assuming timestamps of streaming edges are contiguous, i.e., the difference between every two adjacent time stamps is less than a slide interval
    // the content of the every window instance: [t_start, t_end)
    public void computeSlidingWindowConnectivity(Collection<StreamingEdge> inputStream, List<List<Boolean>> outputStreams) {
        if (inputStream.isEmpty())
            return;

        final int num = outputStreams.size();
        if (workload.size() != num)
            return;

        Iterator<StreamingEdge> streamingEdgeIterator = inputStream.iterator();
        StreamingEdge streamingEdge = streamingEdgeIterator.next();
        long startOfCurrentWindow = streamingEdge.timeStamp;
        insert(streamingEdge);


        while (streamingEdgeIterator.hasNext()) { // make the first window instance full
            streamingEdge = streamingEdgeIterator.next();
            if (streamingEdge.timeStamp - startOfCurrentWindow < range)
                insert(streamingEdge);
            else
                break;
        }

        query(workload, outputStreams);
        startOfCurrentWindow += slide;
        evict(startOfCurrentWindow); // first evict
        insert(streamingEdge);

        while (streamingEdgeIterator.hasNext()) {
            streamingEdge = streamingEdgeIterator.next();
            if (streamingEdge.timeStamp - startOfCurrentWindow >= range) { // compute query result
                query(workload, outputStreams);
                startOfCurrentWindow += slide;
                evict(startOfCurrentWindow);
                manage(streamingEdge.timeStamp);
            }
            insert(streamingEdge);
        }
    }

    // test only for latency experiments
    public void computeSlidingWindowConnectivity(Collection<StreamingEdge> inputStream, List<List<Boolean>> outputStreams, List<Pair<Long, Long>> latencyResults) {
        if (inputStream.isEmpty())
            return;

        final int num = outputStreams.size();
        if (workload.size() != num)
            return;

        Iterator<StreamingEdge> streamingEdgeIterator = inputStream.iterator();
        StreamingEdge streamingEdge = streamingEdgeIterator.next();

        long startOfCurrentWindow = streamingEdge.timeStamp;
        insert(streamingEdge);

        while (streamingEdgeIterator.hasNext()) { // make the first window instance full
            streamingEdge = streamingEdgeIterator.next();
            if (streamingEdge.timeStamp - startOfCurrentWindow < range)
                insert(streamingEdge);
            else
                break;
        }

        query(workload, outputStreams);
        startOfCurrentWindow += slide;
        evict(startOfCurrentWindow); // first evict
        insert(streamingEdge);

        long start, end, queryTime, manageTime;

        while (streamingEdgeIterator.hasNext()) {
            streamingEdge = streamingEdgeIterator.next();
            if (streamingEdge.timeStamp - startOfCurrentWindow >= range) { // compute query result

                start = System.nanoTime();
                query(workload, outputStreams);
                end = System.nanoTime();
                queryTime = end - start; // query latency

                startOfCurrentWindow += slide;

                start = System.nanoTime();
                evict(startOfCurrentWindow);
                manage(streamingEdge.timeStamp);
                end = System.nanoTime();
                manageTime = end - start; // window manage latency

                latencyResults.add(Pair.of(queryTime, manageTime));
            }

            insert(streamingEdge);
        }
    }

    public void computeQueriesAndGetMemoryConsumption(Collection<StreamingEdge> inputStream, List<List<Boolean>> outputStreams, List<Long> memoryConsumptionPerWindow) {
        if (inputStream.isEmpty())
            return;

        final int num = outputStreams.size();
        if (workload.size() != num)
            return;

        Iterator<StreamingEdge> streamingEdgeIterator = inputStream.iterator();
        StreamingEdge streamingEdge = streamingEdgeIterator.next();
        long startOfCurrentWindow = streamingEdge.timeStamp;
        insert(streamingEdge);

        while (streamingEdgeIterator.hasNext()) { // make the first window instance full
            streamingEdge = streamingEdgeIterator.next();
            if (streamingEdge.timeStamp - startOfCurrentWindow < range)
                insert(streamingEdge);
            else
                break;
        }

        query(workload, outputStreams);
        startOfCurrentWindow += slide;
        evict(startOfCurrentWindow); // first evict
        insert(streamingEdge);


        while (streamingEdgeIterator.hasNext()) {
            streamingEdge = streamingEdgeIterator.next();
            if (streamingEdge.timeStamp - startOfCurrentWindow >= range) { // compute query result
                query(workload, outputStreams);
                startOfCurrentWindow += slide;

                memoryConsumptionPerWindow.add(memoryConsumption()); // capturing the memory used

                evict(startOfCurrentWindow);
                manage(streamingEdge.timeStamp);
            }
            insert(streamingEdge);
        }
    }

    public abstract void insert(StreamingEdge streamingEdge);

    // evict all the streaming edges, whose timestamp are less than the lessThan time
    public abstract void evict(long lessThan);

    public abstract boolean query(int source, int target);

    public abstract void query(List<IntIntPair> queries, List<List<Boolean>> outputStreams);

    public abstract long memoryConsumption();

    public abstract void manage(long timeStamp);
}
