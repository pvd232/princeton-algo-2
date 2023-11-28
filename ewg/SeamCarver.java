package ewg;

import edu.princeton.cs.algs4.Picture;

import java.util.ArrayList;

public class SeamCarver {
    private Picture pic;
    private final int[][] colors;

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        pic = new Picture(picture);
        colors = new int[height()][width()];
    }

    // Current picture
    public Picture picture() {
        return new Picture(pic);
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
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IllegalArgumentException();

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1)
            return 1000;

        double xGrad = gradientSq(x + 1, y, x - 1, y), yGrad = gradientSq(x, y + 1, x, y - 1);
        return Math.sqrt(xGrad + yGrad);
    }

    private double gradientSq(int x1, int y1, int x2, int y2) {
        int c1 = colors[y1][x1], c2 = colors[y2][x2];
        if (c1 == 0) { // If no cached value, update
            c1 = pic.getRGB(x1, y1);
            colors[y1][x1] = c1;
        }
        if (c2 == 0) {
            c2 = pic.getRGB(x2, y2);
            colors[y2][x2] = c2;
        }
        // bit shift to extract RGB values from binary encoding of the int RGB value
        int r1 = (c1 >> 16) & 0xff, g1 = (c1 >> 8) & 0xff, b1 = c1 & 0xff;
        int r2 = (c2 >> 16) & 0xff, g2 = (c2 >> 8) & 0xff, b2 = c2 & 0xff;
        double rD = Math.pow(r1 - r2, 2), bD = Math.pow(b1 - b2, 2), gD = Math.pow(g1 - g2, 2);
        return rD + gD + bD;
    }

    // Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return sp(false);
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return sp(true);
    }

    private int[] pathTo(int v, int[] edgeTo, int bound, boolean vertical) {
        int[] res = new int[bound];
        int e = edgeTo[v];
        for (int i = 0; i < bound; i++) { // reverse direction return by edgeTo (built from v->w)
            if (vertical)
                res[bound - i - 1] = e % width(); // col val
            else
                res[bound - i - 1] = e / width(); // row val
            e = edgeTo[e];
        }
        return res;
    }

    private int[] sp(boolean vertical) {
        int[] edgeTo = new int[height() * width() + 1];
        double[] distTo = new double[height() * width() + 1];
        if (vertical) {
            for (int k = 0; k < width(); k++) { // set starting row
                distTo[k] = 1000;
                edgeTo[k] = k;
            }

            for (int i = 0; i < height(); i++)
                for (int j = 0; j < width(); j++)
                    if (i == height() - 1) // vBottom
                        relax(j, i, 0, i + 1, distTo, edgeTo);
                    else
                        for (int r : adj(j, i, vertical))
                            relax(j, i, r, i + 1, distTo, edgeTo);

            return pathTo(height() * width(), edgeTo, height(), vertical);
        } else {
            for (int k = 0; k < height(); k++) {
                distTo[k * width()] = 1000;
                edgeTo[k * width()] = k * width();
            }

            for (int i = 0; i < width(); i++)
                for (int j = 0; j < height(); j++)
                    if (i == width() - 1) // vBottom
                        relax(i, j, 0, height(), distTo, edgeTo);
                    else
                        for (int r : adj(i, j, vertical))
                            relax(i, j, i + 1, r, distTo, edgeTo);

            return pathTo(height() * width(), edgeTo, width(), vertical);
        }
    }

    private void relax(int x1, int y1, int x2, int y2, double[] distTo, int[] edgeTo) {
        double weight;
        if (x1 == width() - 1 || y1 == height() - 1) // x2 & y2 vBottom
            weight = 1000;
        else
            weight = energy(x2, y2);
        int v = y1 * width() + x1, w = y2 * width() + x2;
        if (distTo[w] == 0 || distTo[w] > distTo[v] + weight) { // if dist not set, or smaller, update
            distTo[w] = distTo[v] + weight;
            edgeTo[w] = v;
        }
    }

    private Iterable<Integer> adj(int x, int y, boolean vertical) {
        int[] dir = { -1, 0, 1 };
        ArrayList<Integer> res = new ArrayList<>();
        for (int d : dir) {
            int adjX = x, adjY = y;
            if (vertical) {
                adjX = adjX + d;
                adjY = adjY + 1;
            } else {
                adjY = adjY + d;
                adjX = adjX + 1;
            }
            if (adjX >= 0 && adjX < width() && adjY >= 0 && adjY < height())
                if (vertical)
                    res.add(adjX);
                else
                    res.add(adjY);
        }
        return res;
    }

    private boolean validateSeam(int[] seam, boolean vertical) {
        if (seam == null)
            return false;
        if (vertical && (width() == 1 || seam.length != height()))
            return false;
        if (!vertical && (height() == 1 || seam.length != width()))
            return false;

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0)
                return false;
            if (vertical && seam[i] >= width())
                return false;
            else if (!vertical && seam[i] >= height())
                return false;
            if (seam.length - i > 1) { // if not the last row / col, eval adj
                int cmp = seam[i + 1] - seam[i];
                if (cmp > 1 || cmp < -1)
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
        for (int i = 0; i < newPic.width(); i++)
            for (int j = 0; j < newPic.height(); j++) {
                if (j < seam[i])
                    newPic.set(i, j, pic.get(i, j));
                else { // once the removed seam is reached, shift array
                    newPic.set(i, j, pic.get(i, j + 1));
                    colors[j][i] = colors[j + 1][i];
                }
            }
        pic = newPic;
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!validateSeam(seam, true))
            throw new IllegalArgumentException();

        Picture newPic = new Picture(width() - 1, height());
        for (int i = 0; i < newPic.height(); i++) {
            for (int j = 0; j < newPic.width(); j++)
                if (j < seam[i])
                    newPic.set(j, i, pic.get(j, i));
                else {
                    newPic.set(j, i, pic.get(j + 1, i));
                    colors[i][j] = colors[i][j + 1];
                }
        }
        pic = newPic;
    }

    // Unit testing (optional)
    public static void main(String[] args) {
        // SeamCarver testSC = new SeamCarver(new Picture(args[0]));
        // for (int i = 0; i < 50; i++) {
        // int[] rmSeam = testSC.findVerticalSeam();
        // Picture overlaid = SCUtility.seamOverlay(testSC.picture(), false, rmSeam);
        // testSC.removeVerticalSeam(rmSeam);
        // overlaid.show();
        // }

        // for (int i = 0; i < 50; i++) {
        // int[] rmSeam = testSC.findHorizontalSeam();
        // Picture overlaid = SCUtility.seamOverlay(testSC.picture(), true, rmSeam);
        // testSC.removeHorizontalSeam(rmSeam);
        // overlaid.show();
        // }

        // long startTime = System.currentTimeMillis();
        // int w = 1000;
        // int h = 0;
        // for (int i = 0; i < 5; i++) {
        // h = (int) Math.pow(10, i);
        // SeamCarver testSC = new SeamCarver(SCUtility.randomPicture(w, h));
        // testSC.removeVerticalSeam(testSC.findVerticalSeam());
        // long endTime = System.currentTimeMillis();
        // long timeElapsed = endTime - startTime;
        // System.out.println("w: " + w + " h: " + h + " Time elapsed: " + timeElapsed);
        // }
        // System.out.println();
        // startTime = System.currentTimeMillis();
        // h = 1000;
        // for (int i = 0; i < 5; i++) {
        // w = (int) Math.pow(10, i);
        // SeamCarver testSC = new SeamCarver(SCUtility.randomPicture(w, h));
        // testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
        // long endTime = System.currentTimeMillis();
        // long timeElapsed = endTime - startTime;
        // System.out.println("w: " + w + " h: " + h + " Time elapsed: " + timeElapsed);
        // }
    }
}