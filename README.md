# Low-Latency Sliding Window Connectivity (LSWC)
This repository provides the implementation of the MST and the OMST frameworks, and also the implementation of MST D-Tree, OMST D-Tree, OMST S-Tree, and OMST LC-Tree, which are data structures to compute connectivity queries within sliding windows over streaming graphs.

####
- `mst` includes the implementation of the MST framework and the MST D-Tree data structures
- `omst` shows the implementation of the OMST framework and the OMST D-Tree and the OMST S-Tree data structures
- `omstlct` includes the implementation of the OMST LC-Tree data structure.

#### Getting Started
0. Make sure JDK 11 or higher is installed.
1. Clone the project.
2. Execute `mvn clean package`.

#### Reproducibility

1. Download the [datasets_and_workload.tar.gz](https://drive.google.com/file/d/1qZP08kjVP6j1hLJsRAzhbOiBav7f2wPk/view?usp=drive_link), including the datasets and workloads used in the experimental study.

2. Execute `mkdir benchmark` to create the `benchmark` directory, and move the tar file to the directory

2. Under `benchmark`, execute `tar -czvf rlc-benchmarks.tar.gz`. The directories `benchmark/datasets` and `benchmark/workloads` contain the datasets and workloads.

4. Execute `nohup java -Xmx750g -cp target/lswc-1.0-SNAPSHOT.jar lswc.benchmark.BenchmarkRunner &` to reproduce the results.

5. The benchmark results are available under the directory `benchmark/results`.


