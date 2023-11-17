package graph;

import edu.princeton.cs.algs4.Bag;

public class Graph {
    private Bag<Integer>[] adj;

    Graph(int V) {
        adj = new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Integer>();
        }
    }

    void addEdge(int v, int w) {
        adj[v].add(w);
        adj[w].add(v);
    }

    public Iterable<Integer> adj(int v) {
        return adj[v];
    }
}
