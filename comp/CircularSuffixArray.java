package comp;

import edu.princeton.cs.algs4.In;

public class CircularSuffixArray {
    private final String s; // Reference to input string
    private final int n; // Input string length
    private final int[] t; // Implicit circular suffix array of pointers to char idx

    private static class TWSQS {
        public static void sort(int[] a, String s) {
            sort(a, 0, a.length - 1, 0, s, s.length());
        }

        private static void sort(int[] a, int lo, int hi, int d, String s, int n) {
            if (hi <= lo)
                return;
            int lt = lo, gt = hi;
            int v = charAt(a[lo], d, s, n);
            int i = lo + 1;
            while (i <= gt) {
                // 3-way partitioning (using dth character) to handle variable-length strings
                int t = charAt(a[i], d, s, n);
                if (t < v)
                    exch(a, lt++, i++);
                else if (t > v)
                    exch(a, i, gt--);
                else
                    i++;
            }
            sort(a, lo, lt - 1, d, s, n);
            if (v >= 0)
                sort(a, lt, gt, d + 1, s, n);
            sort(a, gt + 1, hi, d, s, n);
        }

        private static void exch(int[] a, int i, int j) {
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }

        private static int charAt(int start, int d, String s, int n) {
            int i = d + start; // Set i to char index (d) shifted by the starting index of the suffix
            if (i < n) // Char idx is before the end of the string
                return s.charAt(i);
            else if (i % n == start) // For periodic string must ensure no infinite loop
                return -1;
            return s.charAt(i % n); // Map idx to within string bounds if idx > n
        }
    }

    // Circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException();

        this.s = s;
        n = s.length();
        t = new int[n];

        for (int i = 0; i < n; i++)
            t[i] = i;
        TWSQS.sort(t, s);
    }

    // Length of s
    public int length() {
        return n;
    }

    // Returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n)
            throw new IllegalArgumentException();
        return t[i];
    }

    // Unit testing (required)
    public static void main(String[] args) {
        In in = new In(args[0]);
        String input = in.readAll();
        CircularSuffixArray csa = new CircularSuffixArray(input);

        String[] suffix = new String[input.length()];
        for (int i = 0; i < input.length(); i++) {
            String newSuffix = input.substring(i, input.length()) + input.substring(0, i);
            suffix[i] = newSuffix;
        }

        int count = 0;
        String curr = "";
        String[] sortedSuff = new String[csa.s.length()];
        for (int cs : csa.t) {
            String prev = curr;
            curr = csa.s.substring(cs, csa.s.length()) + csa.s.substring(0, cs);
            sortedSuff[count++] = curr;

            int i = 0;
            if (!prev.equals("")) {
                while (i < curr.length() && curr.charAt(i) == prev.charAt(i))
                    i++;

                // Ensure proper sorting
                if (i < curr.length())
                    assert curr.charAt(i) > prev.charAt(i);
            }
        }
        assert count == csa.s.length();

        for (int i = 0; i < sortedSuff.length; i++) {
            // Original suffix
            String originalSuffix = suffix[csa.index(i)];
            // Sorted suffix
            String sortedSuffix = sortedSuff[i];
            // Should always be equal
            assert originalSuffix.equals(sortedSuffix);
        }
    }
}