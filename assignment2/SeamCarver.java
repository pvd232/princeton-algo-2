import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture pic;
    private final int[][] colors;
    private int pHeight;
    private int pWidth;
    private static final int[] dir = { -1, 0, 1 };

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        pic = new Picture(picture);
        pHeight = pic.height();
        pWidth = pic.width();
        colors = new int[pHeight][pWidth];
    }

    // Current picture
    public Picture picture() {
        return new Picture(pic);
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

        if (x == 0 || x == pWidth - 1 || y == 0 || y == pHeight - 1)
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
        return sp(false);
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return sp(true);
    }

    private int[] pathTo(int x, int y, int[][] edgeTo, int bound, boolean vertical) {
        int e = edgeTo[y][x];
        if (x == 0) // x = 0 for vBottom (start for edgeTo); must decrement from pWidth - 1
            x = pWidth - 1;
        if (y == pHeight) // y = pHeight for vBottom (start for edgeTo); must decrement from pHeight - 1
            y = pHeight - 1;

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

    private int[] sp(boolean vertical) {
        int[][] edgeTo = new int[pHeight + 1][pWidth];
        double[][] distTo = new double[pHeight + 1][pWidth];
        double[][] energyCache = new double[pHeight][pWidth];

        if (vertical) {
            // TODO: Optimize by grabbing n+1 pixels in seam and removing this
            for (int i = 0; i < pWidth; i++) {
                edgeTo[0][i] = i;
                distTo[0][i] = 1000;
            }
            for (int i = 0; i < pHeight; i++)
                for (int j = 0; j < pWidth; j++) {
                    if (i == pHeight - 1) // vBottom
                        relax(j, i, 0, pHeight, distTo, edgeTo, energyCache, vertical);
                    else
                        for (int r : adj(j, i, vertical))
                            relax(j, i, r, i + 1, distTo, edgeTo, energyCache, vertical);
                }
            return pathTo(0, pHeight, edgeTo, pHeight, vertical);
        } else {
            // TODO: Optimize by grabbing n+1 pixels in seam and removing this
            for (int i = 0; i < pHeight; i++) {
                edgeTo[i][0] = i;
                distTo[i][0] = 1000;
            }
            for (int i = 0; i < pWidth; i++)
                for (int j = 0; j < pHeight; j++) {
                    if (i == pWidth - 1) // vBottom
                        relax(i, j, 0, pHeight, distTo, edgeTo, energyCache, vertical);
                    else
                        for (int r : adj(i, j, vertical))
                            relax(i, j, i + 1, r, distTo, edgeTo, energyCache, vertical);
                }
            return pathTo(0, pHeight, edgeTo, pWidth, vertical);
        }
    }

    private void relax(int x1, int y1, int x2, int y2, double[][] distTo, int[][] edgeTo, double[][] energyCache,
            boolean vertical) {
        double weight = 1000; // Edge pixel
        if (x1 != pWidth - 1 && y1 != pHeight - 1) // If x2 & y2 not edge pixel
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
        int[] res;
        if ((vertical && x == 0 || x == pWidth - 1) || (!vertical && y == 0 || y == pHeight - 1))
            res = new int[2];
        else
            res = new int[3];

        int k = 0;
        for (int d : dir) {
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
        Picture newPic = new Picture(pWidth, pHeight);
        for (int i = 0; i < pWidth; i++)
            for (int j = 0; j < pHeight; j++) {
                if (j < seam[i])
                    if (colors[j][i] != 0)
                        newPic.setRGB(i, j, colors[j][i]);
                    else
                        newPic.setRGB(i, j, pic.getRGB(i, j));
                else { // Once the removed seam is reached, shift array
                    if (colors[j + 1][i] != 0)
                        newPic.setRGB(i, j, colors[j + 1][i]);
                    else
                        newPic.setRGB(i, j, pic.getRGB(i, j + 1));
                    colors[j][i] = colors[j + 1][i];
                }
            }
        pic = newPic;
        // int col = 0;
        // for (int s : seam) // Update energy for pixels += 2 adjacent pixels away
        // updateGrad(col++, s, false);
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!validateSeam(seam, true))
            throw new IllegalArgumentException();
        pWidth = pWidth - 1;
        Picture newPic = new Picture(pWidth, pHeight);
        for (int i = 0; i < pHeight; i++) { // TODO: Optimize by not creating a new picture, overwriting old one
            for (int j = 0; j < pWidth; j++) {
                if (j < seam[i])
                    if (colors[i][j] != 0)
                        newPic.setRGB(j, i, colors[i][j]);
                    else
                        newPic.setRGB(j, i, pic.getRGB(j, i));
                else {
                    if (colors[i][j + 1] != 0)
                        newPic.setRGB(j, i, colors[i][j + 1]);
                    else
                        newPic.setRGB(j, i, pic.getRGB(j + 1, i));
                    colors[i][j] = colors[i][j + 1];
                }
            }
        }
        pic = newPic;
        // int row = 0;
        // for (int s : seam) // Update energy for pixels += 2 adjacent pixels away
        // updateGrad(s, row++, true);
    }

    // private void updateGrad(int x, int y, boolean vertical) {
    // if (vertical) {
    // if (x < pWidth && x >= 0 && y >= 0 && y < pHeight)
    // energy[y][x] = energy(x, y, false);
    // if (x - 1 < pWidth && x - 1 >= 0 && y >= 0 && y < pHeight)
    // energy[y][x - 1] = energy(x - 1, y, false);
    // } else {
    // if (x < pWidth && x >= 0 && y >= 0 && y < pHeight)
    // energy[y][x] = energy(x, y, false);
    // if (x < pWidth && x >= 0 && y - 1 >= 0 && y - 1 < pHeight)
    // energy[y - 1][x] = energy(x, y - 1, false);
    // }
    // }

    // Unit testing (optional)
    public static void main(String[] args) {
        // Running time tests
        int height = 1000;
        for (int i = 0; i < 5; i++) {
            height = height * 2;
            Picture testPicture = SCUtility.randomPicture(2000, height);
            SeamCarver testSC = new SeamCarver(testPicture);

            long startTime = System.currentTimeMillis();
            testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
            testSC.removeVerticalSeam(testSC.findVerticalSeam());
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("H trial " + i + " time: " + endTime);
        }
        int width = 1000;
        for (int i = 0; i < 6; i++) {
            width = width * 2;
            Picture testPicture = SCUtility.randomPicture(width, 2000);
            SeamCarver testSC = new SeamCarver(testPicture);

            long startTime = System.currentTimeMillis();
            testSC.removeHorizontalSeam(testSC.findHorizontalSeam());
            testSC.removeVerticalSeam(testSC.findVerticalSeam());
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("W trial " + i + " time: " + endTime);
        }
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