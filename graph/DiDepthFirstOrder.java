package graph;

import java.util.Stack;

public class DiDepthFirstOrder {
    private boolean[] marked;
    private Stack<Integer> reversePost;

    public DiDepthFirstOrder(Digraph g) {
        reversePost = new Stack<Integer>();
        marked = new boolean[g.vertices()];
        for (int v = 0; v < g.vertices(); v++)
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