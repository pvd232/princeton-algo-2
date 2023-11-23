import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;

public class SeamCarver {
    private Picture pic;
    private int len;

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        pic = new Picture(picture);
        len = width() * height();
    }

    private void createDAG(boolean vertical, EdgeWeightedDigraph dag) {
        // Populate digraph
        for (int i = 0; i < height(); i++)
            for (int j = 0; j < width(); j++)
                addEdges(j, i, vertical, dag);

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

    private void addEdges(int x, int y, boolean vertical, EdgeWeightedDigraph dag) {
        // Skip virtual top and bottom
        if (x == 0 && y == 0 || x == width() && y == height())
            return;

        int[] dir = { -1, 0, 1 };
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
                dag.addEdge(new DirectedEdge(x + y * width(), adjX + adjY * width(), energy(adjX, adjY)));
        }
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
        int c1 = pic.getRGB(x1, y1), c2 = pic.getRGB(x2, y2);
        int r1 = (c1 >> 16) & 0xff, g1 = (c1 >> 8) & 0xff, b1 = c1 & 0xff;
        int r2 = (c2 >> 16) & 0xff, g2 = (c2 >> 8) & 0xff, b2 = c2 & 0xff;
        double rD = Math.pow(r1 - r2, 2), bD = Math.pow(b1 - b2, 2), gD = Math.pow(g1 - g2, 2);
        return rD + gD + bD;
    }

    // Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        EdgeWeightedDigraph hEWG = new EdgeWeightedDigraph(height() * width() + 1);
        createDAG(false, hEWG);

        AcyclicSP hSP = new AcyclicSP(hEWG, 0);

        Iterable<DirectedEdge> pathTo = hSP.pathTo(len);
        int[] res = new int[width()];
        int resCount = 0;
        for (DirectedEdge e : pathTo)
            if (e.from() != 0)
                res[resCount++] = e.from() / width();
        return res;
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        EdgeWeightedDigraph vEWG = new EdgeWeightedDigraph(height() * width() + 1);
        createDAG(true, vEWG);
        AcyclicSP vSP = new AcyclicSP(vEWG, 0);

        Iterable<DirectedEdge> pathTo = vSP.pathTo(len);
        int[] res = new int[height()];
        int resCount = 0;
        for (DirectedEdge e : pathTo)
            if (e.from() != 0)
                res[resCount++] = e.from() % width();
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

            if (!vertical && seam[i] >= height())
                return false;

            if (seam.length - i > 1) {
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
                else
                    newPic.set(i, j, pic.get(i, j + 1));
            }
        pic = newPic;
        len = newPic.width() * newPic.height();
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
                else
                    newPic.set(j, i, pic.get(j + 1, i));
        }
        pic = newPic;
        len = newPic.width() * newPic.height();
    }

    // Unit testing (optional)
    public static void main(String[] args) {
        SeamCarver testSC = new SeamCarver(new Picture(args[0]));
        for (int i = 0; i < 10; i++) {
            int[] rmSeam = testSC.findVerticalSeam();
            Picture overlaid = SCUtility.seamOverlay(testSC.picture(), false, rmSeam);
            testSC.removeVerticalSeam(rmSeam);
            overlaid.show();
        }

        for (int i = 0; i < 10; i++) {
            int[] rmSeam = testSC.findHorizontalSeam();
            Picture overlaid = SCUtility.seamOverlay(testSC.picture(), true, rmSeam);
            testSC.removeHorizontalSeam(rmSeam);
            overlaid.show();
        }

    }
}