import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

public class R2Trie<Value> {
    private static final int R = 26;
    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        public final TrieST[] next = new TrieST[(R * R) + R];
    }

    public void put(String key, Value val) {
        if (root.next[pre(key, true)] == null)
            root.next[pre(key, true)] = new TrieST();

        if (root.next[pre(key, false)] == null && key.length() > 1)
            root.next[pre(key, false)] = new TrieST();
        if (key.length() > 2)
            root.next[pre(key, false)].put(key, (Integer) val);
    }

    private int pre(String key, boolean first) {
        int idx = (Character.getNumericValue(key.charAt(0)) - 10) * R;
        int shift = (Character.getNumericValue(key.charAt(0)) - 10) * 1;
        if (first || key.length() == 1) {
            return idx + shift;
        } else {
            int idx2 = Character.getNumericValue(key.charAt(1)) - 10;
            return idx + shift + idx2;
        }
    }

    public boolean contains(String key) {
        if (root.next[pre(key, false)] != null && key.length() < 3)
            return true;
        else
            return get(key) != null;
    }

    public Object get(String key) {
        int pre = pre(key, false);
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
        TrieST x = root.next[pre(prefix, false)];
        if (x == null)
            return false;
        else if (prefix.length() < 3)
            return true;
        else
            return x.hasPrefix(old, prefix);
    }

    private void collect(Queue<String> q) {
        for (char c = R; c < R * R + R; c++)
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

        assert trie.contains("SORT");
        assert trie.contains("BREEID");
    }
}