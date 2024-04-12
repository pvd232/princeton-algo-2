package string;

import java.util.ArrayList;
import edu.princeton.cs.algs4.In;
import java.util.Collections;

public class BoggleSolver {
    private static final int[] DIR = { -1, 1 };
    private final TrieST dict;
    private int m = 0;
    private int n = 0;
    private int[][] adj;
    private char[][] g;
    private boolean[][] visited;
    private static final int R = 26;

    private static class Node {
        private String val;
        private String path;
        private boolean isParent;
        private int id = -1;
        private final Node[] next = new Node[R];
    }

    private class TrieST {
        private Node root = new Node();
        private Node prev = new Node();
        private int cnt = 0;

        public void put(String key) {
            root = put(root, key, 0, key.length());
        }

        private Node put(Node x, String key, int d, int n) {
            if (x == null)
                x = new Node();
            if (d == n) {
                if (n > 2)
                    x.val = key;
                x.path = key;
                return x;
            }
            int c = key.charAt(d) - 65;
            if (x.path == null)
                x.path = key.substring(0, d);
            x.isParent = true;
            x.next[c] = put(x.next[c], key, d + 1, n);
            return x;
        }

        private Node get(Node x, String key, int d, char ch, int n) {
            if (x == null)
                return null;
            else if (d < n)
                return get(x.next[key.charAt(d) - 65], key, d + 1, ch, n);
            else if (d == n)
                if (ch == 'Q')
                    return get(x.next[ch - 65], key, d, 'U', n);
                else
                    return get(x.next[ch - 65], key, d + 1, ch, n);
            return x;
        }

        // Returns the Node for a given key
        private Node get(Node x, String key, int d, int n) {
            if (x == null || d == n)
                return x;
            return get(x.next[key.charAt(d) - 65], key, d + 1, n);
        }

        private String get(String key) {
            Node x = get(root, key, 0, key.length());
            if (x == null)
                return null;
            return x.val;
        }

        public boolean contains(String key) {
            return get(key) != null;
        }

        public Node prefix(Node old, char c) {
            Node x;
            int n = old.path.length();
            if (old.equals(prev))
                x = get(prev, old.path, n, c, n);
            else
                x = get(root, old.path, 0, c, n);
            if (x == null)
                return null;
            else {
                prev = x;
                return x;
            }
        }

        // Node will always exist
        public boolean hasKids(Node key, ArrayList<String> res) {
            Node x;
            if (key.equals(prev))
                x = prev;
            else
                x = get(root, key.val, 0, key.val.length());
            if (x.val != null && x.id != cnt) {
                x.id = cnt;
                res.add(key.val);
            }
            return x.isParent;
        }

        public void setCnt() {
            this.cnt++;
        }
    }

    // Initializes the data struct w/given array of strings as the dictionary
    public BoggleSolver(String[] dictionary) { // Assume each word in the dict contains only uppercase letters A - Z
        if (dictionary == null)
            throw new IllegalArgumentException();
        dict = new TrieST();
        for (String s : dictionary)
            dict.put(s);
        adj = new int[16][];
        g = new char[4][4];
        visited = new boolean[4][4];
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null)
            throw new IllegalArgumentException();

        boolean wasChanged = false;
        if (board.rows() != n || board.cols() != m) {
            m = board.cols();
            n = board.rows();
            wasChanged = true;
        }

        if (n * m > adj.length)
            adj = new int[n * m][];

        if (n > g.length || m > g[0].length) {
            g = new char[n][m];
            visited = new boolean[n][m];
        }

