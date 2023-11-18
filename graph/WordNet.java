package graph;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class WordNet {
    private ArrayList<Synset> verts;
    private Digraph g;
    private SAP sap;

    // Constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        verts = new ArrayList<>();
        In synIn = new In(synsets), hyperIn = new In(hypernyms);

        // Create adjacent verts array to store adjacent sets while processing input
        ArrayList<Bag<Integer>> adj = new ArrayList<>();

        int graphLen = 0;
        while (synIn.hasNextLine() && hyperIn.hasNextLine()) {
            graphLen++; // need fixed len for Graph constructor
            verts.add(new Synset(synIn.readLine())); // Create synset from curr line

            String[] rawAdj = hyperIn.readLine().split(","); // Read in adjacent verts
            Bag<Integer> intAdj = new Bag<Integer>();
            for (int i = 1; i < rawAdj.length; i++) // index on one b/c first value of hypernym line is id of vertex
                intAdj.add(Integer.parseInt(rawAdj[i]));

            adj.add(intAdj);
        }
        // Populate digraph
        g = new Digraph(graphLen);
        for (int i = 0; i < adj.size(); i++) {
            Bag<Integer> adjBag = adj.get(i);
            Iterator<Integer> iBag = adjBag.iterator();
            while (iBag.hasNext())
                g.addEdge(i, iBag.next());
        }

        // Check for cycle
        DirectedCycle dC = new DirectedCycle(g);
        if (dC.hasCycle())
            throw new IllegalArgumentException();

        sap = new SAP(g);
    }

    private class Synset {
        private HashSet<String> syns;
        private String first;
        private String synTxt;

        private Synset(String csvLine) {
            String[] parts = csvLine.split(","), synsList = parts[1].split(" ");
            synTxt = parts[1];
            first = synsList[0];

            syns = new HashSet<>();
            for (int i = 0; i < synsList.length; i++)
                syns.add(synsList[i]);
        }

        public boolean contains(String noun) {
            return syns.contains(noun);
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < verts.size(); i++) {
            String[] nouns = verts.get(i).syns.toArray(new String[verts.get(i).syns.size()]);
            for (int j = 0; j < nouns.length; j++)
                res.add(nouns[j]);
        }
        return res;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException();
        return (getId(word).size() > 0);
    }

    // Binary search to find id for given noun
    private ArrayList<Integer> getId(String word) {
        ArrayList<Integer> ids = new ArrayList<>();
        int lo = 0, hi = verts.size(), mid = lo + (hi - lo) / 2;
        while (hi >= lo) {
            Synset s = verts.get(mid);
            if (s.contains(word)) {
                ids.add(mid);

                int i = mid + 1, j = mid - 1;
                while (i < verts.size() && verts.get(i).contains(word))
                    ids.add(i++);
                while (j > 0 && verts.get(j).contains(word))
                    ids.add(j++);

                return ids;
            } else if (s.first.compareTo(word) > 0)
                hi = mid - 1;
            else
                lo = mid + 1;
            mid = lo + (hi - lo) / 2;
        }
        return ids;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        ArrayList<Integer> a = getId(nounA), b = getId(nounB);
        if (a.size() == 0 || b.size() == 0)
            throw new IllegalArgumentException();
        else if (a.size() == 1 && b.size() == 1)
            return sap.length(a.get(0), b.get(0));
        else
            return sap.length(a, b);
    }

    // a synset that is the common ancestor of nounA and nounB
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        ArrayList<Integer> a = getId(nounA), b = getId(nounB);
        if (a.size() == 0 || b.size() == 0)
            throw new IllegalArgumentException();
        else if (a.size() == 1 && b.size() == 1)
            return verts.get(sap.ancestor(a.get(0), b.get(0))).synTxt;
        else
            return verts.get(sap.ancestor(a, b)).synTxt;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet testNet = new WordNet(args[0], args[1]);
        assert testNet.g.V() == testNet.verts.size();
        assert testNet.isNoun("miracle");
        assert !testNet.isNoun("gooblegah");

        // Brute force test nouns
        In syn = new In(args[0]);
        int nounCount = 0;
        while (syn.hasNextLine()) {
            String[] parts = syn.readLine().split(",");
            String[] synsList = parts[1].split(" ");
            nounCount += synsList.length;
        }
        ArrayList<String> nounsList = (ArrayList<String>) testNet.nouns();
        assert nounCount == nounsList.size();

        String testNounA = "Lamaze_method_of_childbirth", testNounB = "Bradley_method_of_childbirth";
        // Distance
        int testDist = testNet.distance(testNounA, testNounB);
        assert testDist == 2;

        String testAncestor = testNet.sap(testNounA, testNounB);
        assert testAncestor.equals("natural_childbirth");

        String testNounC = "miracle", testNounD = "increase";
        int testDist2 = testNet.distance(testNounC, testNounD);

        assert testDist2 != -1;
        assert testDist2 == 3;
    }
}