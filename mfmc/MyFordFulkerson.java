package mfmc;

import java.util.ArrayList;

import edu.princeton.cs.algs4.Queue;

public class MyFordFulkerson {
    private boolean[] marked; // True if s→v path in residual network
    private MyFlowEdge[] edgeTo; // Last edge on s→v path
    private double value; // Value of flow

    public MyFordFulkerson(MyFlowNetwork G, int s, int t) {
        value = 0.0;
        while (hasAugmentingPath(G, s, t)) {
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) // Compute bottleneck capacity

                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            for (int v = t; v != s; v = edgeTo[v].other(v)) // Augment flow

                edgeTo[v].addResidualFlowTo(v, bottle);
            value += bottle;
        }
    }

    // Augment flow
    private boolean hasAugmentingPath(MyFlowNetwork G, int s, int t) {
        edgeTo = new MyFlowEdge[G.V()];
        marked = new boolean[G.V()];
        Queue<Integer> q = new Queue<>();
        q.enqueue(s);
        marked[s] = true;
        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (MyFlowEdge e : G.adj(v)) {
                int w = e.other(v);
                if (e.residualCapacityTo(w) > 0 && !marked[w]) { // If there is a path from s→w in the residual network
                    edgeTo[w] = e;
                    marked[w] = true;
                    q.enqueue(w);
                }
            }
        }
        return marked[t];
    }

    public ArrayList<MyFlowEdge> minCut(MyFlowNetwork G, int s, int t) {
        ArrayList<MyFlowEdge> res = new ArrayList<>();
        for (int i = 0; i < G.V(); i++) {
            if (inCut(i))
                res.add(edgeTo[i]);
        }
        return res;
    }

    public double value() {
        return value;
    }

    public boolean inCut(int v) {
        return marked[v];
    }
}