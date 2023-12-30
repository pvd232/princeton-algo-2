package ewg;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private static final int[] DIR = { -1, 0, 1 };
    private final int[][] colors;
    private final double[][] energies;
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
                if (i >= 2) // n-2 colors necessary for n energy computation
                    energies[i - 2][j] = energy(j, i - 2);
            }
    }

    // Current picture
    public Picture picture() {
        Picture newPic = new Picture(pWidth, pHeight);
        for (int i = 0; i < pHeight; i++)
            for (int j = 0; j < pWidth; j++)
                newPic.setRGB(j, i, colors[i][j]);
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
        energies[y][x] = grad;
        return grad;
    }

    private double gradientSq(int x1, int y1, int x2, int y2) {
        int c1 = colors[y1][x1], c2 = colors[y2][x2];
        // Bit shift to extract RGB values from binary encoding of the int RGB value
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
        int[][] edgeTo = new int[pHeight + 1][pWidth]; // pHeight + 1 b/c virtual bottom
        double[][] distTo = new double[pHeight + 1][pWidth];
        for (int i = 0; i < other; i++) // Set starting row for simplified virtual top
            if (vertical) {
                edgeTo[0][i] = i;
                distTo[0][i] = 1000;
            } else {
                edgeTo[i][0] = i;
                distTo[i][0] = 1000;
            }
        // Each vert n in edgeTo searches 3 adj n-1 verts for min energy to add to path
        for (int i = 0; i < bound; i++)
            for (int j = 1; j < other - 1; j++) // 0 < j < bound - 1 b/c border pixels have same path as adj pixels
                if (i == bound - 1 && vertical) // vBottom
                    relax(j, i, 0, pHeight, distTo, edgeTo, vertical);
                else if (i == bound - 1 && !vertical) // vBottom
                    relax(i, j, 0, pHeight, distTo, edgeTo, vertical);
                else if (vertical)
                    for (int r : adj(j, i, vertical))
                        relax(j, i, r, i + 1, distTo, edgeTo, vertical);
                else
                    for (int r : adj(i, j, vertical))
                        relax(i, j, i + 1, r, distTo, edgeTo, vertical);
        return pathTo(pWidth - 1, pHeight - 1, edgeTo[pHeight][0], edgeTo, bound, vertical);
    }

    private int[] pathTo(int x, int y, int e, int[][] edgeTo, int bound, boolean vertical) {
        int[] res = new int[bound];
        for (int i = 0; i < bound; i++) {
            res[bound - i - 1] = e; // Reverse direction returned by edgeTo (built from v->w)
            if (vertical)
                e = edgeTo[y--][e]; // edgeTo will be col val, decrement y
            else
                e = edgeTo[e][x--]; // edgeTo will be row val, decrement x
        }
        return res;
    }

    // Examine adj pixels for new low gradient paths
    private void relax(int x1, int y1, int x2, int y2, double[][] distTo, int[][] edgeTo,
            boolean vertical) {
        double weight = 1000; // Edge pixel
        if (x2 > 0 && x2 < pWidth - 1 && y2 > 0 && y2 < pHeight - 1) // If x2 & y2 not edge pixel
            weight = energies[y2][x2];
        if (distTo[y2][x2] == 0 || distTo[y2][x2] > distTo[y1][x1] + weight) { // If dist not set, or smaller, update
            distTo[y2][x2] = distTo[y1][x1] + weight;
            if (vertical)
                edgeTo[y2][x2] = x1;
            else
                edgeTo[y2][x2] = y1;
        }
    }

    private int[] adj(int x, int y, boolean vertical) {
        int[] res = new int[3]; // Always 3 adj pixels b/c border pixels not considered in shortest paths
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
        pHeight--; // Decrement bound before removing seam
        // Remove seam
        for (int i = 0; i < pWidth; i++)
            for (int j = seam[i]; j < pHeight; j++)
                colors[j][i] = colors[j + 1][i]; // Shift pixel colors to j+1 from seam[i] -> --pHeight

        // Update pixel energies after colors are updated
        for (int i = 0; i < pWidth; i++)
            for (int j = seam[i] - 1; j < pHeight - 1; j++) // seam[i] = idx of adj pixel >= the OG seam pixel
                if (j >= seam[i] + 1) // If idx is the pixel after the seam, shift to cached value
                    energies[j][i] = energies[j + 1][i];
                else if (j >= 0 && j < seam[i] + 1) // seam[i] - 1 && seam[i] energies are changed by seam removal
                    energies[j][i] = energy(i, j);
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, true);
        pWidth--;
        // Remove seam
        for (int i = 0; i < pHeight; i++)
            for (int j = seam[i]; j < pWidth; j++)
                colors[i][j] = colors[i][j + 1];
        // Update pixel energies
        for (int i = 0; i < pHeight; i++)
            for (int j = seam[i] - 1; j < pWidth - 1; j++)
                if (j >= seam[i] + 1)
                    energies[i][j] = energies[i][j + 1];
                else if (j >= 0 && j < seam[i] + 1)
                    energies[i][j] = energy(j, i);
    }

    // Unit testing (optional)
    public static void main(String[] args) {
        // Runtime tests
        Picture[] hPics = new Picture[6], wPics = new Picture[6];
        int height = 500, width = 500;
        for (int i = 0; i < 6; i++) {
            height = height * 2;
            width = width * 2;
            hPics[i] = SCUtility.randomPicture(2000, height);
            wPics[i] = SCUtility.randomPicture(width, 2000);
        }

        for (int i = 0; i < 6; i++) {
            SeamCarver testSC = new SeamCarver(hPics[i]);
            long startTime = System.currentTimeMillis();
            testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
            testSC.removeVerticalSeam(testSC.findVerticalSeam());
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("H trial " + i + " time: " + endTime);
        }

        for (int i = 0; i < 6; i++) {
            SeamCarver testSC = new SeamCarver(wPics[i]);
            long startTime = System.currentTimeMillis();
            testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
            testSC.removeVerticalSeam(testSC.findVerticalSeam());
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("W trial " + i + " time: " + endTime);
        }

        // Test static removals
        SeamCarver test1 = new SeamCarver(new Picture(6, 6));
        test1.findVerticalSeam();
        test1.findHorizontalSeam();
        test1.picture();
        test1.removeHorizontalSeam(new int[] { 3, 4, 5, 4, 3, 2 });
        test1.removeVerticalSeam(new int[] { 5, 4, 3, 2, 1 });
        test1.removeHorizontalSeam(new int[] { 3, 4, 4, 4, 3 });
        test1.removeVerticalSeam(new int[] { 4, 3, 2, 3 });

        // Test consistency of results after multiple removals
        Picture testPhoto = SCUtility.randomPicture(250, 250);
        for (int i = 0; i < 250; i++) {
            SeamCarver timedCarver = new SeamCarver(testPhoto);
            SeamCarver timedCarverTest = new SeamCarver(testPhoto);

            int[] vSeam = timedCarver.findVerticalSeam();
            int[] testVSeam = timedCarverTest.findVerticalSeam();

            assert vSeam.length == testVSeam.length;

            for (int s = 0; s < vSeam.length; s++)
                assert vSeam[s] == testVSeam[s];

            timedCarver.removeVerticalSeam(vSeam);
            timedCarverTest.removeVerticalSeam(vSeam);

            int[] hSeam = timedCarver.findHorizontalSeam();
            int[] testHSeam = timedCarverTest.findHorizontalSeam();

            assert hSeam.length == testHSeam.length;

            for (int s = 0; s < hSeam.length; s++)
                assert hSeam[s] == testHSeam[s];

            timedCarver.removeHorizontalSeam(hSeam);
            timedCarverTest.removeHorizontalSeam(testHSeam);
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

        // Visually display algorithm functionality
        SeamCarver testSC = new SeamCarver(new Picture(args[0]));
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