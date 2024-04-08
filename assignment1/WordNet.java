import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.HashMap;

public class WordNet {
    private final ArrayList<String> syns; // List of synsets
    private final HashMap<String, ArrayList<Integer>> nouns; // nouns with associated synset ids
    private final SAP sap;

    // Constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        nouns = new HashMap<>();
        syns = new ArrayList<>();

        int n = parseSyns(synsets);
        ArrayList<Bag<Integer>> hypers = parseHypers(n, hypernyms);
        Digraph g = mkDigraph(n, hypers);

        if (DigraphDC.hasCycle(g))
            throw new IllegalArgumentException();
        sap = new SAP(g);
    }

    // Returns the number of created synsets for Graph constructor
    private int parseSyns(String synsets) {
        In synIn = new In(synsets);

        int cnt = 0;
        while (synIn.hasNextLine()) {
            String[] parts = synIn.readLine().split(","), synsList = parts[1].split(" ");
            String synset = parts[1];
            syns.add(synset); // Add synset to vertex-indexed array

            for (String syn : synsList) {
                ArrayList<Integer> sList = nouns.get(syn); // List of synset ids associated with given noun
                if (sList != null) // If noun exists already, add synset id
                    sList.add(cnt);
                else {
                    ArrayList<Integer> newList = new ArrayList<>();
                    newList.add(cnt);
                    nouns.put(syn, newList);
                }
            }
            cnt++;
        }
        return cnt;
    }

    private ArrayList<Bag<Integer>> parseHypers(int n, String hypernyms) {
        // Synset-indexed array of corresponding hypernyms
        ArrayList<Bag<Integer>> hypers = new ArrayList<>();
        In hyperIn = new In(hypernyms);
        int cnt = 0;
        while (hyperIn.hasNextLine()) {
            String[] currH = hyperIn.readLine().split(","); // Read in hypernym
            Bag<Integer> currHyper = new Bag<Integer>();
            for (int i = 1; i < currH.length; i++) // index to 1 b/c first value of hypernym line is synset id
                currHyper.add(Integer.parseInt(currH[i]));
            hypers.add(currHyper);
            cnt++;
        }
        if (cnt != n)
            throw new IllegalArgumentException();
        return hypers;
    }

    private Digraph mkDigraph(int len, ArrayList<Bag<Integer>> hypers) {
        Digraph diG = new Digraph(len);
        for (int i = 0; i < hypers.size(); i++) {
            Bag<Integer> hyper = hypers.get(i);
            for (int synset : hyper)
                diG.addEdge(i, synset);
        }
        return diG;
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
        return syns.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet testNet = new WordNet(args[0], args[1]);
        if (args[0].contains("synsets.txt")) {
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
}