package comp;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // Applies move-to-front encoding and reads to StdOut
    public static void encode() {
        int[] chars = new int[256];
        for (int i = 0; i < chars.length; i++)
            chars[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int idx = 0, prev = chars[idx], curr = chars[idx + 1];
            if (prev != c)
                for (int i = 1; i < chars.length; i++) {
                    curr = chars[i];
                    chars[i] = prev;
                    prev = curr;
                    if (prev == c) {
                        idx = i;
                        break;
                    }
                }
            BinaryStdOut.write((char) idx);
            chars[0] = c;
        }
        BinaryStdOut.flush();
    }

    // Applies move-to-front decoding and writes to StdOut
    public static void decode() {
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-"))
            encode();
        else if (args[0].equals("+"))
            decode();
    }
}