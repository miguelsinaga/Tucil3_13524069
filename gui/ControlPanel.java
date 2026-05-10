package gui;

import src.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * ControlPanel.java
 * -----------------
 * Panel kiri GUI. Berisi:
 * - Input path file puzzle (text field + tombol browse)
 * - Pilihan algoritma (UCS / GBFS / A*)
 * - Pilihan heuristik (muncul hanya untuk GBFS dan A*)
 * - Tombol "Solve"
 * - Ringkasan info puzzle yang dimuat
 *
 * Setelah solve, meneruskan SolveResult ke listener (MainWindow).
 */
public class ControlPanel extends JPanel {

    // ── Callback interface ────────────────────────────────────────────────────
    public interface SolveListener {
        void onBoardLoaded(Board board);
        void onSolveResult(Board board, SolveResult result, int algo, int hChoice);
        void onError(String message);
    }

    private SolveListener listener;

    // ── Komponen UI ───────────────────────────────────────────────────────────
    private JTextField pathField;
    private JComboBox<String> algoCombo;
    private JComboBox<String> heuristicCombo;
    private JLabel heuristicLabel;
    private UI.PastelButton solveBtn;
    private UI.PastelButton browseBtn;
    private JLabel infoLabel;
    private JLabel statusLabel;
    private UI.Card infoCard;

    // ── State ─────────────────────────────────────────────────────────────────
    private Board currentBoard = null;

