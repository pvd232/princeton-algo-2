package ewg;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.MinPQ;

public class LazyPrimMST {
    private boolean[] marked;
    private Queue<Edge> mst;
    private MinPQ<Edge> pq;

    // MST vertices
    // MST edges
    // PQ of edges
    public LazyPrimMST(EdgeWeightedGraph G) {
        pq = new MinPQ<Edge>();
        mst = new Queue<Edge>();
        marked = new boolean[G.V()];
        visit(G, 0);
        while (!pq.isEmpty() && mst.size() < G.V() - 1) { // MST has V-1 edges, 1 per vertex
            Edge e = pq.delMin();
            int v = e.either(), w = e.other(v);
            if (!marked[v] || !marked[w]) { // If the edge is in the tree, ignore it
                mst.enqueue(e);
                if (!marked[v])
                    visit(G, v);
                if (!marked[w])
                    visit(G, v);
            }
        }
    }

    private void visit(EdgeWeightedGraph G, int v) {
        marked[v] = true; // Add v to the tree
        for (Edge e : G.adj(v)) // Check adj verts (edges) of v
            if (!marked[e.other(v)]) // If the edge lies outside the tree
                pq.insert(e);
    }

    public Iterable<Edge> mst() {
        return mst;
    }
}