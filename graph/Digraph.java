package graph;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;

public class Digraph {
    private int numV;
    private int numE;
    private Bag<Integer>[] adj;

    // Create an empty digraph with V vertices
    public Digraph(int V) {
        numV = V;

        adj = (Bag<Integer>[]) new Bag[V];
        for (int v = 0; v < V; v++)
            adj[v] = new Bag<Integer>();

    }

    // Create a digraph from input stream
    public Digraph(In in) {
        while (in.hasNextLine()) {

        }
    }

    // Add a directed edge v→w
    public void addEdge(int v, int w) {
        adj[v].add(w);
        numE++;
    }

    // Vertices pointing from v
    public Iterable<Integer> adj(int v) {
        return adj[v];
    }

    // Number of vertices
    public int vertices() {
        return numV;
    }

    // Number of edges
    public int edges() {
        return numE;
    }

    // Reverse of this digraph
    public Digraph reverse() {
        return this;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int v = 0; v < numV; v++) {
            for (int w : adj(v)) {
                output.append(v + "->" + w);
            }
        }
        return output.toString();
    }
}
