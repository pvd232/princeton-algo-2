package graph;

import java.util.ArrayDeque;
import java.util.Queue;
import edu.princeton.cs.algs4.Digraph;

public class DiBreadthFirstPaths {
    private boolean[] marked;
    private int[] edgeTo; // list of directed edges on shortest path
    private int[] distTo; // distance from each vertex to given vertex

    public DiBreadthFirstPaths(Digraph g, int s) {
        edgeTo = new int[g.V()];
        distTo = new int[g.V()];
        marked = new boolean[g.V()];
        bfs(g, s);
    }

    private void bfs(Digraph G, int s) {
        Queue<Integer> q = new ArrayDeque<Integer>();
        q.add(s);
        marked[s] = true;
        while (!q.isEmpty()) {
            int v = q.remove();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    q.add(w);
                    marked[w] = true;
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                }
            }
        }
    }

    public int length(int w) {
        if (marked[w] == false)
            return -1;
        else
            return distTo[w];
    }
}