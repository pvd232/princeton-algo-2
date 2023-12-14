package comp;

import java.util.Arrays;

import edu.princeton.cs.algs4.In;

public class CircularSuffixArray {
    private String s;
    private CircularSuffix[] t;

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final int start;

        private CircularSuffix(int start) {
            this.start = start;
        }

        // Slow but clean impl -> might swap with implicit comparison later
        // Also might need to implement more efficient suffix sorting -> TBD
        public int compareTo(CircularSuffix that) {
            return compareTo(that, 0);
        }

        private int compareTo(CircularSuffix that, int i) {
            if (i == s.length())
                return 0;
            if (s.charAt(start + i) > s.charAt(that.start + i))
                return 1;
            else if (s.charAt(start + i) < s.charAt(that.start + i))
                return -1;
            return compareTo(that, i + 1);
        }
    }

    // Circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException();

        t = new CircularSuffix[s.length()];
        this.s = s;

        int i = 0;
        while (i < s.length())
            t[i] = new CircularSuffix(i++);
        Arrays.sort(t);
    }

    // length of s
    public int length() {
        return s.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= s.length())
            throw new IllegalArgumentException();
        return t[i].start;
    }

    // unit testing (required)
    public static void main(String[] args) {
        In in = new In(args[0]);
        CircularSuffixArray csa = new CircularSuffixArray(in.readAll());
        assert csa.index(0) == 11;
        assert csa.index(11) == 2;
        assert csa.index(1) == 10;
        assert csa.index(2) == 7;
    }
}