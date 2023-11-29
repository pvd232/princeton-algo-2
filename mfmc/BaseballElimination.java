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
    private final String[] teamNames;
    private final int[] w;
    private final int[] losses;
    private final int[] r;
    private final int[][] games;

    // Create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int n = Integer.parseInt(in.readLine());
        w = new int[n];
        losses = new int[n];
        r = new int[n];
        games = new int[n][n];
        teams = new HashMap<>();
        teamNames = new String[n];
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

        int wins = w[teams.get(team)] + r[teams.get(team)];
        for (int win : w)
            if (wins < win)
                return true;

        FlowNetwork f = network(team);
        FordFulkerson ff = new FordFulkerson(f, 0, netLen() - 1);

        for (int i = 1; i < f.V(); i++)
            if (ff.inCut(i))
                return true;
        return false;
    }

    private int matches(int n) {
        int res = 0;
        for (int i = n - 2; i > 0; i--)
            res += i;
        return res;
    }

    private int netLen() {
        return numberOfTeams() + matches(numberOfTeams()) + 2;
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
        FordFulkerson ff = new FordFulkerson(f, 0, netLen() - 1);
        for (int i = 0; i < f.V(); i++)
            for (FlowEdge fE : f.adj(i))
                if (ff.inCut(fE.to()) && fE.from() != 0)
                    res.add(teamNames[fE.to() - matches(numberOfTeams()) - 1]);
        return res;
    }

    private FlowNetwork network(String team) {
        FlowNetwork fN = new FlowNetwork(netLen());
        int bracket = matches(numberOfTeams());
        int teamIdx = teams.get(team), tIdx = netLen() - 1;

        int added = 0, wins = w[teamIdx], remain = r[teamIdx];
        for (int i = 0; i < numberOfTeams(); i++) {
            for (int j = 0; j < numberOfTeams(); j++) {
                if (i != teamIdx && j != teamIdx && j > i) {
                    FlowEdge s = new FlowEdge(0, ++added, games[i][j]);
                    fN.addEdge(s);

                    FlowEdge e1 = new FlowEdge(added, bracket + i + 1, Double.POSITIVE_INFINITY),
                            e2 = new FlowEdge(added, bracket + j + 1, Double.POSITIVE_INFINITY);
                    fN.addEdge(e1);
                    fN.addEdge(e2);
                }
            }
            FlowEdge t = new FlowEdge(bracket + i + 1, tIdx, wins + remain - w[i]);
            fN.addEdge(t);
        }
        int sCount = 0;
        int gamesCount = 0;
        int tCount = 0;
        int toTCount = 0;
        for (FlowEdge fe : fN.edges()) {
            if (fe.from() == 0)
                sCount++;
            else if (fe.from() <= bracket && fe.from() > 0) {
                gamesCount++;
                assert fe.capacity() == Double.POSITIVE_INFINITY;
            } else if (fe.from() > bracket) {
                tCount++;
            }
            if (fe.to() == tIdx) {
                toTCount++;
            }
        }
        assert sCount == bracket; // Games pointing from s should equal bracket
        assert gamesCount == 2 * bracket; // 2 edges pointing from each game

        assert tCount == numberOfTeams(); // N-1 verts pointing to t

        assert tCount == toTCount; // Should be the same
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
