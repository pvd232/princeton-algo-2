/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- readme.txt template
- Baseball Elimination
  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

Name: Peter Driscoll
NetID: N/A
Precept: N/A

Partner name: N/A
Partner login: N/A
Partner precept: N/A

If you have a partner state how many times you partnered with each
other before? (Only 3 or less is a valid answer.)

Hours to complete assignment (optional): 30-40 hours

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- Explain concisely how you built the FlowNetwork from the input file.
  The number of wins, losses, and remaining matches per team is parsed from the input file and stored in primitive arrays.

  The FlowNetwork is created upon the client's request for the certificate of elimination. It is initialized to the number
  of matches (n(n-1)/2), plus the number of teams sans the queried team (n-1), + 2 for the s and t vertices.
  It is then populated by creating one FlowEdge from s -> match and two FlowEdges from each match, pointing to each match team.  
  One edge is additionally created from each team to t.
  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- Consider the sports league defined in teams12.txt. Explain in
- nontechnical terms (using the results of certificate of elimination
- and grade-school arithmetic) why Japan is mathematically
- eliminated.

Japan is eliminated because the maximum number of games, assuming it wins all its remaining games, is less than
the minimum number of games each of the other teams can win, when you factor in each team's roster.
Another way of thinking of it is that the max games Japan can win is less than the average of the remaining games
to be played by the other teams plus the number of games already won by the other teams.
********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- What is the order of growth of the amount of memory (in the worst
- case) of your program to determine whether _one_ team is eliminated
- as a function of the number of teams N?
  num matches = (N^2 - 3N + 2)/2

  verts = num matches + num teams - 1 + 2 ---> (N^2 - 3N + 2)/2 + N - 1 + 2 ---> 1/2(N^2 - N + 4)

  edges = num matches + 2(num matches) + num teams - 1 ---> 3(num matches) + num teams - 1 ---> 3/2(N^2 - 3N + 2) + N - 1

  total = 2N^2 + 6N + 4

- Briefly justify your answer.

The memory complexity reduces to that of a Directed Graph using the adjacency list representation, E + V.
********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

Number of vertices = 1/2(N^2 - N + 4)
Number of edges = 3/2(N^2 - 3N + 2) + N - 1

Amount of memory = 2N^2 + 6N + 4

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- What is the order of growth of the running time (in the worst case)
- of your program to determine whether _one_ team is eliminated
- as a function of of the number of teams N?
-
- Assume that the order of growth of the running time (in the worst
- case) to compute a maxflow in a network with V vertices and E edges
- is V E^2.

Time complexity = 1/2(N^2 - N + 4)(3/2(N^2 - 3N + 2) + N - 1) ^ 2

- Briefly justify your answer.

The time complexity reduces to that of finding the mincut in a flow network using the ford fulkerson algorithm, V(E^2).

  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- Known bugs / limitations.
  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- Describe whatever help (if any) that you received.
- Don't include readings, lectures, and precepts, but do
- include any help from people (including course staff, lab TAs,
- classmates, and friends) and attribute them by name.
  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- Describe any serious problems you encountered.  

The only serious problem encountered was in building the flow network. The error occured due to incorrect control flow while looping to create 
the edges in the network. The double for loop had a check that the index was not equal to the teamIdx at each loop, preventing j iterations when i
equaled the teamIdx. This caused there to be j less iterations than was necessary to build the network. The problem was solved by adding assertions
during the creation of the flow network to investigate the correctness of its properties incrementally.
  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- If you worked with a partner, assert below that you followed
- the protocol as described on the assignment page. Give one
- sentence explaining what each of you contributed.
  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/

/********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********\*\*********

- List any other comments here. Feel free to provide any feedback
- on how much you learned from doing the assignment, and whether
- you enjoyed doing it.  
  ********\*\*********\*\*********\*\*********\*********\*\*********\*\*********\*\*********/
