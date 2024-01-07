import java.util.HashSet;
import edu.princeton.cs.algs4.In;

public class BoggleSolver {
    private static final int[] DIR = { -1, 1 };
    private final TrieST dict;
    private int n;
    private int m;

    // Initializes the data struct w/given array of strings as the dictionary
    public BoggleSolver(String[] dictionary) { // Assume each word in the dict contains only uppercase letters A - Z
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

        int[][] adj = new int[m * n][]; // Precompute each tile's adjacent tiles
        char[][] g = new char[m][n];

        // Build the graph
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                g[i][j] = board.getLetter(i, j);
                adj[i * n + j] = adjN(i, j);
            }
        return findWords(board, adj, g);
    }

    private int[] adjN(int i, int j) {
        boolean rowIsEdge = false, colIsEdge = false;
        if (i == 0 || i == m - 1)
            rowIsEdge = true;
        if (j == 0 || j == n - 1)
            colIsEdge = true;

        int len = 1;
        if (!rowIsEdge && !colIsEdge) // n & m must be > 1 for non edge point to exist, must have 8 adjacencies
            len = 8;
        else if (rowIsEdge && colIsEdge)
            if (m > 1 && n > 1)
                len = 3;
            else
                len = 1;
        else if (m > 2 && n > 2)
            len = 5;
        else if (m == 1 || n == 1)
            len = 2;

        int count = 0;
        int[] res = new int[len];

        // Diagonal
        if (m > 1 && n > 1)
            if (!rowIsEdge && !colIsEdge)
                for (int dy : DIR)
                    for (int dx : DIR)
                        res[count++] = (i + dy) * n + (j + dx);
            else if (rowIsEdge && !colIsEdge)
                for (int dx : DIR)
                    if (i == 0)
                        res[count++] = (i + 1) * n + (j + dx);
                    else
                        res[count++] = (i - 1) * n + (j + dx);

            else if (!rowIsEdge && colIsEdge)
                for (int dy : DIR)
                    if (j == 0)
                        res[count++] = (i + dy) * n + (j + 1);
                    else
                        res[count++] = (i + dy) * n + (j - 1);
            else if (i == 0 && j == 0)
                res[count++] = (i + 1) * n + (j + 1);
            else if (i == 0 && j == n - 1)
                res[count++] = (i + 1) * n + (j - 1);
            else if (i == m - 1 && j == 0)
                res[count++] = (i - 1) * n + (j + 1);
            else if (i == m - 1 && j == n - 1)
                res[count++] = (i - 1) * n + (j - 1);

        if (n > 1) // Col
            if (!colIsEdge)
                for (int dx : DIR)
                    res[count++] = (i * n) + (j + dx);
            else if (j == n - 1)
                res[count++] = (i * n) + (j - 1);
            else if (j == 0)
                res[count++] = (i * n) + (j + 1);

        if (m > 1) // Row
            if (!rowIsEdge)
                for (int dy : DIR)
                    res[count++] = n * (i + dy) + j;
            else if (i == m - 1)
                res[count++] = n * (i - 1) + j;
            else if (i == 0)
                res[count++] = n * (i + 1) + j;
        return res;
    }

    // Create wrapper function to store results globally
    private Iterable<String> findWords(BoggleBoard board, int[][] adj, char[][] g) {
        HashSet<String> res = new HashSet<>();
        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                String wNew = dict.prefix("", g[i][j]);
                if (wNew != null) {
                    visited[i][j] = true;
                    findWords(board, adj, g, visited, new int[] { i, j }, wNew, res);
                    visited[i][j] = false; // Backtrack after recursive call completes
                }
            }
        return res;
    }

    // Recursive DFS enumeration
    private void findWords(BoggleBoard board, int[][] adj, char[][] g, boolean[][] visited, int[] coord, String w,
            HashSet<String> res) {
        if (dict.hasKids(w, res)) // If word != trie leaf explore adj
            for (int p : adj[coord[0] * n + coord[1]]) {
                int row = p / n, col = p % n;
                coord[0] = row;
                coord[1] = col;
                if (!visited[row][col]) {
                    String wNew = dict.prefix(w, g[row][col]);
                    if (wNew != null) {
                        visited[row][col] = true;
                        findWords(board, adj, g, visited, coord, wNew, res);
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
        int count = 0;
        BoggleBoard board = new BoggleBoard(args[1]);
        long startTime = System.currentTimeMillis();
        solver.getAllValidWords(board);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("Time: " + endTime);
        while (System.currentTimeMillis() - startTime <= 5000) {
            BoggleBoard testBoard = new BoggleBoard();
            solver.getAllValidWords(testBoard);
            count++;
        }

        // BoggleBoard board = new BoggleBoard(args[1]);

        // Iterable<String> res = solver.getAllValidWords(board);
        // ArrayList<String> resSorted = new ArrayList<>();
        // for (String s : res)
        // resSorted.add(s);
        // Collections.sort(resSorted);
        // int score = 0, wordCount = 0;
        // for (String word : resSorted) {
        // StdOut.println("word " + word);
        // score += solver.scoreOf(word);
        // }
        System.out.println("Calls per second: " + count / 5);
        // StdOut.println("Score = " + score + " Word count = " + wordCount);
    }
}