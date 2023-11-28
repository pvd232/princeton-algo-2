package ewg;

import edu.princeton.cs.algs4.IndexMinPQ;

public class DijkstraSP {
    private DirectedEdge[] edgeTo;
    private double[] distTo;
    private IndexMinPQ<Double> pq;

    public DijkstraSP(EdgeWeightedDigraph G, int s) {
        edgeTo = new DirectedEdge[G.V()];
        distTo = new double[G.V()];
        pq = new IndexMinPQ<Double>(G.V());
        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;
        pq.insert(s, 0.0);
        while (!pq.isEmpty()) { // If empty, every vert has been visited
            int v = pq.delMin(); // KEY INVARIANT -> v is always the current smallest dist from s
            for (DirectedEdge e : G.adj(v)) // Evaluate adj verts, which may or may not have already been eval
                relax(e);
        }
    }

    private void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) { // If vert hasnt been eval, or has a smaller dist than prev
            distTo[w] = distTo[v] + e.weight(); // Update dist
            edgeTo[w] = e; // Update path
            if (pq.contains(w)) // If vert already added (each only considered once) to pq via an adj vert to s
                pq.decreaseKey(w, distTo[w]); // Update its pq key, so its adj have right dist when it's evalauted
            else // If vert has not been eval
                pq.insert(w, distTo[w]); // Add to queue for future consideration
        }
    }
}