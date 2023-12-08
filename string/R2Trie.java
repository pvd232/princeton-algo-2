package string;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

public class R2Trie<Value> {
    private static final int R = 26;
    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        private final TST[] next = new TST[(R * R) + R];
    }

    public void put(String key, Value val) {
        int pre = pre(key);
        if (root.next[key.charAt(0)] == null)
            root.next[key.charAt(0)] = new TST();
        if (key.length() > 1 && root.next[pre] == null)
            root.next[pre] = new TST();
        root.next[pre].put(key, (Integer) val);
    }

    private int pre(String key) {
        if (key.isEmpty())
            throw new IllegalArgumentException();
        else if (key.length() == 1)
            return key.charAt(0);
        else
            return key.charAt(0) + key.charAt(1);
    }

    public boolean contains(String key) {
        if (key.length() < 3 && root.next[pre(key)] != null)
            return true;
        else
            return get(key) != null;
    }

    public Object get(String key) {
        int pre = pre(key);
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
        TST x = root.next[pre(prefix)];
        if (x == null)
            return false;
        if (prefix.length() < 3)
            return true;
        else
            return x.hasPrefix(old, prefix);
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        TST x = root.next[pre(prefix)];
        if (x == null)
            return null;
        else
            return x.keysWithPrefix(prefix);
    }

    private void collect(Queue<String> q) {
        for (char c = 0; c < R * R; c++)
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

        Iterable<String> keysWP = trie.keysWithPrefix("BRE");
        assert keysWP != null;
        for (String s : keysWP)
            System.out.println(s);

        assert trie.contains("SORT");
        assert trie.contains("BREEID");
    }
}