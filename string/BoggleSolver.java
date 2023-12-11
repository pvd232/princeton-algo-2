package string;

import java.util.HashSet;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private final TrieST dict;
    private int n;
    private int m;
    private char[][] g;
    private int[][] adj;

    // Initializes the data struct w/given array of strings as the dictionary.
    // Assume each word in the dict contains only uppercase letters A through Z.
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null)
            throw new IllegalArgumentException();
        dict = new TrieST();
        for (String s : dictionary)
            dict.put(s);
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

        int count = 0;
        int[] dir = { 1, -1 }, res = new int[len];
        if (n > 1)
            for (int dx : dir) {
                int adjX = j + dx;
                if (adjX < n && adjX > -1)
                    res[count++] = i * n + adjX;
            }
        if (m > 1)
            for (int dy : dir) {
                int adjY = i + dy;
                if (adjY < m && adjY > -1)
                    res[count++] = adjY * n + j;
            }
        if (m > 1 || n > 1)
            for (int dy : dir)
                for (int dx : dir) {
                    int adjY = i + dy, adjX = j + dx;
                    if (adjX < n && adjX > -1 && adjY < m && adjY > -1)
                        res[count++] = adjY * n + adjX;
                }
        return res;
    }

    private Iterable<String> findWords(BoggleBoard board) {
        HashSet<String> res = new HashSet<>();
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                dict.reset();
                String wNew = dict.prefix("", g[i][j]);
                if (wNew != null) {
                    HashSet<String> add = new HashSet<>();
                    add.add(coord(i, j));
                    findWords(board, i, j, wNew, add, res);
                }
            }
        return res;
    }

    private String coord(int i, int j) {
        return Integer.toString(i) + Integer.toString(j);
    }

    private void findWords(BoggleBoard board, int i, int j, String w, HashSet<String> add,
            HashSet<String> res) {
        if (w.length() > 2 && dict.contains(w))
            res.add(w);
        for (int p : adj[i * n + j]) {
            int row = p / n, col = p % n;
            char c = g[row][col];
            String coord = coord(row, col);
            if (!add.contains(coord)) {
                String wNew = dict.prefix(w, c);
                if (wNew != null) {
                    HashSet<String> newAdd = new HashSet<>(add);
                    newAdd.add(coord);
                    findWords(board, row, col, wNew, newAdd, res);
                }
            }
        }
        dict.remove();
    }

    // Returns the score of the given word if it is in the dict, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int len = word.length();
        if (len < 3 || !dict.contains(word))
            return 0;
        if (len < 5)
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
        int count = 0, wordCount = 0;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000) {
            BoggleBoard board = new BoggleBoard();
            solver.getAllValidWords(board);
            count++;
        }
        // while (count < 1000) {
        // BoggleBoard board = new BoggleBoard();
        // solver.getAllValidWords(board);
        // count++;
        // }
        long endTime = System.currentTimeMillis() - startTime;
        BoggleBoard board = new BoggleBoard(args[1]);

        Iterable<String> res = solver.getAllValidWords(board);

        int score = 0;
        for (String word : res) {
            StdOut.println("word " + word);
            score += solver.scoreOf(word);
            wordCount++;
        }
        System.out.println("Calls per second: " + count / 5);
        // System.out.println("Time: " + endTime);
        StdOut.println("Score = " + score + " Word count = " + wordCount);
    }
}