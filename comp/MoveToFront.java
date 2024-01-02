package comp;

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
            int i = 0, prev = chars[i]; // Initialize prev to chars[0] before curr assigned
            while (prev != c) {
                int curr = chars[i]; // Point curr to chars[i]
                chars[i++] = prev; // Shift right by setting chars[i] = chars[i-1]
                prev = curr; // Point prev to curr
            }
            BinaryStdOut.write((char) Math.max(0, i - 1));
            chars[0] = c; // Move c to front
        }
        BinaryStdOut.flush();
    }

    // Applies move-to-front decoding and writes to StdOut
    public static void decode() {
        int[] chars = new int[R];
        for (int i = 0; i < chars.length; i++) // Create radix-indexed array
            chars[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int res = chars[c]; // Get position of char in radix-indexed array
            BinaryStdOut.write((char) res); // Must cast int to char
            int prev = chars[0]; // Initialize prev to char[0] before curr assigned
            for (int i = 0; i <= c; i++) { // Shift each char until c is reached
                int curr = chars[i]; // Point curr to chars[i]
                chars[i] = prev; // Shift right by setting chars[i] = chars[i-1]
                prev = curr; // Set prev = curr
            }
            chars[0] = res; // Mimic encoding process by setting first element to char
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