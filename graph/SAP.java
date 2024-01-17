package graph;

import java.util.ArrayList;
import java.util.HashMap;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;

public class SAP {
    private final Digraph g;
    private final int n;
    private final int[] distToV;
    private final int[] distToW;
    private final boolean[] markedV;
    private final boolean[] markedW;

    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException();
        g = new Digraph(G);
        n = g.V();
        distToV = new int[n];
        distToW = new int[n];
        markedV = new boolean[n];
        markedW = new boolean[n];
    }

    private void bfs(Iterable<Integer> s, Stack<Integer> changed) {
        Queue<Integer> q = new Queue<Integer>();

        for (int next : s) {
            q.enqueue(next);
            changed.push(next);
            markedV[next] = true;
        }
        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (int w : g.adj(v))
                if (!markedV[w]) {
                    q.enqueue(w);
                    changed.push(w);
                    markedV[w] = true;
                    distToV[w] = distToV[v] + 1;
                }
        }
    }

    // Finds the shortest ancestral path of vertices in v and w shortest path arrays
    private int pathTo(Iterable<Integer> v, Iterable<Integer> w, boolean length) {
        Stack<Integer> changedV = new Stack<>(), changedW = new Stack<>();
        bfs(v, changedV);
        int res = sp(w, length, changedW);

        // Cleanup
        while (!changedV.isEmpty()) {
            int curr = changedV.pop();
            markedV[curr] = false;
            distToV[curr] = 0;
        }
        while (!changedW.isEmpty()) {
            int curr = changedW.pop();
            markedW[curr] = false;
            distToW[curr] = 0;
        }
        return res;
    }

    private int sp(Iterable<Integer> w, boolean length, Stack<Integer> changed) {
        Queue<Integer> q = new Queue<>();
        for (int vert : w) {
            q.enqueue(vert);
            changed.push(vert);
            markedW[vert] = true;
        }
        int dist = -1, ancestor = -1;
        while (!q.isEmpty()) { // For each vertex, check for directed path to v and w
            int curr = q.dequeue();
            for (int adj : g.adj(curr)) {
                if (!markedW[adj]) {
                    q.enqueue(adj);
                    changed.push(adj);
                    markedW[adj] = true;
                    distToW[adj] = distToW[curr] + 1;
                }
            }
            if (markedV[curr]) { // If vertex is reachable from v and w it's an ancestor
                int tmpDist = distToV[curr] + distToW[curr];
                if (dist == -1 || tmpDist < dist) { // If dist unset, or curr dist < dist, update
                    dist = tmpDist;
                    ancestor = curr;
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

    // do unit testing of this class
    public static void main(String[] args) {

        // Unit testing data
        In testLength = new In("./tests/length.txt"), testAncestor = new In("./tests/ancestor.txt");
        In synIn = new In("./inputs/synsets.txt"), hyperIn = new In("./inputs/hypernyms.txt");

        HashMap<String, ArrayList<Integer>> nouns = new HashMap<>(); // List of nouns with associated synsets

        ArrayList<String> s = new ArrayList<>(); // List of synsets

        // Synset-indexed array of corresponding hypernyms
        ArrayList<Bag<Integer>> adj = new ArrayList<>();

        int lineNum = 0; // need fixed len for Graph constructor
        while (synIn.hasNextLine() && hyperIn.hasNextLine()) {
            String[] parts = synIn.readLine().split(","), synsList = parts[1].split(" ");
            String synset = parts[1];
            s.add(synset); // Add synset to vertex-indexed array

            for (String syn : synsList) {
                ArrayList<Integer> sList = nouns.get(syn); // List of synset ids associated with given noun
                if (sList != null) // If noun exists, add synset id
                    sList.add(lineNum);
                else {
                    ArrayList<Integer> newList = new ArrayList<>();
                    newList.add(lineNum);
                    nouns.put(syn, newList);
                }
            }
            lineNum++;

            String[] currH = hyperIn.readLine().split(","); // Read in hypernym
            Bag<Integer> hypers = new Bag<Integer>();
            for (int i = 1; i < currH.length; i++) // index to 1 b/c first value of hypernym line is synset id
                hypers.add(Integer.parseInt(currH[i]));
            adj.add(hypers);
        }
        Digraph g = new Digraph(lineNum);
        for (int i = 0; i < adj.size(); i++) {
            Bag<Integer> adjBag = adj.get(i);
            for (int vert : adjBag)
                g.addEdge(i, vert);
        }
        SAP sap = new SAP(g);

        // Unit test length()
        while (testLength.hasNextLine()) {
            String[] line = testLength.readLine().split(",");
            int length = Integer.parseInt(line[0]);
            ArrayList<Integer> v = nouns.get(line[1]), w = nouns.get(line[2]);
            int testLen = sap.length(v, w);
            assert length == testLen;
        }

        // Unit test ancestor()
        while (testAncestor.hasNextLine()) {
            String[] line = testAncestor.readLine().split(",");
            String ancestor = line[0];
            ArrayList<Integer> v = nouns.get(line[1]), w = nouns.get(line[2]);
            assert ancestor.equals(s.get(sap.ancestor(v, w)));
        }

        // Runtime tests
        long start = System.currentTimeMillis();
        int cnt = 0;
        while (System.currentTimeMillis() - start < 1000) {
            int v = StdRandom.uniform(0, g.V()), w = StdRandom.uniform(0, g.V());
            sap.length(v, w);
            sap.ancestor(v, w);
            cnt++;
        }
        System.out.println("Count: " + cnt);
    }
}
