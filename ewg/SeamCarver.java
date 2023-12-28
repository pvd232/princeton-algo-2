package ewg;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private static final int[] DIR = { -1, 0, 1 };
    private final int[][] colors;
    private double[][] energies;
    private int pHeight;
    private int pWidth;
    private int xStart = 0;
    private int yStart = 0;

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        Picture pic = new Picture(picture);
        pHeight = pic.height();
        pWidth = pic.width();
        colors = new int[pHeight][pWidth];
        energies = new double[pHeight][pWidth];
        for (int i = 0; i < pHeight + 2; i++)
            for (int j = 0; j < pWidth; j++) {
                if (i < pHeight)
                    colors[i][j] = pic.getRGB(j, i);
                if (i >= 2)
                    energies[i - 2][j] = energy(j, i - 2);
            }
    }

    // Current picture
    public Picture picture() {
        Picture newPic = new Picture(width(), height());
        for (int i = yStart; i < pHeight; i++)
            for (int j = xStart; j < pWidth; j++)
                newPic.setRGB(j - xStart, i - yStart, colors[i][j]);
        return newPic;
    }

    // Width of current picture
    public int width() {
        return pWidth - xStart;
    }

    // Height of current picture
    public int height() {
        return pHeight - yStart;
    }

    public double energy(int x, int y) {
        return energy(x, y, false);
    }

    // Energy of pixel at column x and row y
    private double energy(int x, int y, boolean useCache) {
        if (x < xStart || x >= pWidth || y < yStart || y >= pHeight)
            throw new IllegalArgumentException();
        else if (x == xStart || x == pWidth - 1 || y == yStart || y == pHeight - 1)
            return 1000;

        if (!useCache) {
            double xGrad = gradientSq(x + 1, y, x - 1, y), yGrad = gradientSq(x, y + 1, x, y - 1);
            double grad = Math.sqrt(xGrad + yGrad);
            energies[y][x] = grad;
            return grad;
        } else
            return energies[y][x];
    }

    private double gradientSq(int x1, int y1, int x2, int y2) {
        int c1 = colors[y1][x1], c2 = colors[y2][x2];
        // bit shift to extract RGB values from binary encoding of the int RGB value
        int r1 = (c1 >> 16) & 0xff, g1 = (c1 >> 8) & 0xff, b1 = c1 & 0xff;
        int r2 = (c2 >> 16) & 0xff, g2 = (c2 >> 8) & 0xff, b2 = c2 & 0xff;
        double rD = Math.pow(r1 - r2, 2), bD = Math.pow(b1 - b2, 2), gD = Math.pow(g1 - g2, 2);
        return rD + gD + bD;
    }

    // Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return sp(pWidth, pHeight, xStart, yStart, false);
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return sp(pHeight, pWidth, yStart, xStart, true);
    }

    private int[] pathTo(int x, int y, int e, int[][] edgeTo, int bound, int bStart, boolean vertical) {
        int[] res = new int[bound - bStart];
        for (int i = bStart; i < bound; i++) { // reverse direction return by edgeTo (built from v->w)
            res[bound - bStart - i - 1] = e;
            if (vertical)
                e = edgeTo[y--][e]; // edgeTo will be col val, decrement y
            else
                e = edgeTo[e][x--]; // edgeTo will be row val, decrement x
        }
        return res;
    }

    private int[] sp(int bound, int other, int bStart, int oStart, boolean vertical) {
        int[][] edgeTo = new int[pHeight + 1][pWidth];
        double[][] distTo = new double[pHeight + 1][pWidth];
        for (int i = oStart; i < other; i++)
            if (vertical) {
                edgeTo[yStart][i] = i;
                distTo[yStart][i] = 1000;
            } else {
                edgeTo[i][xStart] = i;
                distTo[i][xStart] = 1000;
            }
        for (int i = bStart; i < bound; i++)
            for (int j = 1 + oStart; j < other - 1; j++)
                if (i == bound - 1) // vBottom
                    if (vertical)
                        relax(j, i, xStart, pHeight, distTo, edgeTo, vertical);
                    else
                        relax(i, j, xStart, pHeight, distTo, edgeTo, vertical);
                else if (vertical)
                    for (int r : adj(j, i, vertical))
                        relax(j, i, r, i + 1, distTo, edgeTo, vertical);
                else
                    for (int r : adj(i, j, vertical))
                        relax(i, j, i + 1, r, distTo, edgeTo, vertical);
        return pathTo(pWidth - 1, pHeight - 1, edgeTo[pHeight][xStart], edgeTo, bound, bStart, vertical);
    }

    private void relax(int x1, int y1, int x2, int y2, double[][] distTo, int[][] edgeTo,
            boolean vertical) {
        double weight = 1000; // Edge pixel
        if (x2 > xStart && x2 < pWidth - 1 && y2 > yStart && y2 < pHeight - 1) // If x2 & y2 not edge pixel
            weight = energy(x2, y2, true);
        if (distTo[y2][x2] == 0 || distTo[y2][x2] > distTo[y1][x1] + weight) { // If dist not set, or smaller, update
            distTo[y2][x2] = distTo[y1][x1] + weight;
            if (vertical)
                edgeTo[y2][x2] = x1;
            else
                edgeTo[y2][x2] = y1;
        }
    }

    private int[] adj(int x, int y, boolean vertical) {
        int[] res = new int[3];
        int k = 0;
        for (int d : DIR) {
            int adjX = x + d, adjY = y + d;
            if (vertical && adjX >= xStart && adjX < pWidth)
                res[k++] = adjX;
            else if (!vertical && adjY >= yStart && adjY < pHeight)
                res[k++] = adjY;
        }
        return res;
    }

    private boolean validateSeam(int[] seam, boolean vertical) {
        if (seam == null)
            return false;
        if (vertical && (pWidth == 1 || seam.length != pHeight))
            return false;
        if (!vertical && (pHeight == 1 || seam.length != pWidth))
            return false;

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0)
                return false;
            if (vertical && seam[i] >= pWidth)
                return false;
            if (!vertical && seam[i] >= pHeight)
                return false;
            if (seam.length - i > 1) { // If not the last row / col, eval adj
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
        // pHeight = pHeight - 1;
        boolean rtl = true;
        if (seam[0] < pHeight / 2) {
            rtl = false;
            yStart++;
        } else
            pHeight--;
        for (int i = xStart; i < pWidth; i++) {
            if (rtl)
                for (int j = seam[i]; j < pHeight; j++)
                    colors[j][i] = colors[j + 1][i];
            else
                for (int j = seam[i]; j >= yStart; j--)
                    colors[j][i] = colors[j - 1][i];
        }
        for (int i = xStart; i < pWidth; i++)
            if (rtl) {
                for (int j = seam[i] - 1; j < pHeight - 1; j++)
                    if (j >= xStart)
                        energies[j][i] = energy(i, j, false);
            } else
                for (int j = seam[i] + 1; j > yStart - 1; j--)
                    if (j < pHeight)
                        energies[j][i] = energy(i, j, false);
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!validateSeam(seam, true))
            throw new IllegalArgumentException();
        pWidth = pWidth - 1;
        for (int i = 0; i < pHeight; i++)
            for (int j = seam[i]; j < pWidth; j++)
                colors[i][j] = colors[i][j + 1];

        for (int i = 0; i < pHeight; i++) {
            for (int j = seam[i] - 1; j < pWidth - 1; j++)
                if (j >= 0)
                    energies[i][j] = energy(j, i, false);
        }
    }

    // Unit testing (optional)
    public static void main(String[] args) {
        // Running time tests
        // int height = 1000;
        // for (int i = 0; i < 5; i++) {
        // height = height * 2;
        // Picture testPicture = SCUtility.randomPicture(2000, height);
        // SeamCarver testSC = new SeamCarver(testPicture);

        // long startTime = System.currentTimeMillis();
        // testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
        // testSC.removeVerticalSeam(testSC.findVerticalSeam());
        // long endTime = System.currentTimeMillis() - startTime;
        // System.out.println("H trial " + i + " time: " + endTime);
        // }
        // int width = 1000;
        // for (int i = 0; i < 6; i++) {
        // width = width * 2;
        // Picture testPicture = SCUtility.randomPicture(width, 2000);
        // SeamCarver testSC = new SeamCarver(testPicture);

        // long startTime = System.currentTimeMillis();
        // testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
        // testSC.removeVerticalSeam(testSC.findVerticalSeam());
        // long endTime = System.currentTimeMillis() - startTime;
        // System.out.println("W trial " + i + " time: " + endTime);
        // }
        SeamCarver timed = new SeamCarver(SCUtility.randomPicture(736, 584));
        int count = 0;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1500) {
            // timed.removeHorizontalSeam(timed.findHorizontalSeam());
            timed.removeVerticalSeam(timed.findVerticalSeam());
            timed.removeVerticalSeam(timed.findVerticalSeam());
            count++;
        }
        System.out.println("Runs per second: " + (count * 2) / 3);
        SeamCarver testSC = new SeamCarver(new Picture(args[0]));
        // SeamCarver testSC = new SeamCarver(SCUtility.randomPicture(500, 500));

        // Display algorithms functionality
        // for (int i = 0; i < 50; i++) {
        // int[] rmSeam = testSC.findVerticalSeam();
        // Picture overlaid = SCUtility.seamOverlay(testSC.picture(), false, rmSeam);
        // testSC.removeVerticalSeam(rmSeam);
        // overlaid.show();
        // }

        for (int i = 0; i < 100; i++) {
            int[] rmSeam = testSC.findHorizontalSeam();
            int[] otherRmSeam = new int[rmSeam.length];
            for (int u = 0; u < rmSeam.length; u++)
                otherRmSeam[u] = rmSeam[u] - testSC.yStart;
            Picture overlaid = SCUtility.seamOverlay(testSC.picture(), true, otherRmSeam);
            testSC.removeHorizontalSeam(rmSeam);
            overlaid.show();
        }
    }
}