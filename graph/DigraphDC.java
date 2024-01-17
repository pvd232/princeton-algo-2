package graph;

import edu.princeton.cs.algs4.Digraph;

public class DigraphDC {
    public static boolean hasCycle(Digraph G) {
        boolean[] onStack = new boolean[G.V()];
        boolean[] marked = new boolean[G.V()];
        for (int v = 0; v < G.V(); v++)
            if (!marked[v] && dfs(G, v, onStack, marked))
                return true;
        return false;
    }

    // run DFS and find a directed cycle (if one exists)
    private static boolean dfs(Digraph G, int v, boolean[] onStack, boolean[] marked) {
        onStack[v] = true;
        marked[v] = true;
        for (int w : G.adj(v)) {
            if (!marked[w] && dfs(G, w, onStack, marked)) // Found new vertex, so recur
                return true;
            else if (onStack[w])
                return true;
        }
        onStack[v] = false;
        return false;
    }
}