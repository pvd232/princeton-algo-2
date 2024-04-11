package graph;

import java.util.ArrayList;
import java.util.HashMap;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

public class SAP {
    private final int n;
    private final int[][] adj; // Jagged 2D array
    private final int[] distToV;
    private final int[] distToW;
    private final boolean[] markedV;
    private final boolean[] markedW;
    private final int[] changed;
    private final int[] vQ;
    private final int[] wQ;
    private Iterable<Integer> prevV;
    private Iterable<Integer> prevW;
    private int prevPairV = -1;
    private int prevPairW = -1;
    private final int[] prevSAP;
    private final int[] prevPairSAP;

    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException();
        n = G.V();
        adj = new int[n][];
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
            for (int vert : G.adj(i))
                tmp.add(vert);
            int[] res = new int[tmp.size()];
            for (int j = 0; j < res.length; j++)
                res[j] = tmp.get(j);
            adj[i] = res;
        }

        distToV = new int[n];
        distToW = new int[n];
        markedV = new boolean[n];
        markedW = new boolean[n];
        changed = new int[2 * n];
        vQ = new int[n];
        wQ = new int[n];
        prevSAP = new int[2];
        prevPairSAP = new int[2];

    }

    // Finds the shortest ancestral path of vertices in v and w shortest path arrays
    private int pathTo(Iterable<Integer> v, Iterable<Integer> w, boolean length, int vPair, int wPair) {
        int[] resArray = sp(v, w, length, vPair, wPair);
        int res = resArray[0];
        int cnt = resArray[1];
        int lCnt = 0;
        // Cleanup
        while (lCnt < cnt) {
            int curr = changed[lCnt++];
            markedV[curr] = false;
            markedW[curr] = false;
            distToV[curr] = 0;
            distToW[curr] = 0;
            changed[lCnt - 1] = 0;
        }
        cnt = 0;
        return res;
    }

    private int[] sp(Iterable<Integer> v, Iterable<Integer> w,
            boolean length, int vPair, int wPair) {
        int vS = 0, wS = 0, vE = 0, wE = 0, cnt = 0;
        // Load up search queues
        for (int vert : v) {
            vQ[vE++] = vert;
            changed[cnt++] = vert;
            markedV[vert] = true;
        }
        for (int vert : w) {
            wQ[wE++] = vert;
            changed[cnt++] = vert;
            markedW[vert] = true;
        }

        int[] currQ; // Pointer to queue currently being explored
        int[] currDist; // Pointer to dist array currently being used
        boolean[] currMarked; // Pointer to marked array currently being used
        boolean isV = true;
        if (vE - vS > 0) {
            currQ = vQ;
            currDist = distToV;
            currMarked = markedV;
        } else {
            currQ = wQ;
            currDist = distToW;
            currMarked = markedW;
            isV = false;
        }

        int dist = -1, ancestor = -1;
        while (vE - vS > 0 || wE - wS > 0) { // While exploration queue not empty
            int curr;
            if (isV) // pointer to int is a value, not reference, must explicitly update it
                curr = currQ[vS++];
            else
                curr = currQ[wS++];
            int tmpDist = distToV[curr] + distToW[curr];
            if (markedV[curr] && markedW[curr] && (dist < 0 || tmpDist < dist)) { // ancestor && dist unset or less
                dist = tmpDist;
                ancestor = curr;
            }
            if ((dist < 0 || currDist[curr] < dist)) // Only explore adj verts if dist of vert from source < curr SP
                for (int vert : adj[curr]) {
                    if (!currMarked[vert]) {
                        currDist[vert] = currDist[curr] + 1;
                        currMarked[vert] = true;
                        if (isV)
                            currQ[vE++] = vert;
                        else
                            currQ[wE++] = vert;
                        changed[cnt++] = vert;
                    }
                }
            else if (isV) // must update explicitly
                vS = vE;
            else
                wS = wE;

            if (isV && wE - wS > 0) {
                isV = false;
                currQ = wQ;
                currDist = distToW;
                currMarked = markedW;
            } else if (!isV && vE - vS > 0) {
                isV = true;
                currQ = vQ;
                currDist = distToV;
                currMarked = markedV;
            }
        }

        if (ancestor > -1) {
            if (vPair == -1) {
                prevV = v;
                prevW = w;
                prevSAP[0] = dist;
                prevSAP[1] = ancestor;
            } else {
                prevPairV = vPair;
                prevPairW = wPair;
                prevPairSAP[0] = dist;
                prevPairSAP[1] = ancestor;
            }
        }

        if (length)
            return new int[] { dist, cnt };
        return new int[] { ancestor, cnt };
    }

    // Length of shortest ancestral path between v and w
    public int length(int v, int w) {
        if (v >= n || w >= n || v < 0 || w < 0)
            throw new IllegalArgumentException();

        if ((prevPairV == v && prevPairW == w) || (prevPairW == v && prevPairV == w))
            return prevPairSAP[0];

        ArrayList<Integer> vList = new ArrayList<>(1);
        vList.add(v);
        ArrayList<Integer> wList = new ArrayList<>(1);
        wList.add(w);
        return pathTo(vList, wList, true, v, w);
    }

    // Common ancestor of v and w that participates in a shortest ancestral path
    public int ancestor(int v, int w) {
        if (v >= n || w >= n || v < 0 || w < 0)
            throw new IllegalArgumentException();

        if ((prevPairV == v && prevPairW == w) || (prevPairW == v && prevPairV == w))
            return prevPairSAP[1];

        ArrayList<Integer> vList = new ArrayList<>(1);
        vList.add(v);
        ArrayList<Integer> wList = new ArrayList<>(1);
        wList.add(w);
        return pathTo(vList, wList, false, v, w);
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
        if (prevV == v && prevW == w || prevW == v && prevV == w)
            return prevSAP[0];
        return pathTo(v, w, true, -1, -1);
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
        if (prevV == v && prevW == w || prevW == v && prevV == w)
            return prevSAP[1];
        return pathTo(v, w, false, -1, -1);
    }

    // Do unit testing of this class
    public static void main(String[] args) {

        // Unit testing data
        In testLength = new In("./tests/length.txt"), testAncestor = new In("./tests/ancestor.txt");
        In synIn = new In("./inputs/synsets.txt"), hyperIn = new In("./inputs/hypernyms.txt");

        HashMap<String, ArrayList<Integer>> nouns = new HashMap<>(); // List of nouns with associated synsets

        ArrayList<String> s = new ArrayList<>(); // List of synsets

        // Synset-indexed array of corresponding hypernyms
        ArrayList<Bag<Integer>> synAdj = new ArrayList<>();

        int lineNum = 0; // Need fixed len for Graph constructor
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
            synAdj.add(hypers);
        }
        Digraph g = new Digraph(lineNum);
        for (int i = 0; i < synAdj.size(); i++) {
            Bag<Integer> adjBag = synAdj.get(i);
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
        int numReps = 0;
        while (System.currentTimeMillis() - start < 1000) {
            int v = StdRandom.uniform(0, g.V()), w = StdRandom.uniform(0, g.V());
            sap.length(v, w);
            sap.ancestor(v, w);
            numReps++;
        }
        System.out.println("Count: " + numReps);
    }
}
