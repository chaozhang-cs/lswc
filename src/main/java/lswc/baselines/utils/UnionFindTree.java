package lswc.baselines.utils;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * This implementation is based on the weighted quick union find with path compression.
 * Please see <a href="https://algs4.cs.princeton.edu/15uf">Section 1.5</a> of Algorithms, 4th Edition by Robert Sedgewick and Kevin Wayne for additional details.
 */
public class UnionFindTree {


    private final Int2IntOpenHashMap parent; // map a vertex to its parent, and if a vertex is a root, then the vertex is mapped to itself
    private final Int2IntOpenHashMap size; // if a vertex is a root, then the vertex is mapped to size of the tree
    private int count; // number of connected components

    /**
     * Initialize an empty incremental connectivity with a set of {@code vertices}.
     * Initially, each vertex is in its own component.
     *
     * @param vertices a set of vertices
     */
    public UnionFindTree(IntSet vertices) {
        count = vertices.size();
        parent = new Int2IntOpenHashMap();
        size = new Int2IntOpenHashMap();
        for (int v : vertices) {
            parent.put(v, v);
            size.put(v, 1);
        }
    }

    /**
     * Initialize an empty incremental connectivity without a set of vertices.
     */
    public UnionFindTree() {
        count = 0;
        parent = new Int2IntOpenHashMap();
        size = new Int2IntOpenHashMap();
    }

    private void addVertex(int v) {
        parent.putIfAbsent(v, v);
        size.putIfAbsent(v, 1);
        count++;
    }

    private void insertVerticesIfAbsent(int source, int target) {
        if (!parent.containsKey(source))
            addVertex(source);
        if (!parent.containsKey(target))
            addVertex(target);
    }

    /**
     * Returns true if source and target are in the same component.
     * If source or target was not included in the data structure, they will not be inserted during executing this method.
     *
     * @param source a vertex
     * @param target a vertex
     * @return {@code true} if {@code source} and {@code target} are in the same component;
     * {@code false} otherwise.
     */
    public boolean connected(int source, int target) {
        if (!parent.containsKey(source) || !parent.containsKey(target))
            return false;
        return find(source) == find(target);
    }

    /**
     * Links the source vertex to the target vertex.
     * Returns true if the data structure changes after linking, false otherwise.
     * If source or target was not included in the data structure, they will be first inserted before linking.
     *
     * @param source a source vertex
     * @param target a target vertex
     * @return true if the data structure changes after linking, false otherwise.
     */
    public boolean union(int source, int target) { // aka the union method
        insertVerticesIfAbsent(source, target);
        int rootOfSource = find(source);
        int rootOfTarget = find(target);
        if (rootOfSource == rootOfTarget) return false;

        int updatedSize = size.get(rootOfTarget) + size.get(rootOfSource);

        if (size.get(rootOfSource) < size.get(rootOfTarget)) {
            parent.put(rootOfSource, rootOfTarget);
            size.put(rootOfTarget, updatedSize);
        } else {
            parent.put(rootOfTarget, rootOfSource);
            size.put(rootOfSource, updatedSize);
        }

        count--; // a new connected component has been found
        return true;
    }

    private int find(final int v) {
        // compute the root of v
        int tempRoot = parent.get(v);
        while (tempRoot != parent.get(tempRoot))
            tempRoot = parent.get(tempRoot);

        // optimization
        int u = v;
        final int root = tempRoot;
        while (u != root) {
            int temp = parent.get(u);
            parent.put(u, root);
            u = temp;
        }
        return root;
    }

    /**
     * Returns the number of components
     *
     * @return the number of components.
     */
    public int getCount() {
        return count;
    }
}
