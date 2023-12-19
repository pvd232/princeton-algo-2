import edu.princeton.cs.algs4.In;

public class CircularSuffixArray {
    private final String s;
    private final int n;
    private final CircularSuffix[] t;

    private static class CircularSuffix {
        private final int start;

        private CircularSuffix(int start) {
            this.start = start;
        }
    }

    private static class TWSQS {
        public static void sort(CircularSuffix[] a, String s) {
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
            int len = s.length();
            int i = d + cir.start;
            if (i < len)
                return s.charAt(i);
            else if (i % len == cir.start) // For periodic string must ensure no infinite loop
                return -1;
            else
                return s.charAt(i % len);
        }
    }

    // Circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException();

        this.s = s;
        n = s.length();
        t = new CircularSuffix[n];

        for (int i = 0; i < n; i++)
            t[i] = new CircularSuffix(i);
        TWSQS.sort(t, s);
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n)
            throw new IllegalArgumentException();
        return t[i].start;
    }

    // unit testing (required)
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
        for (CircularSuffix cs : csa.t) {
            String prev = curr;
            curr = csa.s.substring(cs.start, csa.s.length()) + csa.s.substring(0, cs.start);
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