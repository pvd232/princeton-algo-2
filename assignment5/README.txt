/* *****************************************************************************

- Name: Peter Driscoll
- NetID: N/A
- Precept: N/A
-
- Partner Name: N/A
- Partner NetID: N/A
- Partner Precept: N/A
-
- Hours to complete assignment (optional): ~60
**************************************************************************** */

Programming Assignment 8: Burrows-Wheeler

/* *****************************************************************************

* List in table format which input files you used to test your program.
* Fill in columns for how long your program takes to compress and
* decompress these instances (by applying BurrowsWheeler, MoveToFront,
* and Huffman in succession). Also, fill in the third column for
* the compression ratio (number of bytes in compressed message divided by the number of bytes in the message).

file            size      comp time   decomp time  comp ratio  gzip comp time    gzip decomp time    gzip comp ratio
--------------------------------------------------------------------------------------------------------------------
mobydick.txt    1191463   0.899       0.326            65.3%   0.091             0.014               59.2%               
aesop.txt       191942    0.430       0.32             65.6%   0.029             0.010               60.9%
headshot.jpg    132481    0.471       0.355            0.16%   0.016             0.011               0.90%
**************************************************************************** */


/* *****************************************************************************

* Compare the results of your program (compression ratio and running
* time) on mobydick.txt to that of the most popular Windows
* compression program (pkzip) or the most popular Unix/Mac one (gzip).
* If you don't have pkzip, use 7zip and compress using zip format.

Gzip is ~10x faster compression and ~3x faster decompression, however Burrows Wheeler comp ratio is ~10% higher.

**************************************************************************** */

/* *****************************************************************************

* Give the order of growth of the running time of each of the 6
* methods as a function of the input size n and the alphabet size R
* both in practice (on typical English text inputs) and in theory
* (in the worst case), e.g., n, n + R, n log n, n^2, or R n.
* Include the time for sorting circular suffixes in the Burrows-Wheeler encoder.

algorithm                            typical            worst               driver

------------------------------------------------------------------------------------------------


BurrowsWheeler transform()           1.39 w n lg R      1.39 n log n       LSD radix sort
BurrowsWheeler inverseTransform()    11 N + 4 R         2 WN               3-way radix quicksort 
MoveToFront encode()                 n + .8 n / R       n + n R            radix array search && .2 vowel frequency
MoveToFront decode()                 n + .8 n / R       n + n R            radix array search && .2 vowel frequency
Huffman compress()                   n + R log R        n  + R log R       radix trie search
Huffman expand()                     n                  n                  iterating through input


Memory complexity of CircularSuffixArray is 72 (68 rounded to multiple of 8) + 6 n

instance variable       memory (bytes)
-------------------------------

String s                8 + 24 + 2 n
int n                   4
int[] t                 8 + 24 + 4 n

/* *****************************************************************************

- Known bugs / limitations.

The compression algorithm is much less efficient with jpgs, likely because they have already been compressed
using lossy compression.

**************************************************************************** */

/* *****************************************************************************

- Describe whatever help (if any) that you received.
- Don't include readings, lectures, and precepts, but do
- include any help from people (including course staff, lab TAs,
- classmates, and friends) and attribute them by name.

**************************************************************************** */

/* *****************************************************************************

- Describe any serious problems you encountered.  

Had serious issues with encoding. This was because I attempted to shift chars without pointers to n and n-1.
This was causing n + 1 to drag n until reaching the index of c.

Initially attempted to use 3 way String QS for sorting chars in the inverse transform. This was running slower 
than the Java system Array.sort. However, implementing with LSD radix sort proved to be the fastest (by a small margin).

Initially had small difficulties with structuring the sorting in the Circular Suffix array. First implemented with comparable, but this was not feasible
for 3 way Radix QS. Learned more about static inner classes as a result!

Had small difficulties with periodic string input for Circular suffix creation. Required a small change in the charAt method of the QS, ensuring that 
cylical strings were caught.

Also had issues with using the best iteration method (while vs for loop). For loop was always the easiest to implement,
but encoding naturally lended itself to a while loop b/c the loop was implicetly terminated on a condition, not fixed length.
**************************************************************************** */

/* *****************************************************************************

- List any other comments here. Feel free to provide any feedback
- on how much you learned from doing the assignment, and whether
- you enjoyed doing it. Additionally, you may include any suggestions
- for what to change or what to keep (assignments or otherwise) in future
- semesters.

**************************************************************************** */
