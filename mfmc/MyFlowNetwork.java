package mfmc;

import edu.princeton.cs.algs4.Bag;

public class MyFlowNetwork {
    private final int V;
    private int E;
    private final Bag<MyFlowEdge>[] adj;

    public MyFlowNetwork(int V) {
        this.V = V;
        adj = (Bag<MyFlowEdge>[]) new Bag[V];
        for (int v = 0; v < V; v++)
            adj[v] = new Bag<MyFlowEdge>();
    }

    public Iterable<MyFlowEdge> adj(int v) {
        return adj[v];
    }

    public void addEdge(MyFlowEdge e) {
        int v = e.from();
        int w = e.to();
        adj[v].add(e);
        adj[w].add(e);
        E += 2;
    }

    int V() {
        return V;
    }

    int E() {
        return E;
    }
}