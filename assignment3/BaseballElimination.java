import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;

import java.util.HashMap;
import java.util.HashSet;

public class BaseballElimination {
    private final HashMap<String, Integer> teams;
    private final String[] teamNames; // Array of team names for result printing
    private final int[] w; // Team-indexed array of wins
    private final int[] losses; // Team-indexed array of losses
    private final int[] r; // Team-indexed array of remaining games
    private final int[][] games; // Edge weighted team-indexed game digraph
    private final int n; // Number of teams
    private final int nVerts; // Num verts in FlowNetwork
    private final int nMatches; // Num matches in FlowNetwork

    // Create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        n = Integer.parseInt(in.readLine());
        w = new int[n];
        losses = new int[n];
        r = new int[n];
        games = new int[n][n];
        teams = new HashMap<>();
        teamNames = new String[n];
        nMatches = matches(n - 1); // Matches for num teams excluding queried team
        nVerts = n + nMatches + 2; // Verts in flow network, 2 extra verts for s and t
        int i = 0;
        while (i < n) {
            String team = in.readString().strip();
            teamNames[i] = team;
            teams.put(team, i);

            int win = in.readInt(), loss = in.readInt(), remain = in.readInt();

            w[i] = win;
            losses[i] = loss;
            r[i] = remain;

            games[i] = new int[n];
            for (int s = 0; s < n; s++)
                games[i][s] = in.readInt();
            i++;
        }
    }

    // Number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // All teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // Number of wins for given team
    public int wins(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException();
        return w[teams.get(team)];
    }

    // Number of losses for given team
    public int losses(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException();
        return losses[teams.get(team)];
    }

    // Number of remaining games for given team
    public int remaining(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException();
        return r[teams.get(team)];
    }

    // Number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teams.containsKey(team1) || !teams.containsKey(team2))
            throw new IllegalArgumentException();
        return games[teams.get(team1)][teams.get(team2)];
    }

    // Is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException();

        int wins = w[teams.get(team)] + r[teams.get(team)]; // Augment number of wins with remaining games
        for (int win : w)
            if (wins < win) // Trivial elimination if max potential wins < other team confirmed wins
                return true;

        FlowNetwork fn = network(team);
        FordFulkerson ff = new FordFulkerson(fn, 0, nVerts - 1);

        for (int i = 1; i < nMatches + 1; i++) // Edges from s to nMatches + 1 must be full
            if (ff.inCut(i)) // If one of the match edges pointing from s is not full, team is eliminated
                return true;
        return false;
    }

    private int matches(int numTeams) {
        return (numTeams * (numTeams - 1)) / 2;
    }

    // Subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException();
        if (!isEliminated(team)) // Constructs FlowNetwork and FordFulkerson
            return null;

        HashSet<String> res = new HashSet<>();
        int wins = w[teams.get(team)] + r[teams.get(team)];
        for (int i = 0; i < w.length; i++)
            if (w[i] > wins) { // If trivially eliminated, add team
                res.add(teamNames[i]);
                return res;
            }

        FlowNetwork fn = network(team);
        FordFulkerson ff = new FordFulkerson(fn, 0, nVerts - 1);

        for (int i = 1; i < nMatches + 1; i++)
            for (FlowEdge fE : fn.adj(i))
                // Team the edge points to is in mincut (flow < capacity) && edge not from s
                if (ff.inCut(fE.to()) && fE.from() != 0)
                    res.add(teamNames[fE.to() - nMatches - 1]);
        return res;
    }

    // Build FlowNetwork
    private FlowNetwork network(String team) {
        FlowNetwork fN = new FlowNetwork(nVerts);
        int teamIdx = teams.get(team), tIdx = nVerts - 1;
        int added = 0, wins = w[teamIdx], remain = r[teamIdx];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++)
                if (i != teamIdx && j != teamIdx) { // Exclude queried team
                    fN.addEdge(new FlowEdge(0, ++added, games[i][j])); // Edge for each match from s -> match

                    // Two edges for each match, one pointing to each team in the match
                    fN.addEdge(new FlowEdge(added, nMatches + i + 1, Double.POSITIVE_INFINITY));
                    fN.addEdge(new FlowEdge(added, nMatches + j + 1, Double.POSITIVE_INFINITY));
                }
            // One edge per team, excluding queried team, from team -> t
            FlowEdge t = new FlowEdge(nMatches + i + 1, tIdx, wins + remain - w[i]);
            fN.addEdge(t);
        }
        return fN;
    }

    public static void main(String[] args) {
        // Build FlowNetwork by calling certificateOfElimination method
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }

        // Unit tests to ensure validity of FlowNetwork
        int sCnt = 0, gamesCnt = 0, tCnt = 0, toTCnt = 0;
        for (FlowEdge fe : division.network(division.teamNames[0]).edges()) {
            if (fe.from() == 0)
                sCnt++;
            else if (fe.from() <= division.nMatches && fe.from() > 0) {
                gamesCnt++;
                assert fe.capacity() == Double.POSITIVE_INFINITY;
            } else if (fe.from() > division.nMatches)
                tCnt++;
            if (fe.to() == division.nVerts - 1)
                toTCnt++;
        }
        assert sCnt == division.nMatches; // Games pointing from s should equal bracket
        assert gamesCnt == 2 * division.nMatches; // 2 edges pointing from each game
        assert tCnt == division.n; // verts pointing to t should equal number of teams
        assert tCnt == toTCnt; // Should be the same
    }
}
