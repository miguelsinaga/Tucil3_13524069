package src;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean continueProgram = true;

        printBanner();

        while (continueProgram) {
            System.out.println();

            
            Board board = null;
            while (board == null) {
                System.out.print("Masukkan path file puzzle (.txt): ");
                String path = sc.nextLine().trim();
                try {
                    board = Parser.parse(path);
                    System.out.println("File berhasil dibaca.");
                    Parser.printSummary(board);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("Silakan coba lagi.");
                }
            }

            int algoChoice = 0;
            while (algoChoice < 1 || algoChoice > 3) {
                System.out.println("\nPilih algoritma:");
                System.out.println("  1. UCS  (Uniform Cost Search)");
                System.out.println("  2. GBFS (Greedy Best-First Search)");
                System.out.println("  3. A*   (A-Star Search)");
                System.out.print("Pilihan (1-3): ");
                try {
                    algoChoice = Integer.parseInt(sc.nextLine().trim());
                    if (algoChoice < 1 || algoChoice > 3)
                        System.out.println("Input tidak valid. Masukkan 1, 2, atau 3.");
                } catch (NumberFormatException e) {
                    System.out.println("Input tidak valid. Masukkan angka 1-3.");
                }
            }

            int hChoice = Heuristic.H1; // default
            if (algoChoice == 2 || algoChoice == 3) {
                int hInput = 0;
                while (hInput < 1 || hInput > 3) {
                    System.out.println("\nPilih heuristik:");
                    System.out.println("  1. H1 — Manhattan Distance");
                    System.out.println("  2. H2 — Chebyshev Distance");
                    System.out.println("  3. H3 — Minimum Moves (jumlah gerakan)");
                    System.out.print("Pilihan (1-3): ");
                    try {
                        hInput = Integer.parseInt(sc.nextLine().trim());
                        if (hInput < 1 || hInput > 3)
                            System.out.println("Input tidak valid. Masukkan 1, 2, atau 3.");
                    } catch (NumberFormatException e) {
                        System.out.println("Input tidak valid. Masukkan angka 1-3.");
                    }
                }
                hChoice = hInput;
            }

        
            System.out.println("\nMencari solusi...");
            SolveResult result = runSolver(board, algoChoice, hChoice);

            System.out.println();
            if (!result.found) {
                System.out.println("Solusi tidak ditemukan.");
                System.out.println("Iterasi   : " + result.iterations);
                System.out.println("Waktu     : " + result.timeMs + " ms");
            } else {
                printResultSummary(result, algoChoice, hChoice);
                System.out.println();
                System.out.print("Tampilkan playback solusi? (y/n): ");
                String playbackChoice = sc.nextLine().trim().toLowerCase();
                if (playbackChoice.equals("y")) {
                    System.out.println();
                    Playback.play(board, result);
                }

            
                System.out.print("\nSimpan output ke file .txt? (y/n): ");
                String saveChoice = sc.nextLine().trim().toLowerCase();
                if (saveChoice.equals("y")) {
                    System.out.print("Nama file output (contoh: output.txt): ");
                    String outPath = sc.nextLine().trim();
                    saveOutput(outPath, board, result, algoChoice, hChoice);
                }
            }

            System.out.print("\nJalankan lagi? (y/n): ");
            String again = sc.nextLine().trim().toLowerCase();
            continueProgram = again.equals("y");
        }

        System.out.println("\nTerima kasih!");
        sc.close();
    }

    private static SolveResult runSolver(Board board, int algoChoice, int hChoice) {
        switch (algoChoice) {
            case 1: return UCS.solve(board);
            case 2: return GBFS.solve(board, hChoice);
            case 3: return AStar.solve(board, hChoice);
            default: throw new IllegalArgumentException("Algoritma tidak valid");
        }
    }

    private static void printResultSummary(SolveResult result, int algoChoice, int hChoice) {
        System.out.println("=".repeat(40));
        System.out.println("            HASIL PENCARIAN");
        System.out.println("=".repeat(40));
        System.out.println("Algoritma      : " + algoName(algoChoice));
        if (algoChoice != 1) {
            System.out.println("Heuristik      : " + Heuristic.name(hChoice));
        }
        System.out.println("Status         : Solusi ditemukan");
        System.out.println("Urutan gerakan : " + result.movesToString());
        System.out.println("Total cost     : " + (int) result.totalCost);
        System.out.println("Iterasi        : " + result.iterations);
        System.out.println("Waktu eksekusi : " + formatTime(result.timeMs));
        System.out.println("=".repeat(40));
    }

    private static void saveOutput(String path, Board board, SolveResult result,
                                   int algoChoice, int hChoice) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("=".repeat(40));
            pw.println("     ICE SLIDING PUZZLE — OUTPUT");
            pw.println("=".repeat(40));
            pw.println("Algoritma      : " + algoName(algoChoice));
            if (algoChoice != 1) {
                pw.println("Heuristik      : " + Heuristic.name(hChoice));
            }
            pw.println();

            if (!result.found) {
                pw.println("Status: Solusi tidak ditemukan.");
            } else {
                pw.println("Status         : Solusi ditemukan");
                pw.println("Urutan gerakan : " + result.movesToString());
                pw.println("Total cost     : " + (int) result.totalCost);
                pw.println("Iterasi        : " + result.iterations);
                pw.println("Waktu eksekusi : " + formatTime(result.timeMs));
                pw.println();

                pw.println("--- Detail Langkah ---");
                java.util.List<State> history = result.stateHistory;
                java.util.List<Integer> moves = result.moves;

                pw.println("[Posisi Awal]");
                State init = history.get(0);
                char[][] initGrid = board.buildDisplayGrid(
                    init.row, init.col, init.nextCheckpoint);
                writeGrid(pw, initGrid);

                for (int i = 0; i < moves.size(); i++) {
                    State prev = history.get(i);
                    State curr = history.get(i + 1);
                    double stepCost = curr.g - prev.g;
                    pw.println("\nLangkah " + (i + 1) + ": "
                        + Board.DIR_CHAR[moves.get(i)]
                        + "  (cost langkah: " + (int) stepCost
                        + ", total: " + (int) curr.g + ")");
                    char[][] grid = board.buildDisplayGrid(
                        curr.row, curr.col, curr.nextCheckpoint);
                    writeGrid(pw, grid);
                }
            }

            System.out.println("Output berhasil disimpan ke: " + path);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file: " + e.getMessage());
        }
    }

    private static void writeGrid(PrintWriter pw, char[][] grid) {
        for (char[] row : grid) {
            pw.println(new String(row));
        }
    }
    private static String formatTime(long ms) {
        if (ms == 0) return "< 1 ms";
        return ms + " ms";
    }

    private static String algoName(int choice) {
        switch (choice) {
            case 1: return "UCS (Uniform Cost Search)";
            case 2: return "GBFS (Greedy Best-First Search)";
            case 3: return "A* (A-Star Search)";
            default: return "Unknown";
        }
    }

    private static void printBanner() {
        System.out.println("========================================");
        System.out.println("    ICE SLIDING PUZZLE SOLVER");
        System.out.println("    Tugas Kecil 3 — IF2211 STIMA");
        System.out.println("========================================");
    }
}