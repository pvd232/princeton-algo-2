package comp;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    public static void transform() {
        while (!BinaryStdIn.isEmpty()) {
            int start = 0;
            String s = BinaryStdIn.readString();
            char[] res = new char[s.length()];
            CircularSuffixArray cs = new CircularSuffixArray(s);

            for (int i = 0; i < cs.length(); i++) {
                int ogIdx = cs.index(i);
                res[i] = end(ogIdx, s);
                if (ogIdx == 0)
                    start = i;
            }
            BinaryStdOut.write(start);
            for (char c : res)
                BinaryStdOut.write(c);
        }
        BinaryStdOut.flush();
    }

    // Get the last char for the given Circular suffix
    private static char end(int i, String s) {
        if (i == 0)
            return s.charAt(s.length() - 1);
        return s.charAt(i - 1);
    }

    // Create key-indexed, index for each char in msg
    private static int[][] charCount(char[] msg) {
        int[] currCount = new int[R];
        int[][] res = new int[R][];
        for (int i = 0; i < msg.length; i++) // Get fixed length char count
            currCount[msg[i]]++;
        for (int i = 0; i < res.length; i++) // Create int[] for each char
            res[i] = new int[currCount[i]];
        for (int i = 0; i < msg.length; i++)
            // Decrement currCount[msg[i]]to track the char being assigned the index
            res[msg[i]][res[msg[i]].length - currCount[msg[i]]--] = i;
        return res;
    }

    public static void inverseTransform() {
        while (!BinaryStdIn.isEmpty()) {
            int first = BinaryStdIn.readInt();
            char[] msg = BinaryStdIn.readString().toCharArray(), sorted = msg.clone();

            KeyIndexCount.sort(sorted); // More efficient to sort chars using key-index sorting
            int[] next = new int[msg.length], sortedCount = new int[R];
            int[][] charCount = charCount(msg);

            for (int i = 0; i < next.length; i++) // Match sorted suffix char idx with charCount idx, increment count
                next[i] = charCount[sorted[i]][sortedCount[sorted[i]]++];

            int cnt = 0;
            // Guard against periodic / unary msg w/cnt
            while (cnt < next.length) {
                cnt++;
                BinaryStdOut.write(sorted[first]);
                first = next[first];
            }
        }
        BinaryStdOut.flush();
    }

    public static void main(String[] args) {
        if (args[0].equals("-"))
            BurrowsWheeler.transform();
        else if (args[0].equals("+"))
            BurrowsWheeler.inverseTransform();
    }
}