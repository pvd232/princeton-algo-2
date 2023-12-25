import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private static final int[] DIR = { -1, 0, 1 };
    private Picture pic;
    private final int[][] colors;
    private int pHeight;
    private int pWidth;

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        pic = new Picture(picture);
        pHeight = pic.height();
        pWidth = pic.width();
        colors = new int[pHeight][pWidth];
        for (int i = 0; i < pHeight; i++)
            for (int j = 0; j < pWidth; j++)
                colors[i][j] = pic.getRGB(j, i);
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

    public double energy(int x, int y) {
        return energy(x, y, null);
    }

    // Energy of pixel at column x and row y
    private double energy(int x, int y, double[][] energyCache) {
        if (x < 0 || x >= pWidth || y < 0 || y >= pHeight)
            throw new IllegalArgumentException();
        else if (x == 0 || x == pWidth - 1 || y == 0 || y == pHeight - 1)
            return 1000;

        if (energyCache == null || energyCache[y][x] == 0.0) {
            double xGrad = gradientSq(x + 1, y, x - 1, y), yGrad = gradientSq(x, y + 1, x, y - 1);
            double grad = Math.sqrt(xGrad + yGrad);
            if (energyCache != null)
                energyCache[y][x] = grad;
            return grad;
        } else
            return energyCache[y][x];
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
        return sp(pWidth, pHeight, false);
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return sp(pHeight, pWidth, true);
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

    private int[] sp(int bound, int other, boolean vertical) {
        int[][] edgeTo = new int[pHeight + 1][pWidth];
        double[][] distTo = new double[pHeight + 1][pWidth], energyCache = new double[pHeight][pWidth];
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
                if (i == bound - 1) // vBottom
                    if (vertical)
                        relax(j, i, 0, pHeight, distTo, edgeTo, energyCache, vertical);
                    else
                        relax(i, j, 0, pHeight, distTo, edgeTo, energyCache, vertical);
                else if (vertical)
                    for (int r : adj(j, i, vertical))
                        relax(j, i, r, i + 1, distTo, edgeTo, energyCache, vertical);
                else
                    for (int r : adj(i, j, vertical))
                        relax(i, j, i + 1, r, distTo, edgeTo, energyCache, vertical);
        return pathTo(pWidth - 1, pHeight - 1, edgeTo[pHeight][0], edgeTo, bound, vertical);
    }

    private void relax(int x1, int y1, int x2, int y2, double[][] distTo, int[][] edgeTo, double[][] energyCache,
            boolean vertical) {
        double weight = 1000; // Edge pixel
        if (x2 < pWidth - 1 && y2 < pHeight - 1) // If x2 & y2 not edge pixel
            weight = energy(x2, y2, energyCache);
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
        pHeight = pHeight - 1;
        for (int i = 0; i < pWidth; i++)
            for (int j = seam[i]; j < pHeight; j++) {
                pic.setRGB(i, j, colors[j + 1][i]);
                colors[j][i] = colors[j + 1][i];
            }
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!validateSeam(seam, true))
            throw new IllegalArgumentException();
        pWidth = pWidth - 1;
        for (int i = 0; i < pHeight; i++) {
            for (int j = seam[i]; j < pWidth; j++) {
                pic.setRGB(j, i, colors[i][j + 1]);
                colors[i][j] = colors[i][j + 1];
            }
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
        while (System.currentTimeMillis() - start < 1000) {
            timed.removeHorizontalSeam(timed.findHorizontalSeam());
            timed.removeVerticalSeam(timed.findVerticalSeam());
            count++;
        }
        System.out.println("Runs per second: " + count);
        SeamCarver testSC = new SeamCarver(new Picture(args[0]));

        // Display algorithms functionality
        for (int i = 0; i < 50; i++) {
            int[] rmSeam = testSC.findVerticalSeam();
            Picture overlaid = SCUtility.seamOverlay(testSC.picture(), false, rmSeam);
            testSC.removeVerticalSeam(rmSeam);
            overlaid.show();
        }

        for (int i = 0; i < 50; i++) {
            int[] rmSeam = testSC.findHorizontalSeam();
            Picture overlaid = SCUtility.seamOverlay(testSC.picture(), true, rmSeam);
            testSC.removeHorizontalSeam(rmSeam);
            overlaid.show();
        }
    }
}