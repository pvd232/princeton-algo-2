import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    // Applies move-to-front encoding and reads to StdOut
    public static void encode() {
        int[] chars = new int[R];
        for (int i = 0; i < chars.length; i++)
            chars[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int i = 0, prev = chars[i], curr = chars[i + 1];
            while (prev != c) {
                curr = chars[i];
                chars[i++] = prev;
                prev = curr;
            }
            BinaryStdOut.write((char) Math.max(0, i - 1));
            chars[0] = c;
        }
        BinaryStdOut.flush();
    }

    // Applies move-to-front decoding and writes to StdOut
    public static void decode() {
        int[] chars = new int[R];
        for (int i = 0; i < chars.length; i++)
            chars[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int res = chars[c];
            BinaryStdOut.write((char) res);
            int prev = chars[0], curr = chars[1];
            for (int i = 0; i <= c; i++) {
                curr = chars[i];
                chars[i] = prev;
                prev = curr;
            }
            chars[0] = res;
        }
        BinaryStdOut.flush();
    }

    public static void main(String[] args) {
        if (args[0].equals("-"))
            encode();
        else if (args[0].equals("+"))
            decode();
    }
}