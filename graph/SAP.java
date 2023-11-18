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

    // Arch Note: merged ancestor/dist logic in pathTo b/c min dist -> ancestor
    private int pathTo(int v, int w, boolean getAncestor) {
        // Topo sort DAG
        DiDepthFirstOrder diSort = new DiDepthFirstOrder(g);

        // Get iterable
        Iterator<Integer> topoSortedIt = diSort.reversePost().iterator();

        int dist = -1, ancestor = -1;
        // For each vertex, check for path to v and w
        DiBFSPaths bfsSearchV = new DiBFSPaths(g, v), bfsSearchW = new DiBFSPaths(g, w);
        while (topoSortedIt.hasNext()) { // Iterate through nodes in order
            int curr = topoSortedIt.next();

            int vDist = bfsSearchV.dist(curr), wDist = bfsSearchW.dist(curr);
            if (vDist != -1 && wDist != -1) { // If each vertex -> curr, it's an ancestor
                if (dist == -1 || vDist + wDist < dist) {
                    dist = vDist + wDist;
                    ancestor = curr;
                }
            }
        }
        if (getAncestor)
            return ancestor;
        else
            return dist;
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