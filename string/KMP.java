package string;

public class KMP {
    private static final int R = 26;
    private String pat;
    private int m;
    private int n;
    private int[][] dfa;

    private KMP(String pat) {
        this.pat = pat;
        m = pat.length();
        dfa = new int[R][m];
        dfa[pat.charAt(0)][0] = 1;
        for (int X = 0, j = 1; j < m; j++) {
            for (int c = 0; c < R; c++)
                dfa[c][j] = dfa[c][X];
            dfa[pat.charAt(j)][j] = j + 1;
            X = dfa[pat.charAt(j)][X];
        }
    }

    public static void substring(String s) {
    }
}
