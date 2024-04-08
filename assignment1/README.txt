/* *****************************************************************************
 *  Name: Peter Driscoll    
 *  NetID: N/A   
 *  Precept: N/A 
 *
 *  Partner Name: N/A    
 *  Partner NetID: N/A   
 *  Partner Precept: N/A 
 *
 *  Hours to complete assignment (optional):
 *
 **************************************************************************** */

Programming Assignment 1: WordNet


/* *****************************************************************************
 *  Describe concisely the data structure(s) you used to store the 
 *  information in synsets.txt. Why did you make this choice?
 **************************************************************************** */

Description:

I used a HashMap<String, ArrayList<Integer>> to store all the WordNet nouns. 
I did this because the Client makes queries using the noun, thus the key
had to be the noun, and the HashMap provides constant time lookup.
Each noun has multiple potential synsets associated with it, and due to CPU 
caching I chose the ArrayList to store the synset ids.

I used a synset-indexed ArrayList<String> to store the synsets, allowing for 
quick retrieval as synsets when given the synset index by SAP.    


/* *****************************************************************************
 *  Describe concisely the data structure(s) you used to store the 
 *  information in hypernyms.txt. Why did you make this choice?
 **************************************************************************** */

Description:

I used a Digraph to store the hypernyms as the WordNet is a Directed Acyclic
Graph. The Digraph is built using the hypernyms, with an edge from each synset
to each of its hypernyms.


/* *****************************************************************************
 *  Describe concisely the algorithm you use in the constructor of
 *  ShortestAncestralPath to check if the digraph is a rooted DAG.
 *  What is the order of growth of the worst-case running times of
 *  your algorithms as a function of the number of vertices V and the
 *  number of edges E in the digraph?    
 **************************************************************************** */

Description:

I used a topological sort algorithm with uses a depth first search to 
find the reverse postorder. Each vertex undergoes a DFS of its adjacent vertices.
A boolean array tracks the visited adjacent vertices for each vertex.
A cycle is detected if a vertex is in the processing stack when it appears as an adjacent vertex for exploration.


Order of growth of running time:


/* *****************************************************************************
 *  Describe concisely your algorithm to compute the shortest ancestral path
 *  in ShortestAncestralPath. For each method, give the order of growth of
 *  the best- and worst-case running times (as a function of the number of
 *  vertices V and the number of edges E in the digraph)?
 *
 *  If you use hashing, assume the uniform hashing assumption so that put()
 *  and get() take constant time per operation.
 *
 *  Be careful! If you use a BreadthFirstDirectedPaths object, do not forget
 *  to count the time needed to initialize the marked[], edgeTo[], and
 *  distTo[] arrays.
 **************************************************************************** */

Description:


                                 running time
method                  best case            worst case
--------------------------------------------------------
length()

ancestor()

lengthSubset()

ancestorSubset()



/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */


/* *****************************************************************************
 *  Describe whatever help (if any) that you received.
 *  Do not include readings, lectures, and precepts, but do
 *  include any help from people (including course staff, lab TAs,
 *  classmates, and friends) and attribute them by name.
 **************************************************************************** */


/* *****************************************************************************
 *  Describe any serious problems you encountered.                    
 **************************************************************************** */


/* *****************************************************************************
 *  If you worked with a partner, assert below that you followed
 *  the protocol as described on the assignment page. Give one
 *  sentence explaining what each of you contributed.
 **************************************************************************** */




/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on how much you learned from doing the assignment, and whether    
 *  you enjoyed doing it.                                             
 **************************************************************************** */