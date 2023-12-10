import edu.princeton.cs.algs4.Queue;

public class TrieST {
    private static final int R = 26;
    private Node root = new Node();
    private Node prev;
    private String prevS;

    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    public void put(String key, Integer val) {
        root = put(root, key, val, 2);
    }

    private Node put(Node x, String key, Integer val, int d) {
        if (x == null)
            x = new Node();
        if (d == key.length()) {
            x.val = val;
            return x;
        }
        char c = key.charAt(d);
        x.next[Character.getNumericValue(c) - 10] = put(x.next[Character.getNumericValue(c) - 10], key, val, d + 1);
        return x;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    private Node cached(String old) {
        if (old.equals(prevS))
            return prev;
        else
            return null;
    }

    public boolean hasPrefix(String old, String prefix) {
        Node x = cached(old);
        if (x == null)
            x = get(root, prefix, 2);
        else
            x = get(x, prefix, old.length());
        return x != null;
    }

    public Object get(String key) {
        Node x;
        if (key.equals(prevS))
            x = prev;
        else
            x = get(root, key, 2);

        if (x == null)
            return null;
        else
            return x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null)
            return null;
        if (d == key.length()) {
            prev = x;
            prevS = key;
            return x;
        }
        char c = key.charAt(d);
        return get(x.next[Character.getNumericValue(c) - 10], key, d + 1);
    }
    // private Node get(Node x, String key, int d) {
    // while (d < key.length()) {
    // if (x == null)
    // return null;
    // x = x.next[Character.getNumericValue(key.charAt(d++)) - 10];
    // }
    // prev = x;
    // prevS = key;
    // return x;
    // }

    public Iterable<String> keys() {
        Queue<String> queue = new Queue<String>();
        collect(root, "", queue);
        return queue;
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> q = new Queue<>();
        Node x = get(root, prefix, 2);
        collect(x, prefix, q);
        return q;
    }

    private void collect(Node x, String prefix, Queue<String> q) {
        if (x == null)
            return;
        if (x.val != null)
            q.enqueue(prefix);
        for (char c = 0; c < R; c++)
            collect(x.next[Character.getNumericValue(c) - 10], prefix + c, q);
    }
}
