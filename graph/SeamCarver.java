package graph;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import java.awt.Color;

// The Plan
// Create EW DAG using energy as weight for vertices 
// 
public class SeamCarver {
    Picture pic;
    EdgeWeightedDigraph eg;
    AcyclicSP sp;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        eg = new EdgeWeightedDigraph(pic.height() * pic.width() + 1);
        createDAG();
        sp = new AcyclicSP(eg, 0);
    }

    private void createDAG() {
        // connect virtual top (v=0) to all pixels in row 0
        for (int i = 1; i < pic.width(); i++)
            eg.addEdge(new DirectedEdge(0, i, 1000));

        // populate digraph
        for (int i = 0; i < pic.height(); i++)
            for (int j = 0; j < pic.width(); j++)
                connPixels(i, j, true);
        // connect virtual bottom (v=width * height - 1) to all pixels in row -1
        for (int i = (pic.height() - 1) * pic.width(); i < pic.height() * pic.width(); i++)
            eg.addEdge(new DirectedEdge(i, pic.height() * pic.width(), 1000));
    }

    private void connPixels(int x, int y, boolean vertical) {
        // skip virtual top and bottom
        if (x == 0 && y == 0 || x == pic.width() - 1 && y == pic.height() - 1)
            return;
        int[] dir = new int[] { -1, 0, 1 };
        for (int dx : dir) {
            int adjX = dx + x;
            int adjY = y + 1;
            if (vertical)
                if (adjX >= 0 && adjX < pic.width() && adjY < pic.height())
                    eg.addEdge(new DirectedEdge(x + y * pic.width(), adjX + adjY * pic.width(), energy(adjX, adjY)));
            // else
        }
    }

    // current picture
    public Picture picture() {
        return pic;
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x == 0 || x == pic.width() - 1 || y == 0 || y == pic.height() - 1)
            return 1000;
        double xGrad = gradientSq(x + 1, y, x - 1, y);
        double yGrad = gradientSq(x, y + 1, x, y - 1);
        return Math.sqrt(xGrad + yGrad);
    }

    private double gradientSq(int x1, int y1, int x2, int y2) {
        Color c1 = pic.get(x1, y1);
        Color c2 = pic.get(x2, y2);

        double rD = Math.pow(c1.getRed() - c2.getRed(), 2), bD = Math.pow(c1.getBlue() - c2.getBlue(), 2),
                gD = Math.pow(c1.getGreen() - c2.getGreen(), 2);
        return rD + gD + bD;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return new int[0];
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        Iterable<DirectedEdge> pathTo = sp.pathTo(pic.height() * pic.width());
        int[] res = new int[pic.height()];
        int resCount = 0;
        for (DirectedEdge e : pathTo) {
            if (e.from() != 0)
                res[resCount++] = e.from();
        }
        return res;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

    }

    // unit testing (optional)
    public static void main(String[] args) {
        Picture testPic = new Picture(args[0]);
        SeamCarver testSC = new SeamCarver(testPic);
        for (int pix : testSC.findVerticalSeam()) {
            System.out.println(pix);
        }
    }

}