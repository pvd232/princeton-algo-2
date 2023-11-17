package graph;

import java.util.Iterator;

import edu.princeton.cs.algs4.Digraph;

public class SAP {
    private Digraph g;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        g = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return pathTo(v, w, false);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        return pathTo(v, w, true);
    }

    private int pathTo(int v, int w, boolean ancestor) {
        // Topo sort DAG
        DiDepthFirstOrder diSort = new DiDepthFirstOrder(g);
        Iterable<Integer> topoSorted = diSort.reversePost();
        Iterator<Integer> it = topoSorted.iterator();
        int res = -1;

        DiBFSPaths bfsSearchV = new DiBFSPaths(g, v); // For each vertex, check for path to v and w
        DiBFSPaths bfsSearchW = new DiBFSPaths(g, w); // For each vertex, check for path to v and w
        while (it.hasNext()) { // Iterate through nodes in order
            int curr = it.next();
            if (bfsSearchV.dist(curr) != -1 && bfsSearchW.dist(curr) != -1) // If each vertex -> curr, it's an ancestor
                if (ancestor)
                    res = curr;
                else
                    res = bfsSearchV.dist(curr) + bfsSearchW.dist(curr);
        }
        return res;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in
    // w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return -1;
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}