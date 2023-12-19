package comp;

public class KeyIndexCount {
    private static final int R = 256;

    public static void sort(char[] a) {
        int n = a.length;
        int[] count = new int[R + 1], aux = new int[n];
        for (int i = 0; i < n; i++)
            count[a[i] + 1]++;
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        for (int i = 0; i < n; i++)
            aux[count[a[i]]++] = a[i];
        for (int i = 0; i < n; i++)
            a[i] = (char) aux[i];
    }
}
