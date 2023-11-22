package graph;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdPicture;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import java.awt.Color;
import java.util.ArrayList;

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
        if (x < 0 || x >= pic.width() || y < 0 || y >= pic.height())
            throw new IllegalArgumentException();
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

    private boolean validateSeam(int[] seam, boolean vertical) {
        if (seam == null)
            return false;
        if (vertical && (height() == 1 || seam.length != pic.height()))
            return false;

        if (!vertical && (width() == 1 || seam.length != pic.width()))
            return false;

        for (int i = 0; i < seam.length - 1; i++) {
            int j = i + 1;
            int res = seam[j] - seam[i];
            if (vertical) {
                if (res != width() && res != width() + 1 && res != width() - 1)
                    return false;
                if (!(seam[i] >= width() * i && seam[i] <= width() * (i + 1)))
                    return false;
            }

            if (!vertical) {
                if (res != 1 - width() && res != 1 && res != width() + 1)
                    return false;
                if (!(seam[i] % width() == i))
                    return false;
            }
        }
        return true;
    }

    // Remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!validateSeam(seam, false))
            throw new IllegalArgumentException();

        Picture newPic = new Picture(width(), height() - 1);
        for (int i = 0; i < newPic.height(); i++) {
            int py = seam[i] / width(), px = seam[i] % width();
            for (int j = 0; j < newPic.width(); j++)
                if (i < py && j < px)
                    newPic.set(j, i, pic.get(j, i));
                else if (i == py && j == px)
                    newPic.set(j, i, pic.get(j, i + 1));
                else
                    newPic.set(j, i, pic.get(j, i));

        }
        pic = newPic;
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!validateSeam(seam, true))
            throw new IllegalArgumentException();

        Picture newPic = new Picture(width() - 1, height());
        for (int i = 0; i < newPic.height(); i++) {
            int py = seam[i] / width(), px = seam[i] % width();
            System.out.println(py);
            for (int j = 0; j < newPic.width(); j++)
                if (i < py && j < px)
                    newPic.set(j, i, pic.get(j, i));
                else if (i == py && j == px)
                    newPic.set(j, i, pic.get(j + 1, i));
                else
                    newPic.set(j, i, pic.get(j, i));
        }
        pic = newPic;
    }

    // Unit testing (optional)
    public static void main(String[] args) {
        Picture testPic = new Picture(args[0]);
        SeamCarver testSC = new SeamCarver(testPic);
        ArrayList<int[]> rmved = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int[] rmSeam = testSC.findHorizontalSeam();
            rmved.add(rmSeam);
            testSC.removeHorizontalSeam(rmSeam);
            testSC = new SeamCarver(testSC.picture());
        }
        // StdPicture.init(testSC.picture().width(), testSC.picture().height());
        // for (int i = 0; i < testSC.picture().height(); i++)
        // for (int j = 0; j < testSC.picture().width(); j++)
        // StdPicture.setRGB(j, i, testSC.picture().get(j, i).getRed(),
        // testSC.picture().get(j, i).getGreen(),
        // testSC.picture().get(j, i).getBlue());
        // StdPicture.show();
        for (int i = 0; i < 5; i++) {
            int[] rmSeam = testSC.findVerticalSeam();
            rmved.add(rmSeam);
            testSC.removeVerticalSeam(rmSeam);
            testSC = new SeamCarver(testSC.picture());
        }

        StdPicture.read(args[0]);
        StdPicture.show();
        for (int i = 0; i < rmved.size(); i++)
            for (int j = 0; j < rmved.get(i).length; j++)
                StdPicture.setRGB(rmved.get(i)[j] % testPic.width(), rmved.get(i)[j] /
                        testPic.width(), 255, 0, 0);
        // StdPicture.show();
        // testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
        // testSC.removeVerticalSeam(testSC.findVerticalSeam());
        // for (int a : testSC.findVerticalSeam()) {
        // System.out.println(a);
        // }
        // System.out.println();
        // for (int a : testSC.findHorizontalSeam()) {
        // System.out.println(a);
        // }
        // // Remove horizontal layers

        // StdPicture.init(testSC.picture().width(), testSC.picture().height());
        // for (int i = 0; i < testSC.picture().height(); i++)
        // for (int j = 0; j < testSC.picture().height(); j++)
        // StdPicture.setRGB(j, i, testSC.picture().get(j, i).getRed(),
        // testSC.picture().get(j, i).getBlue(),
        // testSC.picture().get(j, i).getBlue());
        // StdPicture.show();
    }
}