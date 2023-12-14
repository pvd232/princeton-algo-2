package comp;

import edu.princeton.cs.algs4.In;

public class CircularSuffixArray {
    private final String s;
    private CircularSuffix[] t;

    private class CircularSuffix {
        private final int start;

        private CircularSuffix(int start) {
            this.start = start;
        }

        // // Slow but clean impl -> might swap with implicit comparison later
        // // Also might need to implement more efficient suffix sorting -> TBD
        // public int compareTo(CircularSuffix that) {
        // return compareTo(that, 0);
        // }

        // private int compareTo(CircularSuffix that, int i) {
        // if (i == s.length())
        // return 0;
        // if (s.charAt(start + i) > s.charAt(that.start + i))
        // return 1;
        // else if (s.charAt(start + i) < s.charAt(that.start + i))
        // return -1;
        // return compareTo(that, i + 1);
        // }
    }

    private static class TWSQS {
        private static void sort(CircularSuffix[] a, String s) {
            sort(a, 0, a.length - 1, 0, s);
        }

        private static void sort(CircularSuffix[] a, int lo, int hi, int d, String s) {
            if (hi <= lo)
                return;
            int lt = lo, gt = hi;
            int v = charAt(a[lo], d, s);
            int i = lo + 1;
            while (i <= gt) {
                // 3-way partitioning (using dth character) to handle variable-length strings
                int t = charAt(a[i], d, s);
                if (t < v)
                    exch(a, lt++, i++);
                else if (t > v)
                    exch(a, i, gt--);
                else
                    i++;
            }
            sort(a, lo, lt - 1, d, s);
            if (v >= 0)
                sort(a, lt, gt, d + 1, s);
            sort(a, gt + 1, hi, d, s);
        }

        private static void exch(CircularSuffix[] a, int i, int j) {
            CircularSuffix tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }

        private static int charAt(CircularSuffix cir, int d, String s) {
            int i = d + cir.start;
            if (i < s.length())
                return s.charAt(i);
            return s.charAt(i % s.length());
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
        TWSQS.sort(t, s);
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
        for (CircularSuffix cs : csa.t) {
            String str = csa.s.substring(cs.start, csa.length());
            System.out.println(str);
        }
        assert csa.index(0) == 11;
        assert csa.index(11) == 2;
        assert csa.index(1) == 10;
        assert csa.index(2) == 7;
    }
}