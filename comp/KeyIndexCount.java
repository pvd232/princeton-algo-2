package comp;

public class KeyIndexCount {
    private static final int R = 256;

    public static void sort(char[] a) {
        int N = a.length;
        int[] count = new int[R + 1], aux = new int[N];
        for (int i = 0; i < N; i++)
            count[a[i] + 1]++;
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        for (int i = 0; i < N; i++)
            aux[count[a[i]]++] = a[i];
        for (int i = 0; i < N; i++)
            a[i] = (char) aux[i];
    }
}
