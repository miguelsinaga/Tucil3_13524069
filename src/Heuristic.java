package src;

public class Heuristic {

    public static final int H1 = 1;
    public static final int H2 = 2;
    public static final int H3 = 3;

    public static double compute(State state, Board board, int hChoice) {
        int[] target = getTarget(state, board);
        int targetRow = target[0];
        int targetCol = target[1];

        int dRow = Math.abs(state.row - targetRow);
        int dCol = Math.abs(state.col - targetCol);

        switch (hChoice) {
            case H1: return manhattan(dRow, dCol);
            case H2: return chebyshev(dRow, dCol);
            case H3: return minMoves(state.row, state.col, targetRow, targetCol);
            default: return manhattan(dRow, dCol);
        }
    }
    private static double manhattan(int dRow, int dCol) {
        return dRow + dCol;
    }
    private static double chebyshev(int dRow, int dCol) {
        return Math.max(dRow, dCol);
    }

    private static double minMoves(int r, int c, int tr, int tc) {
        if (r == tr && c == tc) return 0;          
        if (r == tr || c == tc) return 1;           
        return 2;                                   
    }


    private static int[] getTarget(State state, Board board) {
        if (state.nextCheckpoint != -1 &&
            state.nextCheckpoint < board.getNumCheckpoints()) {
            char target = (char) ('0' + state.nextCheckpoint);
            char[][] grid = board.getGrid();
            for (int i = 0; i < board.getN(); i++) {
                for (int j = 0; j < board.getM(); j++) {
                    if (grid[i][j] == target) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return board.getGoal();
    }
    public static String name(int hChoice) {
        switch (hChoice) {
            case H1: return "H1 (Manhattan Distance)";
            case H2: return "H2 (Chebyshev Distance)";
            case H3: return "H3 (Minimum Moves)";
            default: return "Unknown";
        }
    }
}