package string;

import edu.princeton.cs.algs4.Queue;
import java.util.HashMap;

public class TST {
    private Node root;
    private final HashMap<String, Node> cache = new HashMap<>(1000);
    private Node prev;
    private String prevS;

    private class Node {
        private Integer val;
        private char c;
        private Node left, mid, right;
    }

    public TST(String key) {
        // if (key.length() == 2)
        // root = new Node();
        // else
        put(key, 0);
    }

    public void put(String key, Integer val) {
        // if (key.length() > 2)
        // root = put(root, key, val, 2);
        // else
        root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Integer val, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node();
            x.c = c;
        }
        if (c < x.c)
            x.left = put(x.left, key, val, d);
        else if (c > x.c)
            x.right = put(x.right, key, val, d);
        else if (d < key.length() - 1)
            x.mid = put(x.mid, key, val, d + 1);
        else
            x.val = val;
        return x;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public Integer get(String key) {
        if (prevS != null && prevS.equals(key))
            return get(prev, key, key.length() - 1).val;

        Node x = get(root, key, 0);
        // Node x = get(root, key, 2);
        if (x == null)
            return null;
        else
            return x.val;

    }

    private Node cached(String old) {
        if (old.equals(prevS))
            return prev;
        else if (cache.containsKey(old))
            return cache.get(old);
        else
            return null;
    }

    private Node get(Node x, String key, int d) {
        if (x == null)
            return null;
        else if (cache.containsKey(key))
            return cache.get(key);
        char c = key.charAt(d);
        if (c < x.c)
            return get(x.left, key, d);
        else if (c > x.c)
            return get(x.right, key, d);
        else if (d < key.length() - 1)
            return get(x.mid, key, d + 1);
        else {
            prev = x;
            prevS = key;
            cache.put(key, x);
            return x;
        }
    }

    public Iterable<String> keys() {
        Queue<String> queue = new Queue<String>();
        collect(root, "", queue);
        return queue;
    }

    public boolean hasPrefix(String old, String prefix) {
        Node x = cached(old);
        if (x == null)
            x = get(root, prefix, 0);
        // x = get(root, prefix, 2);
        else
            x = get(x, prefix, old.length() - 1);
        return x != null;
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> q = new Queue<>();
        Node x = get(root, prefix, 0);
        collect(x, prefix, q);
        if (q.isEmpty())
            return null;
        else
            return q;
    }

    private void collect(Node x, String prefix, Queue<String> q) {
        if (x == null)
            return;
        if (x.val != null)
            q.enqueue(prefix);
        collect(x.right, prefix + x.c, q);
        collect(x.mid, prefix + x.c, q);
        collect(x.left, prefix + x.c, q);
    }

    public static void main(String[] args) {
        // In in = new In(args[0]);
        // String[] dictionary = in.readAllStrings();

        TST trie = new TST("");

        // int i = 0;
        // for (String word : dictionary)
        // trie.put(word, i++, 0);

        Iterable<String> keysWP = trie.keysWithPrefix("SORT");
        assert keysWP != null;
        for (String s : keysWP) {
            System.out.println(s);
        }
    }
}