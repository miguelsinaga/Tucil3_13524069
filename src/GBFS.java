package src;

import java.util.*;
public class GBFS {
    public static SolveResult solve(Board board, int hChoice) {
        long startTime = System.currentTimeMillis();

        int[] start = board.getStart();
        int[] goal  = board.getGoal();
        int initialCP = (board.getNumCheckpoints() > 0) ? 0 : -1;

        State initState = new State(start[0], start[1], initialCP);
        initState.g = 0;
        initState.h = Heuristic.compute(initState, board, hChoice);
        initState.f = initState.h; 

        PriorityQueue<State> pq = new PriorityQueue<>();
        pq.offer(initState);
        Set<State> visited = new HashSet<>();

        int iterations = 0;

        while (!pq.isEmpty()) {
            State current = pq.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            iterations++;
            if (current.row == goal[0] && current.col == goal[1]
                    && current.nextCheckpoint == -1) {
                long elapsed = System.currentTimeMillis() - startTime;
                return UCS.buildResult(current, iterations, elapsed);
            }

            for (int dir = 0; dir < 4; dir++) {
                Board.SlideResult slide = board.slide(
                    current.row, current.col, dir, current.nextCheckpoint);

                if (!slide.valid) continue;

                int newCP = UCS.computeNewCP(board, current.nextCheckpoint,
                                              slide.tilesRow, slide.tilesCol);

                double newG = current.g + slide.moveCost;
                State next = new State(slide.endRow, slide.endCol, newCP);
                next.g = newG;
                next.h = Heuristic.compute(next, board, hChoice);
                next.f = next.h; // GBFS: f = h (abaikan g)
                next.parent  = current;
                next.moveDir = dir;

                if (!visited.contains(next)) {
                    pq.offer(next);
                }
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return new SolveResult(iterations, elapsed);
    }
}