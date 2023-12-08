package string;

import java.util.HashSet;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private final R2Trie<Integer> dict;
    private int n;
    private int m;
    private char[][] g;
    private int[][] adj;

    // Initializes the data struct w/given array of strings as the dictionary.
    // Assume each word in the dict contains only uppercase letters A through Z.
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null)
            throw new IllegalArgumentException();
        dict = new R2Trie<>();
        int i = 0;
        for (String s : dictionary)
            dict.put(s, i++);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null)
            throw new IllegalArgumentException();
        m = board.rows();
        n = board.cols();
        g = new char[m][n];
        adj = new int[m * n][];
        mkGraph(board);
        return findWords(board);
    }

    private void mkGraph(BoggleBoard board) {
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                g[i][j] = board.getLetter(i, j);
                adj[i * n + j] = adjN(i, j);
            }
    }

    private int[] adjN(int i, int j) {
        int len = 0;
        boolean rowIsEdge = false, colIsEdge = false;
        if (i == 0 || i == m - 1)
            rowIsEdge = true;
        if (j == 0 || j == n - 1)
            colIsEdge = true;

        if (rowIsEdge && colIsEdge)
            if (m > 1 && n > 1)
                len = 3;
            else
                len = 1;
        else if (!rowIsEdge && !colIsEdge)
            len = 8;
        else if (m > 2 && n > 2)
            len = 5;
        else if (m == 1 || n == 1)
            len = 2;
        else
            len = 1;

        int x = 0;
        int[] res = new int[len];
        int[] dir = { 1, -1 };
        for (int dx : dir) {
            int adjX = j + dx;
            if (adjX < n && adjX > -1)
                res[x++] = i * n + adjX;
        }
        int y = x;
        for (int dy : dir) {
            int adjY = i + dy;
            if (adjY < m && adjY > -1)
                res[y++] = adjY * n + j;
        }
        int xy = y;
        for (int dy : dir)
            for (int dx : dir) {
                int adjY = i + dy;
                int adjX = j + dx;
                if (adjX < n && adjX > -1 && adjY < m && adjY > -1)
                    res[xy++] = adjY * n + adjX;
            }
        return res;
    }

    private Iterable<String> findWords(BoggleBoard board) {
        HashSet<String> res = new HashSet<>();
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (dict.hasPrefix("", Character.toString(g[i][j])))
                    findWords(board, i, j, new StringBuilder(), new HashSet<>(), res);
        return res;
    }

    private String coord(int i, int j) {
        return Integer.toString(i) + Integer.toString(j);
    }

    private void findWords(BoggleBoard board, int i, int j, StringBuilder w, HashSet<String> add,
            HashSet<String> res) {
        w.toString();
        char c = g[i][j];
        if (c == 'Q')
            w.append("QU");
        else
            w.append(c);
        add.add(coord(i, j));

        String wS = w.toString();

        if (w.length() > 2 && dict.contains(wS))
            res.add(wS);

        for (int p : adj[i * n + j]) {
            int row = p / n, col = p % n;
            if (dict.hasPrefix(wS, wS + g[row][col]) && !add.contains(coord(row, col)))
                findWords(board, row, col, new StringBuilder(w), new HashSet<>(add), res);
        }
    }

    // Returns the score of the given word if it is in the dict, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null || !dict.contains(word))
            return 0;
        int len = word.length();
        if (len <= 2)
            return 0;
        else if (len < 5)
            return 1;
        else if (len == 5)
            return 2;
        else if (len == 6)
            return 3;
        else if (len == 7)
            return 5;
        else
            return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++)
            solver.getAllValidWords(board);

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Time: " + timeElapsed);

        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            // StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}