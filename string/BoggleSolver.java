package string;

import java.util.HashSet;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.KMP;

public class BoggleSolver {
    private R2Trie<Integer> dict;

    // Initializes the data struct w/given array of strings as the dictionary.
    // Assume each word in the dict contains only uppercase letters A through Z.
    public BoggleSolver(String[] dictionary) {
        dict = new R2Trie<>();
        int i = 0;
        for (String s : dictionary)
            dict.put(s, i++);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Iterable<String> allWords = findWords(board);
        return allWords;
    }

    private Iterable<String> findWords(BoggleBoard board) {
        HashSet<String> res = new HashSet<>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++)
                findWords(board, i, j, new StringBuilder(), new StringBuilder(), res);
        }
        return res;
    }

    private String coord(int i, int j) {
        StringBuilder s = new StringBuilder();
        s.append("(");
        s.append(Integer.toString(i));
        s.append("-");
        s.append(Integer.toString(j));
        s.append(")");
        return s.toString();
    }

    private void findWords(BoggleBoard board, int i, int j, StringBuilder w, StringBuilder add,
            HashSet<String> res) {

        if (w.length() < 2 || dict.keysWithPrefix(w.toString()) != null) {
            w.append(board.getLetter(i, j));
            add.append(coord(i, j));

            if (w.length() > 2 && dict.contains(w.toString()))
                res.add(w.toString());

            int[] dir = { 1, -1 };
            for (int dx : dir) {
                int adjX = j + dx;
                if (adjX < board.cols() && adjX > -1) {
                    KMP kmp = new KMP(coord(i, adjX));
                    if (kmp.search(add.toString()) == add.length())
                        findWords(board, i, adjX, new StringBuilder(w.toString()), new StringBuilder(add.toString()),
                                res);
                }
            }
            for (int dy : dir) {
                int adjY = i + dy;
                if (adjY < board.rows() && adjY > -1) {
                    KMP kmp = new KMP(coord(adjY, j));
                    if (kmp.search(add.toString()) == add.length())
                        findWords(board, adjY, j, new StringBuilder(w.toString()), new StringBuilder(add.toString()),
                                res);
                }
            }
            for (int dy : dir) {
                for (int dx : dir) {
                    int adjX = j + dx;
                    int adjY = i + dy;
                    if (adjX < board.cols() && adjX > -1 && adjY < board.rows() && adjY > -1) {
                        KMP kmp = new KMP(coord(adjY, adjX));
                        if (kmp.search(add.toString()) == add.length())
                            findWords(board, adjY, adjX, new StringBuilder(w.toString()),
                                    new StringBuilder(add.toString()),
                                    res);
                    }
                }
            }
        }
    }

    // Returns the score of the given word if it is in the dict, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        assert dict.contains(word);
        if (word.length() <= 2)
            return 0;
        if (word.length() < 5)
            return 1;
        else if (word.length() == 5)
            return 2;
        else if (word.length() == 6)
            return 3;
        else if (word.length() == 7)
            return 5;
        else
            return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        assert board.rows() == 4;
        assert board.cols() == 4;
        assert board.getLetter(0, 0) == 'A';
        assert board.getLetter(0, 1) == 'T';
        assert board.getLetter(0, 3) == 'E';

        HashSet<String> bruteDict = new HashSet<>();
        for (String s : dictionary)
            bruteDict.add(s);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            assert bruteDict.contains(word);
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
