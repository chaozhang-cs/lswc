package lswc.baselines.utils;

import java.util.HashSet;
import java.util.Set;

public class InternalVertex {
    private final int v;
    private final Set<InternalVertex> adjacencyList;

    public InternalVertex(int v) {
        this.v = v;
        adjacencyList = new HashSet<>();
    }

    public int getV() {
        return v;
    }

    public Set<InternalVertex> getAdjacencyList() {
        return adjacencyList;
    }

    public void addEdge(InternalVertex target) {
        adjacencyList.add(target);
    }


    public void removeEdge(InternalVertex target) {
        adjacencyList.remove(target);
    }

    public boolean isAdjacentTo(InternalVertex target) {
        return adjacencyList.contains(target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InternalVertex that = (InternalVertex) o;

        return v == that.v;
    }

    @Override
    public int hashCode() {
        return v;
    }

    @Override
    public String toString() {
        return "InternalVertex{" +
                "v=" + v +
                '}';
    }
}