        // Build the graph
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                g[i][j] = board.getLetter(i, j);
                int k = i * m + j;
                if (wasChanged || adj[k] == null)
                    adj[k] = adjN(i, j); // Precompute each tile's adjacent tiles
            }
        return findWords(board);
    }

    private int[] adjN(int i, int j) {
        boolean rowIsEdge = false, colIsEdge = false;
        if (i == 0 || i == n - 1)
            rowIsEdge = true;
        if (j == 0 || j == m - 1)
            colIsEdge = true;

        int len = 1;
        if (!rowIsEdge && !colIsEdge) // m & n must be > 1 for non edge point to exist, must have 8 adjacencies
            len = 8;
        else if (rowIsEdge && colIsEdge)
            if (n > 1 && m > 1)
                len = 3;
            else
                len = 1;
        else if (n > 2 && m > 2)
            len = 5;
        else if (n == 1 || m == 1)
            len = 2;

        int count = 0;
        int[] res = new int[len];

        // Diagonal
        if (n > 1 && m > 1)
            if (!rowIsEdge && !colIsEdge)
                for (int dy : DIR)
                    for (int dx : DIR)
                        res[count++] = (i + dy) * m + (j + dx);
            else if (rowIsEdge && !colIsEdge)
                for (int dx : DIR)
                    if (i == 0)
                        res[count++] = (i + 1) * m + (j + dx);
                    else
                        res[count++] = (i - 1) * m + (j + dx);

            else if (!rowIsEdge && colIsEdge)
                for (int dy : DIR)
                    if (j == 0)
                        res[count++] = (i + dy) * m + (j + 1);
                    else
                        res[count++] = (i + dy) * m + (j - 1);
            else if (i == 0 && j == 0)
                res[count++] = (i + 1) * m + (j + 1);
            else if (i == 0 && j == m - 1)
                res[count++] = (i + 1) * m + (j - 1);
            else if (i == n - 1 && j == 0)
                res[count++] = (i - 1) * m + (j + 1);
            else if (i == n - 1 && j == m - 1)
                res[count++] = (i - 1) * m + (j - 1);

        if (m > 1) // Col
            if (!colIsEdge)
                for (int dx : DIR)
                    res[count++] = (i * m) + (j + dx);
            else if (j == m - 1)
                res[count++] = (i * m) + (j - 1);
            else if (j == 0)
                res[count++] = (i * m) + (j + 1);

        if (n > 1) // Row
            if (!rowIsEdge)
                for (int dy : DIR)
                    res[count++] = m * (i + dy) + j;
            else if (i == n - 1)
                res[count++] = m * (i - 1) + j;
            else if (i == 0)
                res[count++] = m * (i + 1) + j;
        return res;
    }

    // Create wrapper function to store results globally
    private Iterable<String> findWords(BoggleBoard board) {
        ArrayList<String> res = new ArrayList<>();

        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                Node wNew = dict.prefix(null, g[i][j]);
                if (wNew != null) {
                    visited[i][j] = true;
                    findWords(board, new int[] { i, j }, wNew, res);
                    visited[i][j] = false; // Backtrack after recursive call completes
                }
            }
        dict.setCnt();
        return res;
    }

    // Recursive DFS enumeration
    private void findWords(BoggleBoard board, int[] coord, Node w,
            ArrayList<String> res) {
        if (dict.hasKids(w, res)) // If != trie leaf, explore adj
            for (int p : adj[coord[0] * m + coord[1]]) {
                int row = p / m, col = p % m;
                coord[0] = row;
                coord[1] = col;
                if (!visited[row][col]) {
                    Node wNew = dict.prefix(w, g[row][col]);
                    if (wNew != null) {
                        visited[row][col] = true;
                        findWords(board, coord, wNew, res);
                        visited[row][col] = false;
                    }
                }
            }
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
        BoggleBoard board = new BoggleBoard(args[1]);
        long startTime = System.currentTimeMillis();

        int count = 0;
        while (System.currentTimeMillis() - startTime <= 5000) {
            // BoggleBoard testBoard = new BoggleBoard();
            solver.getAllValidWords(board);
            count++;
        }

        BoggleBoard otherBoard = new BoggleBoard(4, 5);

        Iterable<String> res = solver.getAllValidWords(otherBoard);
        ArrayList<String> resSorted = new ArrayList<>();
        for (String s : res)
            resSorted.add(s);

        Collections.sort(resSorted);
        int score = 0, wordCount = 0;
        for (String word : res) {
            // System.out.println("word " + word);
            score += solver.scoreOf(word);
        }
        System.out.println("Calls per second: " + count / 5);
        System.out.println("Score = " + score + " Word count = " + wordCount);
    }
}