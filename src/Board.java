package src;
public class Board {
    public static final int UP    = 0;
    public static final int DOWN  = 1;
    public static final int LEFT  = 2;
    public static final int RIGHT = 3;

    public static final char[] DIR_CHAR = {'U', 'D', 'L', 'R'};
    private static final int[] DR = {-1, 1,  0, 0};
    private static final int[] DC = { 0, 0, -1, 1};
    private final int N;           
    private final int M;           
    private final char[][] grid;   
    private final int[][] cost;    
    private final int[] start;     
    private final int[] goal;      
    private final int numCheckpoints; 
    public Board(int N, int M, char[][] grid, int[][] cost) {
        this.N = N;
        this.M = M;
        this.grid = grid;
        this.cost = cost;

        int[] foundStart = null;
        int[] foundGoal  = null;
        int maxCheckpoint = -1;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                char c = grid[i][j];
                if (c == 'Z') foundStart = new int[]{i, j};
                if (c == 'O') foundGoal  = new int[]{i, j};
                if (c >= '0' && c <= '9') {
                    int digit = c - '0';
                    if (digit > maxCheckpoint) maxCheckpoint = digit;
                }
            }
        }

        this.start = foundStart;
        this.goal  = foundGoal;
        this.numCheckpoints = maxCheckpoint + 1; 
    }

    

    public static class SlideResult {
        public final boolean valid;      
        public final int endRow;         
        public final int endCol;
        public final int moveCost;       
        public final int nextCheckpoint; 
        
        public final int[] tilesRow;     
        public final int[] tilesCol;     
        public SlideResult() {
            this.valid = false;
            this.endRow = -1;
            this.endCol = -1;
            this.moveCost = 0;
            this.nextCheckpoint = -1;
            this.tilesRow = new int[0];
            this.tilesCol = new int[0];
        }

        public SlideResult(int endRow, int endCol, int moveCost,
                           int[] tilesRow, int[] tilesCol) {
            this.valid = true;
            this.endRow = endRow;
            this.endCol = endCol;
            this.moveCost = moveCost;
            this.nextCheckpoint = -1; 
            this.tilesRow = tilesRow;
            this.tilesCol = tilesCol;
        }
    }
    public SlideResult slide(int startRow, int startCol, int dir, int nextNeeded) {
        int dr = DR[dir];
        int dc = DC[dir];

        int curRow = startRow;
        int curCol = startCol;

        int totalCost = 0;
        int stepCount = 0;
        int maxSteps = N * M; 
        int[] tRow = new int[maxSteps];
        int[] tCol = new int[maxSteps];

        int currentNextNeeded = nextNeeded; 

        while (true) {
            int nextRow = curRow + dr;
            int nextCol = curCol + dc;
            if (nextRow < 0 || nextRow >= N || nextCol < 0 || nextCol >= M) {
                return new SlideResult();
            }

            char nextTile = grid[nextRow][nextCol];
            if (nextTile == 'X') {
                if (stepCount == 0) return new SlideResult();
                int[] tr = java.util.Arrays.copyOf(tRow, stepCount);
                int[] tc = java.util.Arrays.copyOf(tCol, stepCount);
                return new SlideResult(curRow, curCol, totalCost, tr, tc);
            }
            if (nextTile == 'L') {
                return new SlideResult();
            }
            if (nextTile >= '0' && nextTile <= '9') {
                int checkpointNum = nextTile - '0';
                if (currentNextNeeded != -1 && checkpointNum != currentNextNeeded) {
                    return new SlideResult();
                }

                
                if (currentNextNeeded != -1 && checkpointNum == currentNextNeeded) {
                    currentNextNeeded++;
                    if (currentNextNeeded >= numCheckpoints) {
                        currentNextNeeded = -1; // semua checkpoint selesai
                    }
                }
            }
            curRow = nextRow;
            curCol = nextCol;
            totalCost += cost[curRow][curCol];
            tRow[stepCount] = curRow;
            tCol[stepCount] = curCol;
            stepCount++;
            if (nextTile == 'O') {
                int[] tr = java.util.Arrays.copyOf(tRow, stepCount);
                int[] tc = java.util.Arrays.copyOf(tCol, stepCount);
                return new SlideResult(curRow, curCol, totalCost, tr, tc);
            }
        }
    }
    public void printGrid(char[][] baseGrid) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                System.out.print(baseGrid[i][j]);
            }
            System.out.println();
        }
    }
    public char[][] buildDisplayGrid(int actorRow, int actorCol, int nextNeeded) {
        char[][] display = new char[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                char c = grid[i][j];
                if (c >= '0' && c <= '9') {
                    int digit = c - '0';
                    boolean visited = (nextNeeded == -1) || (digit < nextNeeded);
                    display[i][j] = visited ? '*' : c;
                } else if (c == 'Z') {
                    display[i][j] = '*';
                } else {
                    display[i][j] = c;
                }
            }
        }
        display[actorRow][actorCol] = 'Z';
        return display;
    }
    public int getN() { return N; }
    public int getM() { return M; }
    public char[][] getGrid() { return grid; }
    public int[][] getCost() { return cost; }
    public int[] getStart() { return start; }
    public int[] getGoal() { return goal; }
    public int getNumCheckpoints() { return numCheckpoints; }

    public char getCell(int r, int c) { return grid[r][c]; }
    public int getCellCost(int r, int c) { return cost[r][c]; }
}