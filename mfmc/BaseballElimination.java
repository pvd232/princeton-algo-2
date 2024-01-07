package mfmc;

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
    private final int nVerts;
    private final int nMatches;

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
        nMatches = matches(n);
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

        FlowNetwork f = network(team);
        FordFulkerson ff = new FordFulkerson(f, 0, nVerts - 1);

        for (int i = 1; i < f.V(); i++)
            if (ff.inCut(i))
                return true;
        return false;
    }

    private int matches(int numTeams) {
        int res = 0;
        for (int i = numTeams - 2; i > 0; i--)
            res += i;
        return res;
    }

    // Subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!teams.containsKey(team))
            throw new IllegalArgumentException();
        if (!isEliminated(team))
            return null;

        HashSet<String> res = new HashSet<>();
        int wins = w[teams.get(team)] + r[teams.get(team)];
        for (int i = 0; i < w.length; i++)
            if (w[i] > wins) {
                res.add(teamNames[i]);
                return res;
            }

        FlowNetwork f = network(team);
        FordFulkerson ff = new FordFulkerson(f, 0, nVerts - 1);
        for (int i = 0; i < f.V(); i++)
            for (FlowEdge fE : f.adj(i))
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
            for (int j = 0; j < n; j++) {
                if (i != teamIdx && j != teamIdx && j > i) {
                    FlowEdge s = new FlowEdge(0, ++added, games[i][j]);
                    fN.addEdge(s);

                    FlowEdge e1 = new FlowEdge(added, nMatches + i + 1, Double.POSITIVE_INFINITY),
                            e2 = new FlowEdge(added, nMatches + j + 1, Double.POSITIVE_INFINITY);
                    fN.addEdge(e1);
                    fN.addEdge(e2);
                }
            }
            FlowEdge t = new FlowEdge(nMatches + i + 1, tIdx, wins + remain - w[i]);
            fN.addEdge(t);
        }
        int sCnt = 0, gamesCnt = 0, tCnt = 0, toTCnt = 0;
        for (FlowEdge fe : fN.edges()) {
            if (fe.from() == 0)
                sCnt++;
            else if (fe.from() <= nMatches && fe.from() > 0) {
                gamesCnt++;
                // assert fe.capacity() == Double.POSITIVE_INFINITY;
            } else if (fe.from() > nMatches)
                tCnt++;
            if (fe.to() == tIdx)
                toTCnt++;
        }
        assert sCnt == nMatches; // Games pointing from s should equal bracket
        assert gamesCnt == 2 * nMatches; // 2 edges pointing from each game
        assert tCnt == n; // N-1 verts pointing to t
        assert tCnt == toTCnt; // Should be the same
        return fN;
    }

    public static void main(String[] args) {
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
    }
}
