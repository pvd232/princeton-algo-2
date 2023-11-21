package graph;

import java.util.ArrayDeque;
import java.util.Queue;
import edu.princeton.cs.algs4.Digraph;

public class DiBFSPaths {
    private boolean[] marked;
    private int[] distTo; // distance from each vertex to given vertex

    public DiBFSPaths(Digraph g, int s) {
        distTo = new int[g.V()];
        marked = new boolean[g.V()];
        bfs(g, s);
    }

    public DiBFSPaths(Digraph g, Iterable<Integer> s) {
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
                    distTo[w] = distTo[v] + 1;
                }
            }
        }
    }

    private void bfs(Digraph G, Iterable<Integer> s) {
        Queue<Integer> q = new ArrayDeque<Integer>();
        for (Integer next : s) {
            q.add(next);
            marked[next] = true;
            distTo[next] = 0;
        }
        while (!q.isEmpty()) {
            int v = q.remove();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    q.add(w);
                    marked[w] = true;
                    distTo[w] = distTo[v] + 1;
                }
            }
        }
    }

    public int dist(int w) {
        if (!marked[w])
            return -1;
        else
            return distTo[w];
    }
}