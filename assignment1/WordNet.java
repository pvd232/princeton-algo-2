import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;

public class WordNet {
    private final ArrayList<Synset> verts;
    private final Digraph g;
    private final SAP sap;
    private final HashMap<String, LinkedList<Integer>> map;

    // Constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        verts = new ArrayList<>();
        In synIn = new In(synsets), hyperIn = new In(hypernyms);

        // Create adjacent verts array to store adjacent sets while processing input
        ArrayList<Bag<Integer>> adj = new ArrayList<>();
        map = new HashMap<>();

        int graphLen = 0;
        while (synIn.hasNextLine() && hyperIn.hasNextLine()) {
            graphLen++; // need fixed len for Graph constructor

            Synset newSyn = new Synset(synIn.readLine());
            verts.add(newSyn); // Create synset from curr line

            Iterator<String> synIt = newSyn.syns.iterator();
            while (synIt.hasNext()) {
                String next = synIt.next();
                if (map.containsKey(next))
                    map.get(next).add(newSyn.id);
                else {
                    LinkedList<Integer> newList = new LinkedList<>();
                    newList.add(newSyn.id);
                    map.put(next, newList);
                }
            }

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
        private final HashSet<String> syns;
        private final String synTxt;
        private final int id;

        private Synset(String csvLine) {
            String[] parts = csvLine.split(","), synsList = parts[1].split(" ");
            id = Integer.parseInt(parts[0]);
            synTxt = parts[1];

            syns = new HashSet<>();
            for (int i = 0; i < synsList.length; i++)
                syns.add(synsList[i]);
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        ArrayList<String> res = new ArrayList<>();
        for (String key : map.keySet()) {
            res.add(key);
        }
        return res;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException();
        return map.containsKey(word);
    }

    // Binary search to find id for given noun
    private HashSet<Integer> subset(String word) {
        HashSet<Integer> res = new HashSet<>();
        Iterator<Integer> ids = map.get(word).iterator();
        while (ids.hasNext())
            res.add(ids.next());
        return res;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        HashSet<Integer> a = subset(nounA), b = subset(nounB);
        if (a.size() == 1 && b.size() == 1)
            return sap.length(a.iterator().next(), b.iterator().next());
        else
            return sap.length(a, b);
    }

    // a synset that is the common ancestor of nounA and nounB
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        HashSet<Integer> a = subset(nounA), b = subset(nounB);
        if (a.size() == 1 && b.size() == 1)
            return verts.get(sap.ancestor(a.iterator().next(), b.iterator().next())).synTxt;
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
        assert testAncestor.contains("natural_childbirth");

        String testNounC = "miracle", testNounD = "increase";
        int testDist2 = testNet.distance(testNounC, testNounD);

        assert testDist2 != -1;
        assert testDist2 == 3;

        String testAncestorB = testNet.sap(testNounC, testNounD);
        assert testAncestorB.contains("happening");
    }
}