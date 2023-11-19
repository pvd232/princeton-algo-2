import java.util.Iterator;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private Digraph g;
    private DiDepthFirstOrder topoSort;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException();
        DirectedCycle dC = new DirectedCycle(G);
        if (dC.hasCycle())
            throw new IllegalArgumentException();
        g = G;
        topoSort = new DiDepthFirstOrder(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v >= g.V() || w >= g.V())
            throw new IllegalArgumentException();
        DiBFSPaths bfsV = new DiBFSPaths(g, v), bfsW = new DiBFSPaths(g, w);
        return pathTo(bfsV, bfsW, false);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        if (v >= g.V() || w >= g.V())
            throw new IllegalArgumentException();
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
        Iterator<Integer> vIt = v.iterator(), wIt = w.iterator();
        while (vIt.hasNext()) {
            Integer vNext = vIt.next();
            if (vNext >= g.V() || vNext <= 0 || vNext == null)
                throw new IllegalArgumentException();
        }
        while (wIt.hasNext()) {
            Integer wNext = wIt.next();
            if (wNext >= g.V() || wNext <= 0 || wNext == null)
                throw new IllegalArgumentException();
        }

        DiBFSPaths bfsV = new DiBFSPaths(g, v), bfsW = new DiBFSPaths(g, w);
        return pathTo(bfsV, bfsW, false);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        Iterator<Integer> vIt = v.iterator(), wIt = w.iterator();
        while (vIt.hasNext()) {
            Integer vNext = vIt.next();
            if (vNext >= g.V() || vNext <= 0 || vNext == null)
                throw new IllegalArgumentException();
        }
        while (wIt.hasNext()) {
            Integer wNext = wIt.next();
            if (wNext >= g.V() || wNext <= 0 || wNext == null)
                throw new IllegalArgumentException();
        }

        DiBFSPaths bfsV = new DiBFSPaths(g, v), bfsW = new DiBFSPaths(g, w);
        return pathTo(bfsV, bfsW, true);
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