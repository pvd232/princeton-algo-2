package graph;

import java.util.Iterator;

import edu.princeton.cs.algs4.Digraph;

public class SAP {
    private Digraph g;
    DiDepthFirstOrder topoSort;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        g = G;
        topoSort = new DiDepthFirstOrder(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        DiBFSPaths bfsV = new DiBFSPaths(g, v), bfsW = new DiBFSPaths(g, w);
        return pathTo(bfsV, bfsW, false);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        DiBFSPaths bfsV = new DiBFSPaths(g, v), bfsW = new DiBFSPaths(g, w);
        return pathTo(bfsV, bfsW, true);
    }

    // Arch Note: merged ancestor/dist logic in pathTo b/c min dist -> ancestor
    private int pathTo(DiBFSPaths bfsV, DiBFSPaths bfsW, boolean getAncestor) {
        // Get iterable
        Iterator<Integer> topoSortedIt = topoSort.reversePost().iterator();

        int dist = -1, ancestor = -1;
        // For each vertex, check for path to v and w
        while (topoSortedIt.hasNext()) { // Iterate through nodes in order
            int curr = topoSortedIt.next();

            int vDist = bfsV.dist(curr), wDist = bfsW.dist(curr);
            if (vDist != -1 && wDist != -1) { // If each vertex -> curr, it's an ancestor
                if (dist == -1 || vDist + wDist < dist) { // If dist unset, or curr dist < dist, update
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
        DiBFSPaths bfsV = new DiBFSPaths(g, v), bfsW = new DiBFSPaths(g, w);
        return pathTo(bfsV, bfsW, false);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        DiBFSPaths bfsV = new DiBFSPaths(g, v), bfsW = new DiBFSPaths(g, w);
        return pathTo(bfsV, bfsW, true);
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}