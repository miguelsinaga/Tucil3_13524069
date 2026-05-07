package src;

import java.util.List;

public class SolveResult {

    public final boolean found;           
    public final List<Integer> moves;     
    public final double totalCost;        
    public final int iterations;          
    public final long timeMs;             
    public final List<State> stateHistory;

    public SolveResult(List<Integer> moves, double totalCost,
                       int iterations, long timeMs, List<State> stateHistory) {
        this.found        = true;
        this.moves        = moves;
        this.totalCost    = totalCost;
        this.iterations   = iterations;
        this.timeMs       = timeMs;
        this.stateHistory = stateHistory;
    }

    public SolveResult(int iterations, long timeMs) {
        this.found        = false;
        this.moves        = null;
        this.totalCost    = 0;
        this.iterations   = iterations;
        this.timeMs       = timeMs;
        this.stateHistory = null;
    }

    public String movesToString() {
        if (moves == null) return "-";
        StringBuilder sb = new StringBuilder();
        for (int dir : moves) {
            sb.append(Board.DIR_CHAR[dir]);
        }
        return sb.toString();
    }
}