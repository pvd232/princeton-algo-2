/* Programming Assignment 7: Seam Carving */


/* *****************************************************************************
 *  Describe concisely your algorithm to find a horizontal (or vertical)
 *  seam.
 **************************************************************************** */

    My SeamCarver class has 2 different DS and algorithmic implementations. 

    The first implementation uses explicit Graph data structures, including an EdgeWeightedDigraph, and DirectedEdges.
    It uses a dynamic topologocial sort-based algorithm that relaxes each vertex to find the shortest path.

    The second impementation relies upon implicit graph data structures, using 2 vertex-indexed arrays.
    One array tracks the the single source vertex distance, the other tracks the path taken to get to that source.
    The shortest path algorithm is similar to the dynamic topological sort based algorithm, except that the vertices
    are not sorted topologically, but rather are considered in the order they occur in the image, traversed either 
    left to right, or top to bottom. Each vertex is relaxed.

/* *****************************************************************************
 *  Describe what makes an image suitable to the seam-carving approach
 *  (in terms of preserving the content and structure of the original
 *  image, without introducing visual artifacts). Describe an image that
 *  would not work well.
 **************************************************************************** */
    Images with orthogonal edges preserve the aspect ratio of the image when resizing via removing coplanar cuts.
    Also, images with consistent, well-distributed layered gradience / prominent backgrounds.


/* *****************************************************************************
 *  Perform computational experiments to estimate the running time to reduce
 *  a W-by-H image by one column and one row (i.e., one call each to
 *  findVerticalSeam(), removeVerticalSeam(), findHorizontalSeam(), and
 *  removeHorizontalSeam()). Use a "doubling" hypothesis, where you
 *  successively increase either W or H by a constant multiplicative
 *  factor (not necessarily 2).
 *
 *  To do so, fill in the two tables below. Each table must have 5-10
 *  data points, ranging in time from around 0.25 seconds for the smallest
 *  data point to around 30 seconds for the largest one.
 **************************************************************************** */

(keep W constant)
 W = 2000
 multiplicative factor (for H) = 2

 H           time (seconds)      ratio       log ratio
------------------------------------------------------
2000        0.8870                -           
4000        1.3060                1.4724      0.1680
8000        3.1490                2.4112      0.3822
16000       6.0940                1.9352      0.2867
32000       23.046                3.8099      0.5809
64000       71.516                3.1032      0.4918

avg log = .3819
1.0605 = a * (2000 * 20000) ^ .3819
a = .012496

(keep H constant)
 H = 2000
 multiplicative factor (for W) = 2

 W           time (seconds)      ratio       log ratio
------------------------------------------------------
2000        0.7680                -                
4000        1.3310                1.7331      0.2388  
8000        2.7150                2.0398      0.3096
16000       5.5830                2.0564      0.3131
32000       11.266                2.0179      0.3049
64000       31.745                2.8178      0.4499

avg log = .3233
9.091 = a * (2000 * 20000) ^ .3233
a = .0316859


/* *****************************************************************************
 *  Using the empirical data from the above two tables, give a formula 
 *  (using tilde notation) for the running time (in seconds) as a function
 *  of both W and H, such as
 *
 *       ~ 5.3*10^-8 * W^5.1 * H^1.5
 *
 *  Briefly explain how you determined the formula for the running time.
 *  Recall that with tilde notation, you include both the coefficient
 *  and exponents of the leading term (but not lower-order terms).
 *  Round each coefficient and exponent to two significant digits.
 **************************************************************************** */


Running time (in seconds) to find and remove one horizontal seam and one
vertical seam, as a function of both W and H:

    Hypothesis: Running time order of growth is linear to H * W -> a * (W*H) ^ b
    ~ 
       _______________________________________




/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */




/* *****************************************************************************
 *  Describe any serious problems you encountered.                    
 **************************************************************************** */




/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on how much you learned from doing the assignment, and whether    
 *  you enjoyed doing it.                                             
 **************************************************************************** */