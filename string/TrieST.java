package string;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class TrieST {
    private static final int R = 26;
    private Node root = new Node();
    private Stack<Node> path = new Stack<>();

    private static class Node {
        private String val;
        private String path;
        private final Node[] next = new Node[R];
    }

    public void put(String key) {
        root = put(root, key, 0);
    }

    private Node put(Node x, String key, int d) {
        if (x == null)
            x = new Node();

        if (d == key.length()) {
            x.val = key;
            x.path = key;
            return x;
        }
        char c = key.charAt(d);
        if (x.path == null)
            x.path = key.substring(0, d);
        x.next[Character.getNumericValue(c) - 10] = put(x.next[Character.getNumericValue(c) - 10], key, d + 1);
        return x;
    }

    public String get(String key) {
        // if (prev != null && key.equals(prev.path))
        // return prev.val;
        Node x = get(root, key, 0);
        if (x == null)
            return null;
        else
            return x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null)
            return null;
        else if (d == key.length())
            return x;

        char c = key.charAt(d);
        return get(x.next[Character.getNumericValue(c) - 10], key, d + 1);
    }

    private Node get(Node x, String key, int d, char ch) {
        if (x == null)
            return null;
        if (d < key.length())
            return get(x.next[Character.getNumericValue(key.charAt(d)) - 10], key, d + 1, ch);
        else if (d == key.length()) {
            if (ch == 'Q')
                return get(x.next[Character.getNumericValue(ch) - 10], key, d, 'U');
            else
                return get(x.next[Character.getNumericValue(ch) - 10], key, d + 1, ch);
        } else
            return x;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    private Node cached() {
        if (path.isEmpty())
            return null;
        else
            return path.peek();
    }

    public String prefix(String old, char c) {
        Node x;
        if (path.isEmpty())
            x = get(root, old, 0, c);
        else
            x = get(path.peek(), old, old.length(), c);
        // if (x == null)
        // x = get(root, old, 0, c);
        // else

        if (x == null)
            return null;
        else {
            path.push(x);
            // prev = x;
            return x.path;
        }

    }

    public void remove() {
        // System.out.println(path.size());
        // assert path.size() > 0;
        path.pop();
    }

    public void reset() {
        while (path.size() > 1)
            path.pop();
    }

    public Iterable<String> keys() {
        Queue<String> queue = new Queue<String>();
        collect(root, "", queue);
        return queue;
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> q = new Queue<>();
        Node x = get(root, prefix, 0);
        collect(x, prefix, q);
        return q;
    }

    private void collect(Node x, String prefix, Queue<String> q) {
        if (x == null)
            return;
        if (x.val != null)
            q.enqueue(prefix);
        for (char c = 0; c < R; c++)
            collect(x.next[c], prefix + c, q);
    }
}
