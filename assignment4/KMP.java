public class KMP {
    private static final int r = 256;
    private final int m;
    private final int[][] dfa;

    public KMP(String pat) {
        m = pat.length();
        dfa = new int[r][m];
        dfa[pat.charAt(0)][0] = 1;
        for (int X = 0, j = 1; j < m; j++) {
            for (int c = 0; c < r; c++)
                dfa[c][j] = dfa[c][X];
            dfa[pat.charAt(j)][j] = j + 1;
            X = dfa[pat.charAt(j)][X];
        }
    }

    public int search(String s) {
        int i, j, k;
        for (i = 0, j = 0, k = 0; k < s.length() && j < m; i++)
            j = dfa[s.charAt(k++)][j];
        if (j == m)
            return i - m;
        else
            return s.length();

    }
}
