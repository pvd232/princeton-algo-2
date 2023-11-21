package graph;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdPicture;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import java.awt.Color;
import java.util.HashSet;

public class SeamCarver {
    private Picture pic;
    private final int len;
    private final EdgeWeightedDigraph vEWG;
    private final EdgeWeightedDigraph hEWG;
    private final AcyclicSP vSP;
    private final AcyclicSP hSP;

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        len = width() * height();
        vEWG = new EdgeWeightedDigraph(height() * width() + 1);
        hEWG = new EdgeWeightedDigraph(height() * width() + 1);
        createDAG(true, vEWG);
        createDAG(false, hEWG);
        vSP = new AcyclicSP(vEWG, 0);
        hSP = new AcyclicSP(hEWG, 0);
    }

    private void createDAG(boolean vertical, EdgeWeightedDigraph dag) {
        // Populate digraph
        for (int i = 0; i < height(); i++)
            for (int j = 0; j < width(); j++)
                connPixels(j, i, vertical, dag);

        // Connect virtual top and virtual bottom
        if (vertical) {
            for (int i = 1; i < width(); i++) // Conn vTop to row 0
                dag.addEdge(new DirectedEdge(0, i, 1000));
            for (int i = (height() - 1) * width(); i < len; i++) // Conn vBottom to row height - 1
                dag.addEdge(new DirectedEdge(i, len, 1000));
        } else {
            for (int i = 1; i < height(); i++) // Conn vTop to col 0
                dag.addEdge(new DirectedEdge(0, i * width(), 1000));
            for (int i = 1; i < height(); i++) // Conn vBottom to col width - 1
                dag.addEdge(new DirectedEdge(i * width() - 1, len, 1000));
        }

    }

    private void connPixels(int x, int y, boolean vertical, EdgeWeightedDigraph dag) {
        // Skip virtual top and bottom
        if (x == 0 && y == 0 || x == width() - 1 && y == height() - 1)
            return;
        int[] dir = new int[] { -1, 0, 1 };
        for (int d : dir) {
            int adjX = x;
            int adjY = y;
            if (vertical) {
                adjX = adjX + d;
                adjY = adjY + 1;
            } else {
                adjY = adjY + d;
                adjX = adjX + 1;
            }
            if (adjX >= 0 && adjX < width() && adjY >= 0 && adjY < height())
                dag.addEdge(new DirectedEdge(x + y * width(), adjX + adjY * width(), energy(adjX, adjY)));
        }
    }

    // Current picture
    public Picture picture() {
        return pic;
    }

    // Width of current picture
    public int width() {
        return pic.width();
    }

    // Height of current picture
    public int height() {
        return pic.height();
    }

    // Energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1)
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

    // Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Iterable<DirectedEdge> pathTo = hSP.pathTo(len);
        int[] res = new int[width()];
        int resCount = 0;
        for (DirectedEdge e : pathTo) {
            if (e.from() != 0)
                res[resCount++] = e.from();
        }
        return res;
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        Iterable<DirectedEdge> pathTo = vSP.pathTo(len);
        int[] res = new int[height()];
        int resCount = 0;
        for (DirectedEdge e : pathTo) {
            if (e.from() != 0)
                res[resCount++] = e.from();
        }
        return res;
    }

    // Remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        HashSet<Integer> seamPix = new HashSet<>(seam.length);
        for (int i = 0; i < seam.length; i++)
            seamPix.add(seam[i]);

        Picture newPic = new Picture(width(), height() - 1);
        for (int i = 0; i < newPic.height(); i++)
            for (int j = 0; j < newPic.width(); j++)
                if (!seamPix.contains(i * width() + j))
                    newPic.set(j, i, pic.get(j, i));
                else
                    newPic.set(j, i, pic.get(j, i + 1));
        pic = newPic;
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        HashSet<Integer> seamPix = new HashSet<>(seam.length);
        for (int i = 0; i < seam.length; i++)
            seamPix.add(seam[i]);

        Picture newPic = new Picture(width() - 1, height());
        for (int i = 0; i < newPic.height(); i++)
            for (int j = 0; j < newPic.width(); j++)
                if (!seamPix.contains(i * width() + j))
                    newPic.set(j, i, pic.get(j, i));
                else
                    newPic.set(j, i, pic.get(j + 1, i));
        pic = newPic;
    }

    // Unit testing (optional)
    public static void main(String[] args) {
        Picture testPic = new Picture(args[0]);
        StdPicture.read(args[0]);
        StdPicture.show();

        SeamCarver testSC = new SeamCarver(testPic);

        // Remove 50 horizontal layers
        for (int x = 0; x < 50; x++) {
            testSC.removeHorizontalSeam(testSC.findHorizontalSeam());

            Picture newPic = testSC.picture();
            for (int i = 0; i < StdPicture.height(); i++)
                for (int j = 0; j < StdPicture.width(); j++) {
                    if (i < newPic.height() && j < newPic.width()) {
                        Color col = newPic.get(j, i);
                        int r = StdPicture.getRed(j, i), g = StdPicture.getGreen(j, i), b = StdPicture.getBlue(j, i);
                        if (col.getRed() != r || col.getGreen() != g || col.getBlue() != b)
                            StdPicture.setRGB(j, i, 255, 0, 0);
                    } else {
                        int r = StdPicture.getRed(j, i), g = StdPicture.getGreen(j, i), b = StdPicture.getBlue(j, i);
                        StdPicture.setRGB(j, i, r, g, b);
                    }
                }
            StdPicture.show();
            testSC = new SeamCarver(newPic);
        }

        // Remove 50 vertical layers
        for (int x = 0; x < 50; x++) {
            testSC.removeVerticalSeam(testSC.findVerticalSeam());
            Picture newPic = testSC.picture();

            for (int i = 0; i < StdPicture.height(); i++)
                for (int j = 0; j < StdPicture.width(); j++) {
                    if (i < newPic.height() && j < newPic.width()) {
                        Color col = newPic.get(j, i);
                        int r = StdPicture.getRed(j, i), g = StdPicture.getGreen(j, i), b = StdPicture.getBlue(j, i);
                        if (col.getRed() != r || col.getGreen() != g || col.getBlue() != b)
                            StdPicture.setRGB(j, i, 255, 0, 0);
                    } else {
                        int r = StdPicture.getRed(j, i), g = StdPicture.getGreen(j, i), b = StdPicture.getBlue(j, i);
                        StdPicture.setRGB(j, i, r, g, b);
                    }
                }
            StdPicture.show();
            testSC = new SeamCarver(testSC.picture());
        }

        // Redraw
        StdPicture.init(testSC.picture().width(), testSC.picture().height());
        for (int i = 0; i < testSC.picture().height(); i++)
            for (int j = 0; j < testSC.picture().width(); j++) {
                Color col = testSC.picture().get(j, i);
                StdPicture.setRGB(j, i, col.getRed(), col.getGreen(), col.getBlue());
            }
        StdPicture.show();
    }
}