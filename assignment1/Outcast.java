import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wNet;

    public Outcast(WordNet wordnet) // constructor takes a WordNet object
    {
        wNet = wordnet;
    }

    public String outcast(String[] nouns) // given an array of WordNet nouns, return an outcast
    {
        String res = "";
        int maxDist = -1;
        // Sum the semantic dist of each noun with all other nouns to find outcast
        for (int i = 0; i < nouns.length; i++) {
            int currDist = -1;
            for (int j = 0; j < nouns.length; j++)
                currDist += wNet.distance(nouns[i], nouns[j]);

            if (currDist > maxDist) {
                maxDist = currDist;
                res = nouns[i];
            }
        }
        return res;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        In testIn = new In(args[2]);
        String[] testNouns = testIn.readAllStrings();

        // Runtime tests
        int cnt = 0;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= 5000) {
            outcast.outcast(testNouns);
            cnt++;
        }
        System.out.println("Count: " + cnt);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            String soln = outcast.outcast(nouns);
            if (args[t].contains("outcast5.txt"))
                assert soln.equals("table");
            if (args[t].contains("outcast8.txt"))
                assert soln.equals("bed");
            if (args[t].contains("outcast11.txt"))
                assert soln.equals("potato");
            if (args[t].contains("outcast29.txt"))
                assert soln.equals("acorn");
            StdOut.println(args[t] + ": " + soln);
        }
    }
}