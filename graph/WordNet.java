package graph;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class WordNet {
    private ArrayList<Synset> vertices;
    private Digraph g;
    private SAP sap;

    // Constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        In synIn = new In(synsets); // List of synset vertices
        In hyperIn = new In(hypernyms); // List of adjacent synset vertices

        vertices = new ArrayList<>();
        int graphLen = 0;

        // Create adjacent vertices array to store adjacent sets while processing input
        ArrayList<Bag<Integer>> adj = new ArrayList<>();

        while (synIn.hasNextLine() && hyperIn.hasNextLine()) {
            graphLen++;

            vertices.add(new Synset(synIn.readLine())); // Create synset from curr line
            Bag<Integer> vAdj = new Bag<Integer>(); // Create bag for adjacent vertices

            String hyperLine = hyperIn.readLine(); // Read in adjacent vertices
            String[] rawAdj = hyperLine.split(",");

            Bag<Integer> intAdj = new Bag<Integer>();
            for (int i = 0; i < rawAdj.length; i++)
                intAdj.add(Integer.parseInt(rawAdj[i]));

            adj.add(vAdj);
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

        private Synset(String csvLine) {
            String[] parts = csvLine.split(",");
            String[] synsList = parts[1].split(" ");
            first = synsList[0];
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
        for (int i = 0; i < vertices.size(); i++) {
            String[] nouns = vertices.get(i).syns.toArray(new String[vertices.get(i).syns.size()]);
            for (int j = 0; j < nouns.length; j++)
                res.add(nouns[j]);
        }
        return res;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException();
        boolean hasWord = (getId(word) != -1);
        return hasWord;
    }

    // Binary search to find id for given noun
    private int getId(String word) {
        int lo = 0, hi = vertices.size();
        int mid = ((hi - lo) + lo) / 2;
        while (hi >= lo) {
            Synset s = vertices.get(mid);
            if (s.contains(word))
                return mid;
            else if (s.first.compareTo(word) > 0) {
                hi = mid;
                mid = ((hi - lo) + lo) / 2;
            } else {
                lo = mid;
                mid = ((hi - lo) + lo) / 2;
            }
        }
        return -1;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        int a = getId(nounA);
        int b = getId(nounB);
        if (a == -1 || b == -1)
            throw new IllegalArgumentException();

        int dist = sap.length(a, b);
        return dist;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA
    // and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();
        return "";
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}