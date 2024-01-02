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
1000        0.147                -           
2000        0.362                2.3946      0.3792
4000        0.577                1.6392      0.2146
8000        0.976                1.6915      0.2283
16000       2.734                2.3293      0.3672
32000       12.123               5.3325      0.7269

avg log = .3833

(keep H constant)
 H = 2000
 multiplicative factor (for W) = 2

 W           time (seconds)      ratio       log ratio
------------------------------------------------------
1000        0.126                -                
2000        0.280                2.2222      0.3468
4000        0.412                1.4714      0.1677
8000        0.828                2.0097      0.3031
16000       2.126                2.5676      0.4095
32000       4.473                2.1040      0.3230


avg log = .3100

0.828 = a * 8000 ^ .31 * 2000 ^ .3833
a = 0.02390494867 ~ 2.39×10−2


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

    Time complexity: Running time order of growth is linear to H * W, determined by the shortest path function which traverses ~ 3(M*N) pixels...

    ~ 2.4*10^−2 * W^.31 * H^.38
    _______________________________________

    Memory complexity: Memory usage order of growth is linear to M*N. Usage, in bytes, was:

     ~ 12*M*N + 64N + 88
    _______________________________________


/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */
1. Program cannot compute for photos larger than 128K W and 64K H.
2. Remove vertical seam is significantly (2x-5x) slower than horizontal seam for large inputs (H >= 16000)


/* *****************************************************************************
 *  Describe any serious problems you encountered.                    
 **************************************************************************** */

>>>>> BEFORE OPTIMIZING >>>>
1. Initially I did not read the instructions, and the seam consisted of both x and y coordinates, in a set.
   I removed the pixels by storing them in a dictionary and examining each pixel for a match. I would then transfer
   the non seam pixels to a new array, and subsequently update the photo instance variable. I fixed this by storing the
   index of the orthogonal coordinate in the seam array, and skipping over the pixels that matched when updating the photo.

2. I had some early difficulties in adopting the virtual top strategy from the union find percolation algorithm to graph traversal.

3. I had minor difficulties transitioning from an explicit graph implementation to an implicit one. This stemmed primarily from 
   reimplementing the shortest paths traversal / mapping functions. I also did not fully understand how intermediate distances and
   traversal paths were stored in distTo and edgeTo.

4. I had major difficulties updating the energy cache in the remove seam function. I believe it was because I was sloppy with my tracking
   of indices in the iteration across the pixels. I also was using my energy function to compute new AND cached energy values, which was
   convoluted and inefficient. I settled on reseting the energy cache for each find seam call. Then after attempting to implement a bimodal
   starting / ending index for my colors array, I refactored my indices, joining common logic from vertical and horizontal find seam functions,
   and mapping boundary indices into function parameters. This drastically simplified the logic for shifting gradient values such that only
   seam[i] and seam[i] - 1 were recomputed. The lesson is to BE CAREFUL with loop indices. Something as innoculous of using a Math.max(n, 0) for
   the starting index instead of a if (n >= 0) within the loop had unforseen consequences. Get everything working and stabalized before trying
   fancy optimzations.

5. I ultimately failed to implement a bimodal index for starting and stopping points of the pixels. My theory was that starting at origin 0,0
   was inefficcient for seams >= pHeight, pWidth / 2. However, after testing, the difference was negligible. And the added complexity of mapping 
   everything from the start indexed cache arrays and the path functions proved insurmountable.

5. Generally, I think I would benefit from a more incremental, test-driven evaluation of my hypothesis driven approach to algorithm design. 
Having robust unit tests, with Brute force checking functions, would greatly simplify my evaluation of changes to the program, and reduce time spend in rabbit holes.
   I have a habbit of making a series of changes aligned with my hypothesis, and then testing later, requiring me to backtrack significantly.

>>>>> AFTER OPTIMIZING >>>>


/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on how much you learned from doing the assignment, and whether    
 *  you enjoyed doing it.                                             
 **************************************************************************** */

 Optimization journey...

 * General tip -> For if / else blocks, if some conditions will be true more often than others, put those at the beginning of the control flow block.
 * Less is more... reducing size of control flow blocks is always ideal. Usually be leveraging information known about the input, or restructuring
   the flow of information such that attributes of the data are determined in advance. 
 * Beware of lazy initialization of arrays. Although it is intended to be a sublinear approach, it usually embeds complexity in the control flow,
   requiring that you check if a value exists many times. It is usually better to just initialize everything up front, especially if multiple 
   passes of the function will eventually fill out the array anyway. This was especially true for the colors and energies cache arrays.

1. I reimplemented by adj func from returning an ArrayList<Integer> to return int[], dynamically choosing between len 2 and len 3.
   I also refactored the directions array creation to be a static variable, such that it would not be created each time the adj func
   was called.

2. I was previously storing the path energy sum (distTo) and path pixels (edgeTo) as 1D primitive int arrays. This required 
   computing the 2D index whenever reading / writing to the array in the sp function. To avoid this, I traded time for memory
   by switching to a 2D array.

3. I began storing energy computations from the relax function in a 2D array. I realized that I was computing 3(M*N) energies when 
   at most I should have to compute M*N. The array was reset for each call to find seam.

>>> These two optimizations improved my program runtime from .77 to .36 >>>

4. I changed my adj func to return a fixed length 3 int[]. I did this by by removing the need for a variable length result
   by limiting path traversal to non-border pixels (0 < j < bound - 1 ), which all have 3 adj pixels.

5. I ceased to update the photo instance variable when removing seams. Instead, only updating the pHeight and pWidth and colors cache array.
   I would then update the photo in the picture method, and return it. However I then realized I did not need to store the photo as an instance
   variable at all. I only needed to create a photo instance on demand when the client requested it, using the cached colors. This freed up the
   memory necessary to store the energies cache as an instance variable maintained across seam removals.


6. I reduced energy cache computations to only be necessary during initialization, and for 2*k*M / 2*k*N pixels (k = number of seams removed).
   I did this by deducing that only the pixel north / west and south / east of the seam would have their energy values changed by the removal 
   of the seam. Thus for the all pixels after the seam[i] or before seam[i] -1, the update to the energy was just energies[i][j] = energies[i][j+1].

>>> These 3 optimizations reduced runtime from .36 to .09 !! >>>