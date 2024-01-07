/* *****************************************************************************

- Name: Peter Driscoll
- NetID: N/A
- Precept: N/A
-
- Partner Name: N/A
- Partner NetID: N/A
- Partner Precept: N/A
-
- Hours to complete assignment (optional): ~80
**************************************************************************** */

Programming Assignment 4: BoggleBoard

/* *****************************************************************************

**************************************************************************** */

Explain your method for storing the dictionary words.  How much storage is required to store M words?  Assume the average word length is L.

The BoggleBoard dictionary is represented via a Symbol Table, which is implemented using the Trie data structure.
Each character of each word is stored as the index of an R length array at each Trie node. This would typically require
(R + 1) * M memory. However, are 2 extra instance variables on the internal Trie Node class utilized to reduce runtime. 

1. String path -> Stores the sum of the chars on the path to a given Trie Node. 
This was done to limit string concatenation to once per char in the Trie, as opposed to concatenating for each 
query from the Boggle Board search.

2. boolean isParent -> Signifies if the given Trie Node is a leaf node to prevent unnecessary prefix queries.
 

Additionally, there is one extra reference to a Node, prev, on the TrieST class that tracks the previously queried node.
This was done to leverage the nature of the BoggleBoard search, which incrementally adds chars to the previous prefix
except when backtracking.

Memory complexity of TrieST.Node is...

instance variable       memory (bytes)               description
------------------------------------------------------------------------
OVERHEAD            16
String val          8 + initialization
String path         8 + 40 + 2 * length of word
boolean isParent    1
Node[] next         8 + 24 + 26(8)                   creates array of 26 references (ref = 8 bytes) to Node objects

total = 313 -> 320 + initialization

Memory complexity of TrieST is...

instance variable       memory (bytes)               description
------------------------------------------------------------------------
OVERHEAD                16                            -
int R                   4                             -
Node root:              8 + 320 -> 328 + initialization      inner Node class is static, no reference to enclosing class    
Node prev:              8 + 320 -> 328 + initialization      inner Node class is static, no reference to enclosing class    

total = 656 + initialization

Memory complexity of BoggleSolver is...

instance variable       memory (bytes)               description
------------------------------------------------------------------------
OVERHEAD                16
int[3] DIR              8 + 24 + 12 = 44 -> 48                  
TrieST dict             8 (not initialized)
int n                   4
int m                   4
int maxLex              4

total = 84 + 3(I * J)

Overall memory complexity for M words of average length L with Radix 26:
100 + 3IJ + # non-leaf nodes * (640 + 2*((L-1)/2)) + # leaf nodes * (360 + 2L)
# non-leaf nodes = M, # leaf nodes = M

100 + 3IJ + M * (639 + L) + M * (360 + 2L)
100 + 3IJ + 639M + ML + 360M + 2ML
100 + 3IJ + 999M + 3ML

Estimate for yawl dictionary (264061 words, avg length of 9)
999*264061 + 3*264061*9

Expected memory consumption with yawl dict -> ~271,000,000
Actual memory consumption with yawl dict -> 202,481,152


/* *****************************************************************************

**************************************************************************** */

Explain your method for storing the NxN puzzle.  How much storage is required?

I stored the length and width of the puzzle globally. Locally, I stored the puzzle in an NxN char array. I also created a local boolean[][]
visited array for backtracking from DFS. Additionally, I pre-computed the adjacencies of each tile and stored them in a local array of arrays. 

The total amount of storage required was 84 bytes plus ~7*N^2 for the visited, graph clone, and adj arrays.
 
/* *****************************************************************************

Explain how you search for words.  Comment on the amount of time it takes for a successful or unsuccessful search.

Words are queried using an R-way Trie. Searching for a given key requires a sequential traversal of 
the chars of the String. Each char is used as an index in a length R array of Nodes maintained by each Trie node. 
If the last char of the String is reached and the value associated with the Node is not null, the search is successful.

Search time complexity is logR(N).
 
**************************************************************************** */

