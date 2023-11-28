package mfmc;

public class MyFlowEdge {
    // Create a flow edge v→w
    private final int v, w;
    private final double capacity;
    private double flow;

    MyFlowEdge(int v, int w, double capacity) {
        this.v = v;
        this.w = w;
        this.capacity = capacity;
    }

    // Vertex this edge points from
    int from() {
        return v;
    }

    // Vertex this edge points to
    int to() {
        return w;
    }

    // Other endpoint
    int other(int vertex) {
        if (vertex == v)
            return w;
        else if (vertex == w)
            return v;
        else
            throw new RuntimeException("Illegal endpoint");
    }

    // Capacity of this edge
    double capacity() {
        return capacity;
    }

    // Flow in this edge
    double flow() {
        return flow;
    }

    // Residual capacity toward vertex
    double residualCapacityTo(int vertex) {
        if (vertex == v) // Backward edge
            return flow;
        else if (vertex == w) // Forward edge
            return capacity - flow;
        else
            throw new IllegalArgumentException();
    }

    // Residual flow toward vertex
    void addResidualFlowTo(int vertex, double delta) {
        if (vertex == v)
            flow -= delta; // If adding residual flow to backward edge, reduce forward flow v→w
        else if (vertex == w)
            flow += delta; // Otherwise increase flow of forward edge
    }
}