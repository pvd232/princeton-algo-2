package string;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

public class R2Trie<Value> {
    private static final int R = 26;
    private static final int len = (R * R * R) + (R * R) + R;
    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        public final TST[] next = new TST[len];
    }

    public void put(String key, Value val) {
        if (root.next[pre(key, true, false)] == null)
            root.next[pre(key, true, false)] = new TST(key);
        if (key.length() > 1 && root.next[pre(key, false, true)] == null)
            root.next[pre(key, false, true)] = new TST(key);
        if (key.length() > 2 && root.next[pre(key, false, false)] == null)
            root.next[pre(key, false, false)] = new TST(key);
        if (key.length() > 3)
            root.next[pre(key, false, false)].put(key, (Integer) val);
    }

    private int pre(String key, boolean first, boolean second) {
        int xIdx = (Character.getNumericValue(key.charAt(0)) - 10);
        int yIdx, zIdx;
        if (first || key.length() == 1) {
            return xIdx;
        } else if (second || key.length() == 2) {
            xIdx = xIdx * R;
            yIdx = (Character.getNumericValue(key.charAt(1)) - 10);
            return R + xIdx + yIdx;
        } else {
            xIdx = xIdx * R * R;
            yIdx = (Character.getNumericValue(key.charAt(1)) - 10) * R;
            zIdx = (Character.getNumericValue(key.charAt(2)) - 10);
            return R + (R * R) + xIdx + yIdx + zIdx;
        }
    }

    public boolean contains(String key) {
        if (key.length() < 3)
            return false;
        else if (key.length() == 3 && root.next[pre(key, false, false)] != null)
            return root.next[pre(key, false, false)].isWord();
        else
            return get(key) != null;
    }

    public Object get(String key) {
        int pre = pre(key, false, false);
        if (root.next[pre] == null)
            return null;
        else
            return root.next[pre].get(key);
    }

    public Iterable<String> keys() {
        Queue<String> q = new Queue<String>();
        collect(q);
        return q;
    }

    public boolean hasPrefix(String old, String prefix) {
        TST x = root.next[pre(prefix, false, false)];
        if (x == null)
            return false;
        if (prefix.length() < 4)
            return true;
        // if (prefix.length() == 3)
        // return x.isWord();
        else
            return x.hasPrefix(old, prefix);
    }

    private void collect(Queue<String> q) {
        for (char c = 0; c < len; c++)
            if (root.next[c] != null)
                for (String s : root.next[c].keys())
                    q.enqueue(s);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();

        R2Trie<Integer> trie = new R2Trie<>();

        int i = 0;
        for (String word : dictionary)
            trie.put(word, i++);

        int j = 0;
        int count = 0;
        for (TST t : trie.root.next) {

            if (t != null) {
                count++;
                // System.out.println("j " + j);

            }
            j++;
        }
        System.out.println("count " + count);

        assert trie.contains("SORT");
        assert trie.contains("BREEDING");
    }
}