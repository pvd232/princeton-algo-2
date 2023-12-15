package comp;

import java.util.Arrays;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static int r = 126;

    // Apply Burrows-Wheeler transform,
    // Reading from standard input and writing to standard output
    public static void transform() {
        StringBuilder res = new StringBuilder();
        int start = 0;
        while (!BinaryStdIn.isEmpty()) {
            String s = BinaryStdIn.readString();
            CircularSuffixArray cs = new CircularSuffixArray(s);
            for (int i = 0; i < s.length(); i++) {
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
        int z = i - 1;
        if (z == -1)
            return s.charAt(s.length() + z);
        return s.charAt(z);
    }

    // Apply Burrows-Wheeler inverse transform,
    // Reading from standard input and writing to standard output
    public static void inverseTransform() {
        while (!BinaryStdIn.isEmpty()) {
            int first = BinaryStdIn.readInt();
            char[] msg = BinaryStdIn.readString().toCharArray(), sorted = msg.clone();
            Arrays.sort(sorted);

            int[] next = new int[msg.length], msgCount = new int[r], sortedCount = new int[r], lastSeen = new int[r];
            for (int i = 0; i < next.length; i++) {
                char c = sorted[i];
                sortedCount[c]++; // Increment the char count for the input string
                while (sortedCount[c] != msgCount[c]) { // While the char count of sorted != message char count
                    int j = lastSeen[c];
                    while (j < msg.length && msg[j] != c) // Increment to find matching char in msg
                        j++;

                    msgCount[c]++; // Increment msg char count after we find a matching char
                    lastSeen[c] = j++; // Update last seen index and increment j past current match
                }
                next[i] = lastSeen[c]++; // Increment last seen after the match is found for j
                msgCount[c] = 0;
            }
            int n = first;
            StringBuilder res = new StringBuilder();
            while (res.length() < msg.length) {
                res.append(msg[next[n]]);
                n = next[n];
            }
            BinaryStdOut.write(res.toString());
            BinaryStdOut.write('\n');
            BinaryStdOut.flush();
        }
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-"))
            BurrowsWheeler.transform();
        else if (args[0].equals("+"))
            BurrowsWheeler.inverseTransform();
    }
}