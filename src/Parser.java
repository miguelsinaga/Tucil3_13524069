package  src;
import java.io.*;
import java.util.*;

public class Parser {
    public static Board parse(String filepath) throws IOException {
        File file = new File(filepath);
        if (!file.exists()) {
            throw new IOException("File tidak ditemukan: " + filepath);
        }
        if (!filepath.toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("File harus dalam bentuk .txt");
        }

        List<String> lines = readNonEmptyLines(file);
        String[] dims = lines.get(0).trim().split("\\s+");
        if (dims.length < 2) {
            throw new IllegalArgumentException(
                "Baris pertama harus berisi dua angka, contoh: '7 7'");
        }

        int N, M;
        try {
            N = Integer.parseInt(dims[0]);
            M = Integer.parseInt(dims[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Dimensi papan tidak valid. Harus berupa bilangan bulat positif.");
        }

        if (N <= 0 || M <= 0) {
            throw new IllegalArgumentException(
                "Dimensi papan harus positif. Diberikan: N=" + N + ", M=" + M);
        }
        int expectedLines = 1 + N + N;
        if (lines.size() < expectedLines) {
            throw new IllegalArgumentException(
                "File terlalu pendek. Dibutuhkan " + expectedLines +
                " baris (1 dimensi + " + N + " map + " + N + " cost), " +
                "ditemukan " + lines.size() + " baris.");
        }

        char[][] grid = new char[N][M];
        for (int i = 0; i < N; i++) {
            String row = lines.get(1 + i);
            String rowClean = row.replaceAll("\\s+", "");
            if (rowClean.length() != M) {
                throw new IllegalArgumentException(
                    "Baris map ke-" + (i + 1) + " memiliki panjang " +
                    rowClean.length() + ", diharapkan " + M + ".");
            }
            for (int j = 0; j < M; j++) {
                char c = rowClean.charAt(j);
                if (!isValidMapChar(c)) {
                    throw new IllegalArgumentException(
                        "Karakter tidak valid '" + c + "' pada baris " +
                        (i + 1) + " kolom " + (j + 1) + ".");
                }
                grid[i][j] = c;
            }
        }
        int[][] cost = new int[N][M];
        for (int i = 0; i < N; i++) {
            String[] tokens = lines.get(1 + N + i).trim().split("\\s+");
            if (tokens.length != M) {
                throw new IllegalArgumentException(
                    "Baris cost ke-" + (i + 1) + " memiliki " + tokens.length +
                    " nilai, diharapkan " + M + ".");
            }
            for (int j = 0; j < M; j++) {
                try {
                    cost[i][j] = Integer.parseInt(tokens[j]);
                    if (cost[i][j] < 0) {
                        throw new IllegalArgumentException(
                            "Cost tidak boleh negatif pada baris cost " +
                            (i + 1) + " kolom " + (j + 1) + ".");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "Cost tidak valid '" + tokens[j] + "' pada baris cost " +
                        (i + 1) + " kolom " + (j + 1) + ".");
                }
            }
        }
        validateGrid(grid, N, M);

        return new Board(N, M, grid, cost);
    }
    private static List<String> readNonEmptyLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private static boolean isValidMapChar(char c) {
        return c == '*' || c == 'X' || c == 'L' ||
               c == 'Z' || c == 'O' ||
               (c >= '0' && c <= '9');
    }

    private static void validateGrid(char[][] grid, int N, int M) {
        int countZ = 0;
        int countO = 0;
        boolean[] checkpointFound = new boolean[10]; // indeks 0..9

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                char c = grid[i][j];
                if (c == 'Z') countZ++;
                if (c == 'O') countO++;
                if (c >= '0' && c <= '9') {
                    checkpointFound[c - '0'] = true;
                }
            }
        }

        if (countZ != 1) {
            throw new IllegalArgumentException(
                "Papan harus memiliki tepat satu 'Z' (posisi awal). " +
                "Ditemukan: " + countZ);
        }
        if (countO != 1) {
            throw new IllegalArgumentException(
                "Papan harus memiliki tepat satu 'O' (titik tujuan). " +
                "Ditemukan: " + countO);
        }
        int maxCheckpoint = -1;
        for (int d = 9; d >= 0; d--) {
            if (checkpointFound[d]) {
                maxCheckpoint = d;
                break;
            }
        }
        if (maxCheckpoint >= 0) {
            for (int d = 0; d <= maxCheckpoint; d++) {
                if (!checkpointFound[d]) {
                    throw new IllegalArgumentException(
                        "Checkpoint tidak urut: angka " + d +
                        " tidak ditemukan di papan, padahal angka " +
                        maxCheckpoint + " ada.");
                }
            }
        }
    }

    // buat debug
    public static void printSummary(Board board) {
        System.out.println("Ukuran papan : " + board.getN() + " x " + board.getM());
        System.out.println("Posisi awal  : " + board.getStart());
        System.out.println("Posisi tujuan: " + board.getGoal());
        System.out.println("Jumlah checkpoint: " + board.getNumCheckpoints());
        System.out.println("Grid:");
        board.printGrid(board.getGrid());
    }
}