/* *****************************************************************************

Which input files did you use to test your program?  Mark the ones where your answers agreed with our reference solutions.  How long did your program take to solve these instances?

I tested my program using the exotic boards (16-q, antidis..., aqua, couscous) and the 1000 point board. 
My program agreed with the reference solution 100% of the time.

**************************************************************************** */
 
/* *****************************************************************************

Justify the approach you took in terms of space usage and running time.
 
 My approach implemented the dictionary using the R-way Trie data structure, as it among the fastest String symbol
 table implementations, only requiring successive testing of char indices in R length arrays. This speedy performance
 comes at the cost of memory, requiring R+1(N) references, however this is an acceptable tradeoff for a low R value
 - as is the case of the English alphabet.

 Further optimizations made included:

 1. Precomputing tile adjacencies during BoggleBoard initialization to prevent repetitive adjacency computations during DFS.
 2. Storing the char path of each Trie node as a String, instead of just storing complete words in the leaf nodes. 
 This frontloads String concatenation and limits it to once per Trie node during dictionary initialization, preventing 
 repetitive concatenations during prefix path traversal.

 3. Storing the parental status of Trie nodes to allow for termination of search paths at Trie nodes without children
 4. Conjoining the querying of Node parental status, which occurs before the exploration of adjacent nodes,
    with the addition of new words to the result set.
**************************************************************************** */

/* *****************************************************************************

Any known bugs / limitations?

**************************************************************************** */

/* *****************************************************************************

List whatever help (if any) that you received.
 
I asked ChatGPT for general suggestions on how to improve the efficiency of my program. This led to 
a more efficient backtracking implementation, as well as checking parental status and conjoining that functionality 
with result addition.

**************************************************************************** */

/* *****************************************************************************

Describe any serious problems you encountered.

1. I initially backtracked by storing a concatenating String representations of the x,y coordinates of each tile 
in the DFS path and searching for them using KMP. This was highly inefficient and unnecessary. Soon after beginning 
speed tests, I swapped this for a HashSet of the String coordinates, which also did not require a delimiter. 

This increased efficieny from ~39x -> ~29x

2. I initially pruned the DFS search path by doing a prefix query of adjacent nodes. This was inefficient, and I
refactored the prefix query to the parent node, just before exploration of adjacent nodes. 

This increased efficiency by 5x, from ~29x -> ~5.5x
 
3. I struggled to effectively implement caching. I initially maintained a HashSet of previously queried nodes, 
which were queried in each prefix query. However, the underlying HashSet implementation was slower than the TrieST,
and I subsequently removed the cache entirely.

This increased efficiency from 5.5x -> 5.15x

4. I struggled to prefix search a new char without incurring the cost of String concatenation. I accomplished this
by storing the char path of each Trie node as a String, and passing the char being queried into the search function. 
The case of Q handled by an if block in the search which increments the search to the letter U.

This increased efficiency from 5.15x -> 4.85x

5. I struggled with choosing the correct data structure. I spent an extensive amount of time on the R^2 TST implementation. 
I was incorrectly computing the indices for mapping TST nodes with the correct chars of the first two letters of the 
query String. I erroneously assumed that the R^2 TST was optimal, and that the R-way Trie would use too much memory for
the yawl dictionary. 

This increased efficiency from 4.85x -> 3.8x

6. With the nudge of ChatGPT, replaced HashSet cloning / querying for backtracking with boolean visited array. The key
was to update the visited status before and after the termination of each recursive call. Understanding the mechanics 
of recursive calls in the executino stack was critical. Backtracking must be implemented in accordance with the start 
and end of each recursive call.

This increased efficiency from 3.8x -> 1.3x

7. Similarly, was aided in implementing additional pruning against the longest word in the dictionary, and against leaf
nodes, and conjoined addition to the result set.

This increased efficiency from 1.3x -> 1.12x

/* *****************************************************************************

Any other comments or feedback?

**************************************************************************** */
