package graph;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.HashMap;

public class WordNet {
    private final ArrayList<String> s; // List of synsets
    private final HashMap<String, ArrayList<Integer>> nouns; // nouns with associated synset ids
    private final Digraph g;
    private final SAP sap;

    // Constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        s = new ArrayList<>();
        In synIn = new In(synsets), hyperIn = new In(hypernyms);

        // Synset-indexed array of corresponding hypernyms
        ArrayList<Bag<Integer>> adj = new ArrayList<>();

        nouns = new HashMap<>();

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
        // Populate digraph
        g = new Digraph(lineNum);
        for (int i = 0; i < adj.size(); i++) {
            Bag<Integer> adjBag = adj.get(i);
            for (int vert : adjBag)
                g.addEdge(i, vert);
        }
        if (DigraphDC.hasCycle(g))
            throw new IllegalArgumentException();
        sap = new SAP(g);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException();
        return nouns.containsKey(word);
    }

    // Distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // Synset that is the common ancestor of nounA and nounB
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();
        return s.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet testNet = new WordNet(args[0], args[1]);
        assert testNet.g.V() == testNet.s.size();
        assert testNet.isNoun("miracle");
        assert !testNet.isNoun("gooblegah");

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