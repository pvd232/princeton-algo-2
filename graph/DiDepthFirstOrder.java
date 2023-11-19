package graph;

import java.util.Stack;
import edu.princeton.cs.algs4.Digraph;

public class DiDepthFirstOrder {
    private boolean[] marked;
    final private Stack<Integer> reversePost;

    public DiDepthFirstOrder(Digraph g) {
        reversePost = new Stack<Integer>();
        marked = new boolean[g.V()];
        for (int v = 0; v < g.V(); v++)
            if (!marked[v])
                dfs(g, v);
    }

    private void dfs(Digraph g, int v) {
        marked[v] = true;
        for (int w : g.adj(v))
            if (!marked[w])
                dfs(g, w);
        reversePost.push(v);
    }

    public Iterable<Integer> reversePost() {
        return reversePost;
    }
}