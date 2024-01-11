package graph;

import java.util.ArrayList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph g;
    private final int n;

    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException();
        g = new Digraph(G);
        n = g.V();
    }

    private void bfs(int[] distTo, boolean[] marked, Iterable<Integer> s) {
        Queue<Integer> q = new Queue<Integer>();
        for (int next : s) {
            q.enqueue(next);
            marked[next] = true;
            distTo[next] = 0;
        }
        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (int w : g.adj(v)) {
                if (!marked[w]) {
                    q.enqueue(w);
                    marked[w] = true;
                    distTo[w] = distTo[v] + 1;
                }
            }
        }
    }

    // Finds the shortest ancestral path of vertices in v and w shortest path arrays
    private int pathTo(Iterable<Integer> v, Iterable<Integer> w, boolean length) {
        int[] distToV = new int[n], distToW = new int[n];
        boolean[] markedV = new boolean[n], markedW = new boolean[n];
        bfs(distToV, markedV, v);
        bfs(distToW, markedW, w);
        return sp(distToV, distToW, markedV, markedW, v, w, length);
    }

    private int sp(int[] distToV, int[] distToW, boolean[] markedV, boolean[] markedW,
            Iterable<Integer> v, Iterable<Integer> w, boolean length) {
        boolean[] marked = new boolean[n];
        Queue<Integer> q = new Queue<>();

        // Start search at v and w to avoid exploring unreachable vertices
        for (int vert : v) {
            q.enqueue(vert);
            marked[vert] = true;
        }
        for (int vert : w) {
            q.enqueue(vert);
            marked[vert] = true;
        }
        int dist = -1, ancestor = -1;
        while (!q.isEmpty()) { // For each vertex, check for directed path to v and w
            int curr = q.dequeue();
            if (markedV[curr] && markedW[curr]) { // If vertex is reachable from v and w it's an ancestor
                int tmpDist = distToV[curr] + distToW[curr];
                if (dist == -1 || tmpDist < dist) { // If dist unset, or curr dist < dist, update
                    dist = tmpDist;
                    ancestor = curr;
                }
            }
            for (int adj : g.adj(curr)) {
                if (dist == -1 || distToV[adj] + distToW[adj] < dist) { // Prune, only pursuing adj with shorter dist
                    if (!marked[adj]) {
                        q.enqueue(adj);
                        marked[adj] = true;
                    }
                }
            }
        }
        if (length)
            return dist;
        return ancestor;
    }

    // Length of shortest ancestral path between v and w
    public int length(int v, int w) {
        if (v >= n || w >= n || v < 0 || w < 0)
            throw new IllegalArgumentException();
        ArrayList<Integer> vList = new ArrayList<>(1);
        vList.add(v);
        ArrayList<Integer> wList = new ArrayList<>(1);
        wList.add(w);
        return pathTo(vList, wList, true);
    }

    // Common ancestor of v and w that participates in a shortest ancestral path
    public int ancestor(int v, int w) {
        if (v >= n || w >= n || v < 0 || w < 0)
            throw new IllegalArgumentException();
        ArrayList<Integer> vList = new ArrayList<>(1);
        vList.add(v);
        ArrayList<Integer> wList = new ArrayList<>(1);
        wList.add(w);
        return pathTo(vList, wList, false);
    }

    // Length of shortest ancestral path between any vertex in v and any vertex in w
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();
        for (Integer vert : v)
            if (vert == null || vert >= n || vert < 0)
                throw new IllegalArgumentException();
        for (Integer vert : w)
            if (vert == null || vert >= n || vert < 0)
                throw new IllegalArgumentException();

        return pathTo(v, w, true);
    }

    // Common ancestor that participates in shortest ancestral path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();
        for (Integer vert : v)
            if (vert == null || vert >= n || vert < 0)
                throw new IllegalArgumentException();
        for (Integer vert : w)
            if (vert == null || vert >= n || vert < 0)
                throw new IllegalArgumentException();

        return pathTo(v, w, false);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);

            if (v == 3 && w == 11) {
                assert length == 4;
                assert ancestor == 1;
            } else if (v == 9 && w == 12) {
                assert length == 3;
                assert ancestor == 5;
            } else if (v == 7 && w == 2) {
                assert length == 4;
                assert ancestor == 0;
            } else if (v == 1 && w == 6) {
                assert length == -1;
                assert ancestor == -1;
            }

            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}