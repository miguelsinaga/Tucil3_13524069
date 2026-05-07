package src;

public class State implements Comparable<State> {

    public final int row;
    public final int col;
    public final int nextCheckpoint; 
    public double g; 
    public double h; 
    public double f; 
    public State parent;    
    public int   moveDir; 

    public State(int row, int col, int nextCheckpoint) {
        this.row = row;
        this.col = col;
        this.nextCheckpoint = nextCheckpoint;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.parent = null;
        this.moveDir = -1;
    }

    public State(int row, int col, int nextCheckpoint, double g, double h,
                 State parent, int moveDir) {
        this.row = row;
        this.col = col;
        this.nextCheckpoint = nextCheckpoint;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.parent = parent;
        this.moveDir = moveDir;
    }
    @Override
    public int compareTo(State other) {
        return Double.compare(this.f, other.f);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof State)) return false;
        State other = (State) obj;
        return this.row == other.row &&
               this.col == other.col &&
               this.nextCheckpoint == other.nextCheckpoint;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        result = 31 * result + nextCheckpoint;
        return result;
    }

    @Override
    public String toString() {
        return "State(row=" + row + ", col=" + col +
               ", nextCP=" + nextCheckpoint +
               ", g=" + g + ", h=" + h + ", f=" + f + ")";
    }
}