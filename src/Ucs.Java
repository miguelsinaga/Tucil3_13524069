package src;

import java.util.*;


public class UCS {
    public static SolveResult solve(Board board) {
        long startTime = System.currentTimeMillis();

        int[] start = board.getStart();
        int[] goal  = board.getGoal();
        int initialCP = (board.getNumCheckpoints() > 0) ? 0 : -1;


        State initState = new State(start[0], start[1], initialCP, 0, 0, null, -1);
        initState.f = 0; // UCS: f = g
        PriorityQueue<State> pq = new PriorityQueue<>();
        pq.offer(initState);
        Map<State, Double> bestG = new HashMap<>();
        bestG.put(initState, 0.0);

        int iterations = 0;

        while (!pq.isEmpty()) {
            State current = pq.poll();
            Double recorded = bestG.get(current);
            if (recorded != null && current.g > recorded) continue;

            iterations++;
            if (current.row == goal[0] && current.col == goal[1]
                    && current.nextCheckpoint == -1) {
                long elapsed = System.currentTimeMillis() - startTime;
                return buildResult(current, iterations, elapsed);
            }
            for (int dir = 0; dir < 4; dir++) {
                Board.SlideResult slide = board.slide(
                    current.row, current.col, dir, current.nextCheckpoint);

                if (!slide.valid) continue;
                int newCP = computeNewCP(board, current.nextCheckpoint,
                                         slide.tilesRow, slide.tilesCol);

                double newG = current.g + slide.moveCost;
                State next = new State(slide.endRow, slide.endCol, newCP,
                                       newG, 0, current, dir);
                next.f = newG; 
                Double prevBest = bestG.get(next);
                if (prevBest == null || newG < prevBest) {
                    bestG.put(next, newG);
                    pq.offer(next);
                }
            }
        }
        long elapsed = System.currentTimeMillis() - startTime;
        return new SolveResult(iterations, elapsed);
    }
    static int computeNewCP(Board board, int currentCP, int[] tilesRow, int[] tilesCol) {
        if (currentCP == -1) return -1; // sudah selesai semua
        int cp = currentCP;
        char[][] grid = board.getGrid();
        for (int i = 0; i < tilesRow.length; i++) {
            char c = grid[tilesRow[i]][tilesCol[i]];
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                if (digit == cp) {
                    cp++;
                    if (cp >= board.getNumCheckpoints()) {
                        return -1; 
                    }
                }
            }
        }
        return cp;
    }


    static SolveResult buildResult(State goalState, int iterations, long elapsed) {
        List<Integer> moves     = new ArrayList<>();
        List<State>   history   = new ArrayList<>();
        State cur = goalState;

        // Trace balik dari goal ke start
        while (cur.parent != null) {
            moves.add(cur.moveDir);
            history.add(cur);
            cur = cur.parent;
        }
        history.add(cur); 
        Collections.reverse(moves);
        Collections.reverse(history);

        return new SolveResult(moves, goalState.g, iterations, elapsed, history);
    }
}