package src;

import java.util.List;

public class Playback {

    public static void play(Board board, SolveResult result) {
        if (!result.found) {
            System.out.println("Tidak ada solusi untuk ditampilkan.");
            return;
        }

        List<State> history = result.stateHistory;
        List<Integer> moves = result.moves;

        System.out.println("=".repeat(40));
        System.out.println("         PLAYBACK SOLUSI");
        System.out.println("=".repeat(40));
        State initState = history.get(0);
        System.out.println("\n[Posisi Awal]");
        char[][] displayGrid = board.buildDisplayGrid(
            initState.row, initState.col, initState.nextCheckpoint);
        printGrid(displayGrid);
        System.out.println("Cost: 0");


        for (int i = 0; i < moves.size(); i++) {
            State prev = history.get(i);
            State curr = history.get(i + 1);
            int dir    = moves.get(i);
            double stepCost = curr.g - prev.g;

            System.out.println("\nLangkah " + (i + 1) + ": "
                + Board.DIR_CHAR[dir]
                + "  (cost langkah: " + (int) stepCost
                + ", total: " + (int) curr.g + ")");

            char[][] grid = board.buildDisplayGrid(
                curr.row, curr.col, curr.nextCheckpoint);
            printGrid(grid);
        }
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Urutan gerakan : " + result.movesToString());
        System.out.println("Total cost     : " + (int) result.totalCost);
        System.out.println("Iterasi        : " + result.iterations);
        System.out.println("Waktu eksekusi : " + formatTime(result.timeMs));
        System.out.println("=".repeat(40));
    }

    private static void printGrid(char[][] grid) {
        for (char[] row : grid) {
            for (char c : row) {
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private static String formatTime(long ms) {
        if (ms == 0) return "< 1 ms";
        return ms + " ms";
    }
}