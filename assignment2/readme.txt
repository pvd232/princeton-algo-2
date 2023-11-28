/* Programming Assignment 7: Seam Carving */


/* *****************************************************************************
 *  Describe concisely your algorithm to find a horizontal (or vertical)
 *  seam.
 **************************************************************************** */

    My SeamCarver class has 2 different DS and algorithmic implementations. 

    The first implementation uses explicit Graph data structures, including an EdgeWeightedDigraph, and DirectedEdges
    It uses a dynamic, topologocial sort based algorithm that relaxes each vertex to find the shortest path.

    The second impementation relies upon implicit graph data structures, using 2 vertex-indexed arrays.
    One array tracks the the single source vertex distance, the other tracks the path taken to get to that source.
    The shortest path algorithm is similar to the dynamic, topological sort based algorithm, except that the vertices
    are not sorted topologically, but rather are considered in the order they occur in the image, traversed either 
    left to right, or top to bottom. Each vertex is relaxed.

/* *****************************************************************************
 *  Describe what makes an image suitable to the seam-carving approach
 *  (in terms of preserving the content and structure of the original
 *  image, without introducing visual artifacts). Describe an image that
 *  would not work well.
 **************************************************************************** */
    Images with orthogonal edges preserve the aspect ratio of the image when resizing via removing coplanar cuts.
    Also, images with high levels of gradience / prominent backgrounds.


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
 multiplicative factor (for H) =

 H           time (seconds)      ratio       log ratio
------------------------------------------------------
...
...
...
...
...
...


(keep H constant)
 H = 2000
 multiplicative factor (for W) =

 W           time (seconds)      ratio       log ratio
------------------------------------------------------
...
...
...
...
...
...



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