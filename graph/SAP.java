package graph;

import edu.princeton.cs.algs4.Digraph;

public class SAP {
    private Digraph g;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        g = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        DiDepthFirstOrder diSort = new DiDepthFirstOrder(g);
        // Iterable<Integer> topoSorted = DiDepthFirstOrder
        // Topo sort DAG
        // Iterate through nodes in order
        // For each node in adj[] of curr node, check if there is a path to both desired
        // nodes
        // When this first occurs, ancestor has been found
        // Return distance to each node as length
        DiBreadthFirstPaths bfsPaths = new DiBreadthFirstPaths(g, v);
        return bfsPaths.length(w);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        return -1;
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