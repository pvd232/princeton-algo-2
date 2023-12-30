import java.util.Arrays;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private static final int[] DIR = { -1, 0, 1 };
    private final int[][] colors;
    private final double[][] energies;
    private int xStart = 0;
    private int yStart = 0;
    private int pWidth;
    private int pHeight;

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
        Picture newPic = new Picture(pWidth, pHeight);
        for (int i = 0; i < pHeight; i++)
            for (int j = 0; j < pWidth; j++)
                newPic.setRGB(j, i, colors[i + yStart][j + xStart]);
        return newPic;
    }

    // Width of current picture
    public int width() {
        return pWidth;
    }

    // Height of current picture
    public int height() {
        return pHeight;
    }
    // Energy of pixel at column x and row y

    public double energy(int x, int y) {
        if (x < 0 || x >= pWidth)
            throw new IllegalArgumentException();
        if (y < 0 || y >= pHeight)
            throw new IllegalArgumentException();
        else if (x == 0 || x == pWidth - 1 || y == 0 || y == pHeight - 1)
            return 1000;

        double xGrad = gradientSq(x + 1, y, x - 1, y), yGrad = gradientSq(x, y + 1, x, y - 1);
        double grad = Math.sqrt(xGrad + yGrad);
        energies[y + yStart][x + xStart] = grad;
        return grad;
    }

    private double gradientSq(int x1, int y1, int x2, int y2) {
        int c1 = colors[y1 + yStart][x1 + xStart], c2 = colors[y2 + yStart][x2 + xStart];
        // bit shift to extract RGB values from binary encoding of the int RGB value
        int r1 = (c1 >> 16) & 0xff, g1 = (c1 >> 8) & 0xff, b1 = c1 & 0xff;
        int r2 = (c2 >> 16) & 0xff, g2 = (c2 >> 8) & 0xff, b2 = c2 & 0xff;
        double rD = Math.pow(r1 - r2, 2), bD = Math.pow(b1 - b2, 2), gD = Math.pow(g1 - g2, 2);
        return rD + gD + bD;
    }

    // Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return sp(pWidth, pHeight, false);
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return sp(pHeight, pWidth, true);
    }

    private int[] sp(int bound, int other, boolean vertical) {
        int[][] edgeTo = new int[pHeight + 1][pWidth];
        double[][] distTo = new double[pHeight + 1][pWidth];
        for (int i = 0; i < other; i++)
            if (vertical) {
                edgeTo[0][i] = i;
                distTo[0][i] = 1000;
            } else {
                edgeTo[i][0] = i;
                distTo[i][0] = 1000;
            }
        for (int i = 0; i < bound; i++)
            for (int j = 1; j < other - 1; j++)
                if (i == bound - 1 && vertical) // vBottom
                    relax(j, i, 0, pHeight, distTo, edgeTo, vertical);
                else if (i == bound - 1 && !vertical)
                    relax(i, j, 0, pHeight, distTo, edgeTo, vertical);
                else if (vertical)
                    for (int r : adj(j, i, vertical))
                        relax(j, i, r, i + 1, distTo, edgeTo, vertical);
                else
                    for (int r : adj(i, j, vertical))
                        relax(i, j, i + 1, r, distTo, edgeTo, vertical);
        return pathTo(pWidth - 1, pHeight - 1, edgeTo[pHeight][0], edgeTo, bound,
                vertical);
    }

    private int[] pathTo(int x, int y, int e, int[][] edgeTo, int bound, boolean vertical) {
        int[] res = new int[bound];
        for (int i = 0; i < bound; i++) { // reverse direction return by edgeTo (built from v->w)
            res[bound - i - 1] = e;
            if (vertical)
                e = edgeTo[y--][e]; // edgeTo will be col val, decrement y
            else
                e = edgeTo[e][x--]; // edgeTo will be row val, decrement x
        }
        return res;
    }

    private void relax(int x1, int y1, int x2, int y2, double[][] distTo, int[][] edgeTo,
            boolean vertical) {
        double weight = 1000; // Edge pixel
        if (x2 > 0 && x2 < pWidth - 1 && y2 > 0 && y2 < pHeight - 1) // If x2 & y2 not edge pixel
            weight = energies[y2 + yStart][x2 + xStart];
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
            if (vertical && adjX >= 0 && adjX < pWidth)
                res[k++] = adjX;
            else if (!vertical && adjY >= 0 && adjY < pHeight)
                res[k++] = adjY;
        }
        return res;
    }

    private boolean validateSeam(int[] seam, boolean vertical) {
        if (seam == null)
            throw new IllegalArgumentException();
        if (vertical && (pWidth == 1 || seam.length != pHeight))
            throw new IllegalArgumentException();
        if (!vertical && (pHeight == 1 || seam.length != pWidth))
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0)
                throw new IllegalArgumentException();
            if (vertical && seam[i] >= pWidth)
                throw new IllegalArgumentException();
            if (!vertical && seam[i] >= pHeight)
                throw new IllegalArgumentException();
            if (seam.length - i > 1) { // If not the last row / col, eval adj
                int cmp = seam[i + 1] - seam[i];
                if (cmp > 1 || cmp < -1)
                    throw new IllegalArgumentException();
            }
        }
        return true;
    }

    // Remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, false);
        boolean bottom = seam[0] > pHeight / 2;
        pHeight--;

        // Remove seam / update pixel colors before updating pixel energies
        for (int i = 0; i < seam.length; i++) {
            if (bottom) // If seam is closer to xMax, update by shifting colors right
                for (int j = seam[i]; j < pHeight; j++)
                    colors[j + yStart][i + xStart] = colors[j + yStart + 1][i + xStart];
            else // Otherwise, update by shifting colors left
                for (int j = seam[i]; j > 0; j--)
                    colors[j + yStart][i + xStart] = colors[j + yStart - 1][i + xStart];
        }

        if (!bottom) // If shifting pixels left, increment origin left
            yStart++;

        // Update pixel energies
        for (int i = 0; i < seam.length; i++)
            if (bottom) // If seam is closer to xMax
                for (int j = Math.max(seam[i] - 1, 0); j < pHeight - 1; j++) // Start LO seam, finish at len - 1
                    if (j > seam[i] + 1) // only seam[i] and seam[i] - 1 need to be recomputed
                        energies[j + yStart][i + xStart] = energies[j + yStart + 1][i + xStart];
                    else
                        energies[j + yStart][i + xStart] = energy(i, j);
            else // Otherwise, increment from seam to yStart
                for (int j = seam[i]; j > 0 && j < pHeight; j--)
                    if (j < seam[i] - 1)
                        energies[j + yStart][i + xStart] = energies[j + yStart - 1][i + xStart];
                    else
                        energies[j + yStart][i + xStart] = energy(i, j);
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, true);
        boolean right = seam[0] > pWidth / 2;
        pWidth--; // Decrement pWidth before removing seam

        // Remove seam
        for (int i = 0; i < seam.length; i++) {
            if (right) // If seam closer to xMax, shift pixels right, traverse to decremented pWidth
                for (int j = seam[i]; j < pWidth; j++)
                    colors[i + yStart][j + xStart] = colors[i + yStart][j + xStart + 1];
            else // Otherwise, update by shifting colors left, with non-incremented start index
                for (int j = seam[i]; j > 0; j--)
                    colors[i + yStart][j + xStart] = colors[i + yStart][j + xStart - 1];
        }

        if (!right) // If shifting pixels left, increment origin left
            xStart++;

        // Update pixel energies
        for (int i = 0; i < seam.length; i++)
            if (right) // If seam is closer to xMax
                for (int j = Math.max(seam[i] - 1, 0); j < pWidth - 1; j++) // Start left of seam
                    if (j > seam[i] + 1)
                        energies[i + yStart][j + xStart] = energies[i + yStart][j + xStart + 1];
                    else
                        energies[i + yStart][j + xStart] = energy(j, i);
            else // Otherwise, increment from seam to yStart
                for (int j = seam[i]; j > 0 && j < pWidth; j--)
                    if (j < seam[i] - 1)
                        energies[i + yStart][j + xStart] = energies[i + yStart][j + xStart - 1];
                    else
                        energies[i + yStart][j + xStart] = energy(j, i);
    }

    // Recursive path traversal - didn't work / informative to underlying mechanics
    private double testFindVerticalSeam() {
        int[][] paths = new int[pWidth][pHeight];
        double[][] distTo = new double[pWidth][pHeight];
        for (int i = 0; i < pWidth; i++) {
            paths[0][i] = i;
            distTo[0][i] = 1000;
        }
        for (int i = 0; i < pWidth; i++) {
            testFindVerticalSeam(i, 0, paths, distTo);
        }
        double[] pathSums = new double[pWidth];
        for (int i = 0; i < paths[0].length; i++) {
            for (int j = 0; j < paths.length; j++) {
                int cnt = 0;
                double sum = 0;
                double sm = energy(paths[i][j], cnt++);
                sum += sm;
                pathSums[i] = sum;
            }
        }
        Arrays.sort(pathSums);
        return pathSums[0];
    }

    private void testFindVerticalSeam(int x, int y, int[][] paths, double[][] distTo) {
        if (y == pHeight - 1)
            return;

        for (int adjX : adj(x, y, true)) {
            double tmp = energy(adjX, y + 1);
            if (distTo[y + 1][adjX] == 0 || distTo[y][x] + tmp < distTo[y + 1][adjX]) {
                distTo[y + 1][adjX] = distTo[y][x] + tmp;
                paths[y + 1][adjX] = x;
            }
            testFindVerticalSeam(adjX, y + 1, paths, distTo);
        }
    }

    private int[] testAdj(int x, int y, boolean vertical) {
        int[] res;
        if ((vertical && x == 0 || x == pWidth - 1) || (!vertical && y == 0 || y == pHeight - 1))
            res = new int[2];
        else
            res = new int[3];

        int k = 0;
        for (int d : DIR) {
            int adjX = x + d, adjY = y + d;
            if (vertical && adjX >= 0 && adjX < pWidth)
                res[k++] = adjX;
            else if (!vertical && adjY >= 0 && adjY < pHeight)
                res[k++] = adjY;
        }
        return res;
    }

    // Confirmed working
    private void testRemoveHorizontalSeam(int[] seam) {
        if (!validateSeam(seam, false))
            throw new IllegalArgumentException();
        pHeight = pHeight - 1;
        for (int i = 0; i < pWidth; i++)
            for (int j = seam[i]; j < pHeight; j++)
                colors[j][i] = colors[j + 1][i];

        for (int i = 0; i < pWidth; i++)
            for (int j = seam[i] - 1; j < pHeight - 1; j++)
                if (j >= 0 && j < seam[i] + 1)
                    energies[j][i] = energy(i, j);
                else if (j >= seam[i] + 1)
                    energies[j][i] = energy(i, j + 1);
    }

    // Confirmed working
    private void testRemoveVerticalSeam(int[] seam) {
        if (!validateSeam(seam, true))
            throw new IllegalArgumentException();
        pWidth = pWidth - 1;
        for (int i = 0; i < pHeight; i++)
            for (int j = seam[i]; j < pWidth; j++)
                colors[i][j] = colors[i][j + 1];

        for (int i = 0; i < pHeight; i++) {
            for (int j = seam[i] - 1; j < pWidth - 1; j++)
                if (j >= 0)
                    energies[i][j] = energy(j, i);
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
        SeamCarver test1 = new SeamCarver(new Picture(6, 6));
        test1.findVerticalSeam();
        test1.findHorizontalSeam();
        test1.picture();
        test1.removeHorizontalSeam(new int[] { 3, 4, 5, 4, 3, 2 });
        test1.removeVerticalSeam(new int[] { 5, 4, 3, 2, 1 });
        test1.removeHorizontalSeam(new int[] { 3, 4, 4, 4, 3 });
        test1.removeVerticalSeam(new int[] { 4, 3, 2, 3 });

        Picture testPhoto = SCUtility.randomPicture(1000, 1000);

        for (int i = 0; i < 30; i++) {
            SeamCarver timedCarver = new SeamCarver(testPhoto);
            SeamCarver timedCarverTest = new SeamCarver(testPhoto);

            int[] vSeam = timedCarver.findVerticalSeam();
            int[] testVSeam = timedCarverTest.findVerticalSeam();

            assert vSeam.length == testVSeam.length;

            for (int s = 0; s < vSeam.length; s++)
                assert vSeam[s] == testVSeam[s];
            timedCarver.removeVerticalSeam(vSeam);
            timedCarverTest.testRemoveVerticalSeam(vSeam);

            int[] hSeam = timedCarver.findHorizontalSeam();
            int[] testHSeam = timedCarverTest.findHorizontalSeam();

            assert hSeam.length == testHSeam.length;

            for (int s = 0; s < hSeam.length; s++)
                assert hSeam[s] == testHSeam[s];

            timedCarver.removeHorizontalSeam(hSeam);
            timedCarverTest.testRemoveHorizontalSeam(testHSeam);
        }
        SeamCarver timedCarver = new SeamCarver(SCUtility.randomPicture(736, 584));
        int count = 0;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start <= 1000) {
            timedCarver.removeVerticalSeam(timedCarver.findVerticalSeam());
            timedCarver.removeHorizontalSeam(timedCarver.findHorizontalSeam());
            count++;
        }
        System.out.println("Runs per second: " + count);
        SeamCarver testSC = new SeamCarver(new Picture(args[0]));
        // SeamCarver testSC = new SeamCarver(SCUtility.randomPicture(500, 500));

        // Display algorithms functionality
        for (int i = 0; i < 50; i++) {
            int[] rmVSeam = testSC.findVerticalSeam();
            Picture overlaid2 = SCUtility.seamOverlay(testSC.picture(), false, rmVSeam);
            testSC.removeVerticalSeam(rmVSeam);
            overlaid2.show();
        }
        for (int i = 0; i < 50; i++) {
            int[] rmSeam = testSC.findHorizontalSeam();
            Picture overlaid = SCUtility.seamOverlay(testSC.picture(), true, rmSeam);
            testSC.removeHorizontalSeam(rmSeam);
            overlaid.show();
        }
    }
}