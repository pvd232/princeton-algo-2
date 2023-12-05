package string;

import java.util.HashSet;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private final R2Trie<Integer> dict;
    private int n;
    private int m;

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
        return findWords(board);
    }

    private Iterable<String> findWords(BoggleBoard board) {
        HashSet<String> res = new HashSet<>();
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                findWords(board, i, j, new StringBuilder(), new StringBuilder(), res);
        return res;
    }

    private String coord(int i, int j) {
        StringBuilder s = new StringBuilder();
        s.append(Integer.toString(i));
        s.append(Integer.toString(j));
        s.append(",");
        return s.toString();
    }

    private void findWords(BoggleBoard board, int i, int j, StringBuilder w, StringBuilder add,
            HashSet<String> res) {

        if (w.length() < 2 || dict.hasKeys(w.toString())) {
            char c = board.getLetter(i, j);
            if (c == 'Q')
                w.append("QU");
            else
                w.append(c);
            add.append(coord(i, j));

            String wS = w.toString(), addS = add.toString();
            int aN = add.length();

            if (w.length() > 2 && dict.contains(wS))
                res.add(wS);

            int[] dir = { 1, -1 };
            for (int dx : dir) {
                int adjX = j + dx;
                if (adjX < n && adjX > -1) {
                    KMP kmp = new KMP(coord(i, adjX));
                    if (kmp.search(addS) == aN)
                        findWords(board, i, adjX, new StringBuilder(wS), new StringBuilder(addS),
                                res);
                }
            }
            for (int dy : dir) {
                int adjY = i + dy;
                if (adjY < m && adjY > -1) {
                    KMP kmp = new KMP(coord(adjY, j));
                    if (kmp.search(addS) == aN)
                        findWords(board, adjY, j, new StringBuilder(wS), new StringBuilder(addS),
                                res);
                }
            }
            for (int dy : dir) {
                for (int dx : dir) {
                    int adjX = j + dx, adjY = i + dy;
                    if (adjX < n && adjX > -1 && adjY < m && adjY > -1) {
                        KMP kmp = new KMP(coord(adjY, adjX));
                        if (kmp.search(addS) == aN)
                            findWords(board, adjY, adjX, new StringBuilder(wS),
                                    new StringBuilder(addS),
                                    res);
                    }
                }
            }
        }
    }

    // Returns the score of the given word if it is in the dict, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null || !dict.contains(word))
            return 0;
        int n = word.length();
        if (n <= 2)
            return 0;
        else if (n < 5)
            return 1;
        else if (n == 5)
            return 2;
        else if (n == 6)
            return 3;
        else if (n == 7)
            return 5;
        else
            return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        // HashSet<String> bruteDict = new HashSet<>();
        // for (String s : dictionary)
        // bruteDict.add(s);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++)
            solver.getAllValidWords(board);

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Time: " + timeElapsed);

        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            // assert bruteDict.contains(word);
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
