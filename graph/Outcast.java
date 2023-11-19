package graph;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wNet;

    public Outcast(WordNet wordnet) // constructor takes a WordNet object
    {
        wNet = wordnet;
    }

    public String outcast(String[] nouns) // given an array of WordNet nouns, return an outcast
    {
        String outcastV = "";
        int maxDist = -1;
        for (int i = 0; i < nouns.length; i++) {
            int sumDist = -1;
            for (int j = 0; j < nouns.length; j++)
                sumDist += wNet.distance(nouns[i], nouns[j]);

            if (sumDist > maxDist) {
                maxDist = sumDist;
                outcastV = nouns[i];
            }
        }
        return outcastV;
    }

    public static void main(String[] args) // see test client below
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            String soln = outcast.outcast(nouns);
            if (args[0].equals("outcast5.txt"))
                assert soln.equals("table");
            if (args[0].equals("outcast8.txt"))
                assert soln.equals("bed");
            if (args[0].equals("outcast11.txt"))
                assert soln.equals("potato");
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}