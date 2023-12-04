package string;

import java.util.Arrays;

// Finds the longest repeating substring using suffix arrays
public class LRS {
    public static String lrs(String s) {
        int N = s.length();
        String[] suffixes = new String[N];
        for (int i = 0; i < N; i++)
            suffixes[i] = s.substring(i, N);
        Arrays.sort(suffixes);
        String lrs = "";
        for (int i = 0; i < N - 1; i++) {
            int len = lcp(suffixes[i], suffixes[i + 1]);
            if (len > lrs.length())
                lrs = suffixes[i].substring(0, len);
        }
        return lrs;
    }

    // Returns the longest common prefix of two strings
    private static int lcp(String a, String b) {
        int len = Math.max(a.length(), b.length()); // Get the longer count
        for (int i = 0; i < len; i++) {
            if (i == a.length() || i == b.length()) // Check if the end of either string has been reached
                return i - 1;
            if (a.charAt(i) != b.charAt(i)) // Check for mismatch
                return i;
        }
        return 0;
    }

    public static void main(String[] args) {
        String test = "abcddddefddlopapapapacfefetruabcdddd";
        String res = LRS.lrs(test);
        System.out.println(res);
    }
}