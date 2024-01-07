package string;

import java.util.HashSet;

public class TrieST {
    private static final int R = 26;
    private Node root = new Node();
    private Node prev = new Node();

    private static class Node {
        private String val;
        private String path;
        private boolean isParent;
        private final Node[] next = new Node[R];
    }

    public void put(String key) {
        root = put(root, key, 0);
    }

    private Node put(Node x, String key, int d) {
        if (x == null)
            x = new Node();

        if (d == key.length()) {
            if (key.length() > 2)
                x.val = key;
            x.path = key;
            return x;
        } else
            x.isParent = true;

        char c = key.charAt(d);
        if (x.path == null)
            x.path = key.substring(0, d);

        x.next[Character.getNumericValue(c) - 10] = put(x.next[Character.getNumericValue(c) - 10], key, d + 1);
        return x;
    }

    public String get(String key) {
        if (key.equals(prev.path))
            return prev.val;
        Node x = get(root, key, 0);
        if (x == null)
            return null;
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

    private Node get(Node x, String key, int d, char ch, int n) {
        while (d <= n)
            if (x == null)
                return null;
            else if (d < n)
                x = x.next[Character.getNumericValue(key.charAt(d++)) - 10];
            else if (ch == 'Q') {
                x = x.next[Character.getNumericValue(ch) - 10];
                ch = 'U';
            } else {
                x = x.next[Character.getNumericValue(ch) - 10];
                d++;
            }
        return x;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public String prefix(String old, char c) {
        Node x;
        if (old.equals(prev.path))
            x = get(prev, old, old.length(), c, old.length());
        else
            x = get(root, old, 0, c, old.length());
        if (x == null)
            return null;
        else {
            prev = x;
            return x.path;
        }
    }

    // Node will always exist
    public boolean hasKids(String key, HashSet<String> res) {
        Node x;
        if (key.equals(prev.path))
            x = prev;
        else
            x = get(root, key, 0);
        if (x.val != null)
            res.add(key);
        return x.isParent;
    }
}
