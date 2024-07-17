package lswc.benchmark;

import lswc.AbstractSlidingWindowConnectivity;
import lswc.StreamingEdge;
import lswc.baselines.FdcSlidingWindowConnectivity;
import lswc.baselines.dtree.DTreeConnectivity;
import lswc.baselines.naive.DfsConnectivity;
import lswc.baselines.naive.RecalculatingWindowConnectivity;
import lswc.baselines.bic.BidirectionalIncrementalConnectivity;
import lswc.omstlct.OmstLctSlidingWindowConnectivity;
import lswc.omstlct.OmstLctImpl;
import lswc.mst.MstDTreeImpl;
import lswc.mst.MstSlidingWindowConnectivity;
import lswc.omst.OptimizedMstDTreeImpl;
import lswc.omst.OptimizedMstSTreeImpl;
import lswc.omst.OptimizedMstSlidingWindowConnectivity;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.jgrapht.alg.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BenchmarkRunner {
    static final String BENCHMARK_RESULTS = "./benchmark/results/";
    static final String BENCHMARK_DATASETS = "/home/c223zhan/swc/benchmark/datasets/";
    static final String BENCHMARK_WORKLOADS = "/home/c223zhan/swc/benchmark/workloads/";

    static final Map<String, Integer> GRAPH_VERTEX_NUM = Map.of(
            "sg-wiki-topcats", 1791489,
            "sg-com-lj.ungraph", 3997962,
            "sg-youtube-u-growth", 3223589,
            "sg-soc-pokec-relationships", 1632803,
            "sg-stackoverflow", 2601977,
            "sg-orkut", 3072441,
            "sg-ldbc-sf1k-knows", 3298534,
            "sg-graph500-25", 17062472,
            "sg-com-friendster.ungraph", 65608366,
            "sg-semantic-scholar", 65695514
    );

    public static void main(String[] args) {
//         performance evaluation
        throughputRunner();
        latencyRunner();

//         varied workload sizes
        scalabilityWorkloadThrExpRunner();
        scalabilityWorkloadLatencyExpRunner();

//         fixed slide interval, varied window sizes
        scalabilityFixedSlideThrExpRunner();
        scalabilityFixedSlideLatencyExpRunner();

//         fixed window size, varied slide intervals
        scalabilityFixedRangeThrExpRunner();
        scalabilityFixedRangeLatencyExpRunner();

//         memory consumption
        memoryConsumptionRunner();

        scalabilityFixedSlideMemRunner();
        scalabilityFixedRangeMemRunner();
    }

    private static void throughputRunner() {
        List<String> results = new ArrayList<>();
        int repeat = 3;
        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };
        setupThrExp(
                methods,
                "per-eva",
                "sg-youtube-u-growth",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-wiki-topcats",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-soc-pokec-relationships",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-com-lj.ungraph",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-stackoverflow",
                List.of(Pair.of(Duration.ofDays(180), Duration.ofDays(9))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-orkut",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-ldbc-sf1k-knows",
                List.of(Pair.of(Duration.ofDays(20), Duration.ofDays(1))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-graph500-25",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-com-friendster.ungraph",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        setupThrExp(
                methods,
                "per-eva",
                "sg-semantic-scholar",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                repeat,
                results
        );
        writeResult(results, BENCHMARK_RESULTS + "throughput-per-eva-" + LocalDateTime.now() + ".txt");
    }

    private static void latencyRunner() {
        String expType = "per-eva";
        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };
        setupLatencyExp(
                methods,
                expType,
                "sg-youtube-u-growth",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-wiki-topcats",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-soc-pokec-relationships",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-com-lj.ungraph",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-stackoverflow",
                List.of(Pair.of(Duration.ofDays(180), Duration.ofDays(9)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-orkut",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-ldbc-sf1k-knows",
                List.of(Pair.of(Duration.ofDays(20), Duration.ofDays(1)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30)))
        );
    }

    private static void scalabilityFixedSlideThrExpRunner() {
        List<String> results = new ArrayList<>();
        int repeat = 3;
        int numOfQueries = 100_000;
        String expType = "fixed-slide";
        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };

        setupThrExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                repeat,
                results,
                numOfQueries
        );

        setupThrExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                repeat,
                results,
                numOfQueries
        );

        setupThrExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                repeat,
                results,
                numOfQueries
        );

        writeResult(results, BENCHMARK_RESULTS + "throughput-" + expType + "-" + LocalDateTime.now() + ".txt");
    }

    private static void scalabilityFixedSlideLatencyExpRunner() {
        String expType = "fixed-slide";
        int numOfQueries = 100_000;
        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };

        setupLatencyExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                numOfQueries
        );

        setupLatencyExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                numOfQueries
        );

        setupLatencyExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                numOfQueries
        );
    }

    private static void scalabilityFixedRangeThrExpRunner() {
        List<String> results = new ArrayList<>();
        int repeat = 3;
        int numOfQueries = 100_000;
        String expType = "fixed-range";
        String[] methods = {
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree",
                "D-Tree",
                "RWC"
        };

        setupThrExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 1)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                repeat,
                results,
                numOfQueries
        );

        setupThrExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 1)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                repeat,
                results,
                numOfQueries
        );

        setupThrExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 1)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                repeat,
                results,
                numOfQueries
        );
        writeResult(results, BENCHMARK_RESULTS + "throughput-" + expType + "-" + LocalDateTime.now() + ".txt");
    }

    private static void scalabilityFixedRangeLatencyExpRunner() {
        String expType = "fixed-range";
        int numOfQueries = 100_000;

        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };
        setupLatencyExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 1)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                numOfQueries
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 1)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                numOfQueries
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 1)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                numOfQueries
        );
    }

    private static void scalabilityWorkloadThrExpRunner() {
        List<String> results = new ArrayList<>();
        String expType = "workload";
        int repeat = 3;
        int[] sizes = new int[]{1, 100, 10_000, 1_000_000};
        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };
        setupThrExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                repeat,
                results,
                getWorkLoads("sg-graph500-25", sizes)
        );
        setupThrExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                repeat,
                results,
                getWorkLoads("sg-com-friendster.ungraph", sizes)
        );
        setupThrExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                repeat,
                results,
                getWorkLoads("sg-semantic-scholar", sizes)
        );
        setupThrExp(
                new String[]{"DFS"},
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                repeat,
                results,
                getWorkLoads("sg-graph500-25", new int[]{1, 10})
        );
        setupThrExp(
                new String[]{"DFS"},
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                repeat,
                results,
                getWorkLoads("sg-com-friendster.ungraph", new int[]{1, 10})
        );
        setupThrExp(
                new String[]{"DFS"},
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                repeat,
                results,
                getWorkLoads("sg-semantic-scholar", new int[]{1, 10})
        );
        writeResult(results, BENCHMARK_RESULTS + "throughput-" + expType + "-" + LocalDateTime.now() + ".txt");
    }

    private static void scalabilityWorkloadLatencyExpRunner() {
        String expType = "workload";
        int[] sizes = new int[]{1, 100, 10_000, 1_000_000};

        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };

        setupLatencyExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                getWorkLoads("sg-graph500-25", sizes)
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                getWorkLoads("sg-com-friendster.ungraph", sizes)
        );
        setupLatencyExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                getWorkLoads("sg-semantic-scholar", sizes)
        );

        setupLatencyExp(
                new String[]{"DFS"},
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                getWorkLoads("sg-graph500-25", new int[]{1, 10})
        );
        setupLatencyExp(
                new String[]{"DFS"},
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                getWorkLoads("sg-com-friendster.ungraph", new int[]{1, 10})
        );
        setupLatencyExp(
                new String[]{"DFS"},
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3))
                ),
                getWorkLoads("sg-com-friendster.ungraph", new int[]{1, 10})
        );
    }

    private static void memoryConsumptionRunner() {
        int numOfQueries = 100_000;
        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };

        setupMemExp(
                methods,
                "per-eva",
                "sg-wiki-topcats",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );
        setupMemExp(
                methods,
                "per-eva",
                "sg-youtube-u-growth",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );
        setupMemExp(
                methods,
                "per-eva",
                "sg-soc-pokec-relationships",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );
        setupMemExp(
                methods,
                "per-eva",
                "sg-com-lj.ungraph",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );
        setupMemExp(
                methods,
                "per-eva",
                "sg-stackoverflow",
                List.of(Pair.of(Duration.ofDays(180), Duration.ofDays(9))),
                numOfQueries
        );

        setupMemExp(
                methods,
                "per-eva",
                "sg-orkut",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );

        setupMemExp(
                methods,
                "per-eva",
                "sg-ldbc-sf1k-knows",
                List.of(Pair.of(Duration.ofDays(20), Duration.ofDays(1))),
                numOfQueries
        );

        setupMemExp(
                methods,
                "per-eva",
                "sg-graph500-25",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );
        setupMemExp(
                methods,
                "per-eva",
                "sg-com-friendster.ungraph",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );
        setupMemExp(
                methods,
                "per-eva",
                "sg-semantic-scholar",
                List.of(Pair.of(Duration.ofHours(10), Duration.ofMinutes(30))),
                numOfQueries
        );
    }

    private static void scalabilityFixedSlideMemRunner() {
        String expType = "fixed-slide";
        int numOfQueries = 100_000;

        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };

        setupMemExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                numOfQueries

        );

        setupMemExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                numOfQueries
        );

        setupMemExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 10), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 20), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 40), Duration.ofHours(3)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3))
                ),
                numOfQueries
        );
    }

    private static void scalabilityFixedRangeMemRunner() {
        String expType = "fixed-range";
        int numOfQueries = 100_000;
        String[] methods = {
                "D-Tree",
                "RWC",
                "BIC",
                "MST-DTree",
                "OMST-DTree",
                "OMST-STree",
                "OMST-LCTree"
        };
        setupMemExp(
                methods,
                expType,
                "sg-graph500-25",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                numOfQueries
        );
        setupMemExp(
                methods,
                expType,
                "sg-com-friendster.ungraph",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                numOfQueries
        );
        setupMemExp(
                methods,
                expType,
                "sg-semantic-scholar",
                List.of(
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 2)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 4)),
                        Pair.of(Duration.ofHours(3 * 80), Duration.ofHours(3 * 8))
                ),
                numOfQueries
        );
    }

    private static void setupMemExp( // various sizes of workloads
                                     String[] methods,
                                     String expType,
                                     String graph,
                                     List<Pair<Duration, Duration>> rangeAndSlides,
                                     int numOfQueries) {

        // get graph
        List<StreamingEdge> streamingEdges = GraphUtils.readStreamingGraph(BENCHMARK_DATASETS + graph + ".txt", ",");

        if (rangeAndSlides == null)
            return;

        System.out.println("Range and slide" + rangeAndSlides);
        List<IntIntPair> workloads = getWorkLoad(graph, numOfQueries);
        for (String method : methods)
            runMemExp(
                    graph,
                    method,
                    expType,
                    rangeAndSlides,
                    workloads,
                    streamingEdges
            );
    }

    private static void runMemExp(
            String graph,
            String method,
            String expType,
            List<Pair<Duration, Duration>> rangeSlides,
            List<IntIntPair> workload,
            List<StreamingEdge> streamingEdges) {
        System.out.println("Start " + expType + " memory experiments for " + method + " on " + graph + " with ranges and slides of " + rangeSlides);

        for (Pair<Duration, Duration> rangeSlide : rangeSlides) {
            Duration range = rangeSlide.getFirst();
            Duration slide = rangeSlide.getSecond();
            List<Long> memoryResults = new ArrayList<>();
            AbstractSlidingWindowConnectivity slidingWindowConnectivity = getSwc(method, range, slide, workload, streamingEdges.get(0).timeStamp);
            slidingWindowConnectivity.computeQueriesAndGetMemoryConsumption(
                    streamingEdges,
                    initializeOutput(workload.size()),
                    memoryResults
            );
            writePerWindowResult(
                    memoryResults,
                    BENCHMARK_RESULTS + "memory-" + expType + "-" + rangeSlide + "-" + graph + "-" + method + "-" + "workload" + workload.size() + ".txt"
            );
            System.out.println("memory-" + expType + "-" + rangeSlide + "-" + graph + "-" + method + "-" + "workload" + workload.size());
            System.gc();
        }
    }

    private static void setupThrExp(
            String[] methods,
            String expType,
            String graph,
            List<Pair<Duration, Duration>> rangeAndSlides,
            int repeat,
            List<String> results) {
        setupThrExp(
                methods,
                expType,
                graph,
                rangeAndSlides,
                repeat,
                results,
                List.of(getWorkLoad(graph, 100_000))
        );
    }

    private static void setupThrExp(
            String[] methods,
            String expType,
            String graph,
            List<Pair<Duration, Duration>> rangeAndSlides,
            int repeat,
            List<String> results,
            int numOfQueries) {
        setupThrExp(
                methods,
                expType,
                graph,
                rangeAndSlides,
                repeat,
                results,
                List.of(getWorkLoad(graph, numOfQueries))
        );
    }

    private static void setupThrExp( // various sizes of workloads
                                     String[] methods,
                                     String expType,
                                     String graph,
                                     List<Pair<Duration, Duration>> rangeAndSlides,
                                     int repeat,
                                     List<String> results,
                                     List<List<IntIntPair>> workloads) {

        // get graph
        List<StreamingEdge> streamingEdges = GraphUtils.readStreamingGraph(BENCHMARK_DATASETS + graph + ".txt", ",");

        for (List<IntIntPair> workload : workloads) {
            System.out.println("Workload size: " + workload.size());

            if (rangeAndSlides == null)
                return;

            System.out.println("Range and slide" + rangeAndSlides);

            for (String method : methods)
                runThrExp(
                        graph,
                        method,
                        expType,
                        rangeAndSlides,
                        repeat,
                        workload,
                        streamingEdges,
                        results
                );
        }
    }

    private static void setupLatencyExp(
            String[] methods,
            String expType,
            String graph,
            List<Pair<Duration, Duration>> rangeAndSlides) {
        setupLatencyExp(
                methods,
                expType,
                graph,
                rangeAndSlides,
                List.of(getWorkLoad(graph, 100_000))
        );
    }

    private static void setupLatencyExp(
            String[] methods,
            String expType,
            String graph,
            List<Pair<Duration, Duration>> rangeAndSlides,
            int numOfQueries) {
        setupLatencyExp(
                methods,
                expType,
                graph,
                rangeAndSlides,
                List.of(getWorkLoad(graph, numOfQueries))
        );
    }

    private static void setupLatencyExp(
            String[] methods,
            String expType,
            String graph,
            List<Pair<Duration, Duration>> rangeAndSlides,
            List<List<IntIntPair>> workloads) { // various sizes of workloads
        // get graph
        List<StreamingEdge> streamingEdges = GraphUtils.readStreamingGraph(BENCHMARK_DATASETS + graph + ".txt", ",");

        for (List<IntIntPair> workload : workloads) {
            System.out.println("Workload size: " + workload.size());

            if (rangeAndSlides == null)
                return;

            System.out.println("Range and slide: " + rangeAndSlides);

            for (String method : methods)
                runLatExp(
                        expType,
                        graph,
                        method,
                        rangeAndSlides,
                        workload,
                        streamingEdges
                );
        }
    }

    private static void runThrExp(
            String graph,
            String method,
            String expType,
            List<Pair<Duration, Duration>> rangeSlides,
            int repeat,
            List<IntIntPair> workload,
            List<StreamingEdge> streamingEdges,
            List<String> results) {
        System.out.println("Start " + expType + " throughput experiments for " + method + " on " + graph + " with ranges and slides of " + rangeSlides);
        for (Pair<Duration, Duration> rangeSlide : rangeSlides) {
            Duration range = rangeSlide.getFirst();
            Duration slide = rangeSlide.getSecond();
            for (int i = 0; i < repeat; i++) {
                AbstractSlidingWindowConnectivity slidingWindowConnectivity = getSwc(method, range, slide, workload, streamingEdges.get(0).timeStamp);
                long start = System.nanoTime();
                slidingWindowConnectivity.computeSlidingWindowConnectivity(
                        streamingEdges,
                        initializeOutput(workload.size())
                );
                long end = System.nanoTime();
                String result = graph + "," + expType + "," + method + "," + range.toMillis() + "," + slide.toMillis() + "," + streamingEdges.size() + "," + (end - start) + "," + workload.size();
                System.out.println(result);
                results.add(result);
                System.gc();
            }
        }
    }

    private static void runLatExp(
            String exp,
            String graph,
            String method,
            List<Pair<Duration, Duration>> rangeAndSlides,
            List<IntIntPair> workload,
            List<StreamingEdge> streamingEdges) {

        System.out.println("Warmup");
        getSwc(method, rangeAndSlides.get(0).getFirst(), rangeAndSlides.get(0).getSecond(), workload, streamingEdges.get(0).timeStamp).computeSlidingWindowConnectivity(
                streamingEdges,
                initializeOutput(workload.size())
        );// warm up

        System.out.println("Start latency " + exp + " experiments for " + method + " on " + graph + " with ranges and slides: " + rangeAndSlides);
        for (Pair<Duration, Duration> pair : rangeAndSlides) {
            Duration range = pair.getFirst(), slide = pair.getSecond();
            AbstractSlidingWindowConnectivity slidingWindowConnectivity = getSwc(method, range, slide, workload, streamingEdges.get(0).timeStamp);
            List<Long> result = new ArrayList<>();
            slidingWindowConnectivity.computeSlidingWindowConnectivity(
                    streamingEdges,
                    initializeOutput(workload.size()),
                    result);

            writePerWindowResult(result, BENCHMARK_RESULTS + "latency-" + exp + "-" + pair + "-" + graph + "-" + method + "-" + "workload" + workload.size() + "-" + LocalDateTime.now() + ".txt");
            System.gc();
        }
    }

    private static AbstractSlidingWindowConnectivity getSwc(String method, Duration range, Duration slide, List<IntIntPair> workload, long first) {
        AbstractSlidingWindowConnectivity ret;
        switch (method) {
            case "DFS":
                ret = new FdcSlidingWindowConnectivity(range, slide, workload, new DfsConnectivity());
                break;
            case "RWC":
                ret = new RecalculatingWindowConnectivity(range, slide, workload);
                break;
            case "D-Tree":
                ret = new FdcSlidingWindowConnectivity(range, slide, workload, new DTreeConnectivity());
                break;
            case "BIC":
                ret = new BidirectionalIncrementalConnectivity(range, slide, first, workload); // BIC uses first timestamp to initialize chunk
                break;
            case "MST-DTree":
                ret = new MstSlidingWindowConnectivity(range, slide, workload, new MstDTreeImpl());
                break;
            case "OMST-DTree":
                ret = new OptimizedMstSlidingWindowConnectivity(range, slide, workload, new OptimizedMstDTreeImpl());
                break;
            case "OMST-STree":
                ret = new OptimizedMstSlidingWindowConnectivity(range, slide, workload, new OptimizedMstSTreeImpl());
                break;
            case "OMST-LCTree":
                ret = new OmstLctSlidingWindowConnectivity(range, slide, workload, new OmstLctImpl());
                break;
            default:
                ret = null;
        }
        return ret;
    }

    private static void writeResult(List<String> results, String path) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(path);
            fileWriter.append("graph,experiment,method,range,slide,size,time,workload").append("\n").flush();
            for (String record : results)
                fileWriter.append(record).append("\n").flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writePerWindowResult(List<Long> result, String path) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(path);
            for (Long record : result)
                fileWriter.append(record.toString()).append("\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<IntIntPair> getFullWorkLoad(String graph) {
        return WorkloadUtils.readWorkload(BENCHMARK_WORKLOADS + graph + ".json");
    }

    private static List<IntIntPair> getWorkLoad(String graph, int limit) {
        List<IntIntPair> temp = getFullWorkLoad(graph);
        List<IntIntPair> workload = new ArrayList<>();
        Random random = new Random(1700276688);
        for (int i = 0; i < limit; i++)
            workload.add(temp.get(random.nextInt(temp.size())));
        return workload;
    }

    private static List<List<IntIntPair>> getWorkLoads(String graph, int[] sizes) {
        List<List<IntIntPair>> ret = new ArrayList<>();
        for (int size : sizes)
            ret.add(getWorkLoad(graph, size));
        return ret;
    }

    static List<List<Boolean>> initializeOutput(int num) {
        List<List<Boolean>> ret = new ArrayList<>();
        for (int i = 0; i < num; i++)
            ret.add(new ArrayList<>());
        return ret;
    }
}
