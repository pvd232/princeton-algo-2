// package string;

// import java.util.ArrayList;

// public class TrieST {
// private static final int R = 26;
// private Node root = new Node();
// private Node prev = new Node();
// private int cnt = 0;

// private static class Node {
// private String val;
// private String path;
// private boolean isParent;
// private int id = -1;
// private final Node[] next = new Node[R];
// }

// public void put(String key) {
// root = put(root, key, 0, key.length());
// }

// private Node put(Node x, String key, int d, int n) {
// if (x == null)
// x = new Node();
// if (d == n) {
// if (n > 2)
// x.val = key;
// x.path = key;
// return x;
// }
// int c = key.charAt(d) - 65;
// if (x.path == null)
// x.path = key.substring(0, d);
// x.isParent = true;
// x.next[c] = put(x.next[c], key, d + 1, n);
// return x;
// }

// private Node get(Node x, String key, int d, char ch, int n) {
// if (x == null)
// return null;
// else if (d < n)
// return get(x.next[key.charAt(d) - 65], key, d + 1, ch, n);
// else if (d == n)
// if (ch == 'Q')
// return get(x.next[ch - 65], key, d, 'U', n);
// else
// return get(x.next[ch - 65], key, d + 1, ch, n);
// return x;
// }

// // Returns the Node for a given key
// private Node get(Node x, String key, int d, int n) {
// if (x == null || d == n)
// return x;
// return get(x.next[key.charAt(d) - 65], key, d + 1, n);
// }

// private String get(String key) {
// Node x = get(root, key, 0, key.length());
// if (x == null)
// return null;
// return x.val;
// }

// public boolean contains(String key) {
// return get(key) != null;
// }

// public String prefix(String old, char c) {
// Node x;
// int n = old.length();
// if (old.equals(prev.path))
// x = get(prev, old, n, c, n);
// else
// x = get(root, old, 0, c, n);
// if (x == null)
// return null;
// else {
// prev = x;
// return x.path;
// }
// }

// // Node will always exist
// public boolean hasKids(String key, ArrayList<String> res) {
// Node x;
// if (key.equals(prev.path))
// x = prev;
// else
// x = get(root, key, 0, key.length());
// if (x.val != null && x.id != cnt) {
// x.id = cnt;
// res.add(key);
// }
// return x.isParent;
// }

// public void setCnt() {
// this.cnt++;
// }
// }