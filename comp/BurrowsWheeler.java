package comp;

import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static int r = 256;

    public static void transform() {
        StringBuilder res = new StringBuilder();
        int start = 0;
        while (!BinaryStdIn.isEmpty()) {
            String s = BinaryStdIn.readString();
            CircularSuffixArray cs = new CircularSuffixArray(s);
            for (int i = 0; i < cs.length(); i++) {
                int ogIdx = cs.index(i);
                if (ogIdx == 0)
                    start = i;
                res.append(end(ogIdx, s));
            }
        }
        BinaryStdOut.write(start);
        BinaryStdOut.write(res.toString());
        BinaryStdOut.flush();
    }

    // Get the last char for the given Circular suffix
    private static char end(int i, String s) {
        if (i == 0)
            return s.charAt(s.length() - 1);
        return s.charAt(i - 1);
    }

    private static ArrayList<ArrayList<Integer>> count(char[] msg) {
        ArrayList<ArrayList<Integer>> res = new ArrayList<>(r);
        for (int i = 0; i < r; i++)
            res.add(new ArrayList<>(msg.length / r));

        for (int i = 0; i < msg.length; i++)
            res.get(msg[i]).add(i);
        return res;
    }

    public static void inverseTransform() {
        StringBuilder res = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            int first = BinaryStdIn.readInt();
            char[] msg = BinaryStdIn.readString().toCharArray(), sorted = msg.clone();
            Arrays.sort(sorted);

            int[] next = new int[msg.length], sortedCount = new int[r];
            ArrayList<ArrayList<Integer>> msgCharCount = count(msg);

            for (int i = 0; i < next.length; i++)
                next[i] = msgCharCount.get(sorted[i]).get(sortedCount[sorted[i]]++);

            while (res.length() < msg.length) {
                res.append(msg[next[first]]);
                first = next[first];
            }
        }
        BinaryStdOut.write(res.toString());
        BinaryStdOut.flush();
    }

    public static void main(String[] args) {
        if (args[0].equals("-"))
            BurrowsWheeler.transform();
        else if (args[0].equals("+"))
            BurrowsWheeler.inverseTransform();
    }
}