    public ControlPanel(SolveListener listener) {
        this.listener = listener;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD / 2));
        buildUI();
    }

    private void buildUI() {
        // ── Judul ─────────────────────────────────────────────────────────────
        JLabel title = new JLabel("Ice Sliding");
        title.setFont(Theme.fontDisplay(22f));
        title.setForeground(Theme.PURPLE());
        title.setAlignmentX(LEFT_ALIGNMENT);
        add(title);

        JLabel subtitle = UI.makeSubLabel("Puzzle Solver", 12f);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        add(subtitle);
        add(UI.vSpace(16));
        add(new UI.Divider());
        add(UI.vSpace(14));

        // ── File input ────────────────────────────────────────────────────────
        JLabel fileLabel = UI.makeLabel("File Puzzle", 12f, true);
        fileLabel.setForeground(Theme.TEXT_SUB());
        fileLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(fileLabel);
        add(UI.vSpace(5));

        JPanel fileRow = new JPanel(new BorderLayout(6, 0));
        fileRow.setOpaque(false);
        fileRow.setAlignmentX(LEFT_ALIGNMENT);
        fileRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        pathField = UI.makeField("Path file .txt ...");
        pathField.setPreferredSize(new Dimension(0, 34));
        fileRow.add(pathField, BorderLayout.CENTER);

        browseBtn = new UI.PastelButton("Browse", Theme.PURPLE());
        browseBtn.setPreferredSize(new Dimension(80, 34));
        browseBtn.addActionListener(e -> browseFile());
        fileRow.add(browseBtn, BorderLayout.EAST);

        add(fileRow);
        add(UI.vSpace(8));

        // Tombol Load
        UI.PastelButton loadBtn = new UI.PastelButton("Load Puzzle", Theme.TEAL());
        loadBtn.setAlignmentX(LEFT_ALIGNMENT);
        loadBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loadBtn.addActionListener(e -> loadPuzzle());
        add(loadBtn);
        add(UI.vSpace(12));

        // ── Info card ─────────────────────────────────────────────────────────
        infoCard = new UI.Card(Theme.SURFACE2(), false);
        infoCard.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        infoLabel = UI.makeSubLabel("Belum ada puzzle dimuat", 11f);
        infoLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(infoLabel);
        add(infoCard);
        add(UI.vSpace(16));
        add(new UI.Divider());
        add(UI.vSpace(14));

        // ── Pilih algoritma ───────────────────────────────────────────────────
        JLabel algoLabel = UI.makeLabel("Algoritma", 12f, true);
        algoLabel.setForeground(Theme.TEXT_SUB());
        algoLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(algoLabel);
        add(UI.vSpace(5));

        String[] algos = {"UCS — Uniform Cost Search",
                          "GBFS — Greedy Best-First Search",
                          "A* — A-Star Search"};
        algoCombo = UI.makeCombo(algos);
        algoCombo.setAlignmentX(LEFT_ALIGNMENT);
        algoCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        algoCombo.addActionListener(e -> updateHeuristicVisibility());
        add(algoCombo);
        add(UI.vSpace(10));

        // ── Pilih heuristik ───────────────────────────────────────────────────
        heuristicLabel = UI.makeLabel("Heuristik", 12f, true);
        heuristicLabel.setForeground(Theme.TEXT_SUB());
        heuristicLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(heuristicLabel);
        add(UI.vSpace(5));

        String[] heuristics = {"H1 — Manhattan Distance",
                               "H2 — Chebyshev Distance",
                               "H3 — Minimum Moves"};
        heuristicCombo = UI.makeCombo(heuristics);
        heuristicCombo.setAlignmentX(LEFT_ALIGNMENT);
        heuristicCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        add(heuristicCombo);
        add(UI.vSpace(16));

        // Sembunyikan heuristik di awal (UCS tidak butuh)
        updateHeuristicVisibility();

        // ── Tombol Solve ──────────────────────────────────────────────────────
        solveBtn = new UI.PastelButton("Solve", Theme.PINK());
        solveBtn.setFont(Theme.fontBodyBold(14f));
        solveBtn.setAlignmentX(LEFT_ALIGNMENT);
        solveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        solveBtn.addActionListener(e -> solve());
        add(solveBtn);
        add(UI.vSpace(10));

        // ── Status label ──────────────────────────────────────────────────────
        statusLabel = UI.makeSubLabel("", 11f);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(statusLabel);

        add(Box.createVerticalGlue()); // dorong semua ke atas
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────────────────

    private void browseFile() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setDialogTitle("Pilih file puzzle (.txt)");
        int ret = fc.showOpenDialog(SwingUtilities.getWindowAncestor(this));
        if (ret == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
            loadPuzzle();
        }
    }

    private void loadPuzzle() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            showStatus("Masukkan path file terlebih dahulu.", Theme.WARNING());
            return;
        }
        try {
            currentBoard = Parser.parse(path);
            int[] s = currentBoard.getStart();
            int[] g = currentBoard.getGoal();
            infoLabel.setText("<html>"
                + "<b>" + currentBoard.getN() + " × " + currentBoard.getM() + "</b>"
                + " &nbsp;|&nbsp; Start (" + s[0] + "," + s[1] + ")"
                + " &nbsp;|&nbsp; Goal (" + g[0] + "," + g[1] + ")"
                + " &nbsp;|&nbsp; CP: " + currentBoard.getNumCheckpoints()
                + "</html>");
            showStatus("Puzzle berhasil dimuat ✓", Theme.SUCCESS());
            if (listener != null) listener.onBoardLoaded(currentBoard);
        } catch (Exception ex) {
            showStatus("Error: " + ex.getMessage(), Theme.ERROR());
            if (listener != null) listener.onError(ex.getMessage());
        }
    }

    private void solve() {
        if (currentBoard == null) {
            showStatus("Muat puzzle terlebih dahulu.", Theme.WARNING());
            return;
        }

        int algoIdx = algoCombo.getSelectedIndex(); // 0=UCS, 1=GBFS, 2=A*
        int hChoice = heuristicCombo.getSelectedIndex() + 1; // 1=H1, 2=H2, 3=H3

        solveBtn.setEnabled(false);
        showStatus("Mencari solusi...", Theme.AMBER());

        Board boardCopy = currentBoard;
        SwingWorker<SolveResult, Void> worker = new SwingWorker<>() {
            @Override
            protected SolveResult doInBackground() {
                switch (algoIdx) {
                    case 0: return UCS.solve(boardCopy);
                    case 1: return GBFS.solve(boardCopy, hChoice);
                    case 2: return AStar.solve(boardCopy, hChoice);
                    default: return UCS.solve(boardCopy);
                }
            }

            @Override
            protected void done() {
                solveBtn.setEnabled(true);
                try {
                    SolveResult result = get();
                    if (result.found) {
                        showStatus("Solusi ditemukan ✓  (" + result.iterations + " iterasi)", Theme.SUCCESS());
                    } else {
                        showStatus("Solusi tidak ditemukan ✗", Theme.ERROR());
                    }
                    if (listener != null)
                        listener.onSolveResult(boardCopy, result, algoIdx + 1, hChoice);
                } catch (Exception ex) {
                    showStatus("Error: " + ex.getMessage(), Theme.ERROR());
                }
            }
        };
        worker.execute();
    }

    private void updateHeuristicVisibility() {
        boolean needH = algoCombo.getSelectedIndex() > 0;
        heuristicLabel.setVisible(needH);
        heuristicCombo.setVisible(needH);
        revalidate();
        repaint();
    }

    private void showStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // ── Theme update ──────────────────────────────────────────────────────────
    public void applyTheme() {
        UI.applyTheme(this);
        repaint();
    }
}