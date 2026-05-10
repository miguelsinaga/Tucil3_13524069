package gui;

import src.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class ResultPanel extends JPanel {

    private BoardPanel boardPanel;
    private Board board;
    private SolveResult result;
    private int algoChoice, hChoice;

    private int currentStep = 0; // indeks di result.stateHistory
    private boolean playing = false;
    private Timer playTimer;
    private int playDelay = 600; // ms antar langkah


    private JLabel algoTag, costVal, iterVal, timeVal, movesVal;
    private JLabel stepLabel;
    private UI.PastelButton btnPlay, btnReset;
    private JSlider speedSlider;
    private JTextArea movesArea;
    private UI.Card statsCard, playbackCard;

    public ResultPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(
            Theme.PAD, Theme.PAD / 2, Theme.PAD, Theme.PAD));
        buildUI();
        showEmpty();
    }

    private void buildUI() {
        JLabel title = UI.makeLabel("Hasil Pencarian", 13f, true);
        title.setAlignmentX(LEFT_ALIGNMENT);
        add(title);
        add(UI.vSpace(8));

        statsCard = new UI.Card();
        statsCard.setAlignmentX(LEFT_ALIGNMENT);
        statsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));
        statsCard.setLayout(new GridBagLayout());
        statsCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 0, 3, 12);


        gbc.gridx = 0; gbc.gridy = 0;
        statsCard.add(UI.makeSubLabel("Algoritma", 11f), gbc);
        gbc.gridx = 1;
        algoTag = new JLabel("—");
        algoTag.setFont(Theme.fontBodyBold(12f));
        algoTag.setForeground(Theme.PURPLE());
        statsCard.add(algoTag, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        statsCard.add(UI.makeSubLabel("Total cost", 11f), gbc);
        gbc.gridx = 1; costVal = statVal("—"); statsCard.add(costVal, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        statsCard.add(UI.makeSubLabel("Iterasi", 11f), gbc);
        gbc.gridx = 1; iterVal = statVal("—"); statsCard.add(iterVal, gbc);


        gbc.gridx = 0; gbc.gridy = 3;
        statsCard.add(UI.makeSubLabel("Waktu", 11f), gbc);
        gbc.gridx = 1; timeVal = statVal("—"); statsCard.add(timeVal, gbc);

        add(statsCard);
        add(UI.vSpace(8));


        JLabel movesTitle = UI.makeLabel("Urutan Gerakan", 12f, true);
        movesTitle.setAlignmentX(LEFT_ALIGNMENT);
        add(movesTitle);
        add(UI.vSpace(5));

        movesArea = new JTextArea(2, 20);
        movesArea.setFont(Theme.fontMono(13f));
        movesArea.setForeground(Theme.TEXT());
        movesArea.setBackground(Theme.SURFACE2());
        movesArea.setLineWrap(true);
        movesArea.setWrapStyleWord(false);
        movesArea.setEditable(false);
        movesArea.setBorder(BorderFactory.createCompoundBorder(
            new UI.RoundBorder(Theme.RADIUS_SM),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JScrollPane movesScroll = new JScrollPane(movesArea);
        movesScroll.setAlignmentX(LEFT_ALIGNMENT);
        movesScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        movesScroll.setBorder(null);
        movesScroll.setOpaque(false);
        movesScroll.getViewport().setOpaque(false);
        add(movesScroll);
        add(UI.vSpace(12));
        add(new UI.Divider());
        add(UI.vSpace(10));


        JLabel pbTitle = UI.makeLabel("Playback Animasi", 13f, true);
        pbTitle.setAlignmentX(LEFT_ALIGNMENT);
        add(pbTitle);
        add(UI.vSpace(8));

        playbackCard = new UI.Card();
        playbackCard.setAlignmentX(LEFT_ALIGNMENT);
        playbackCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        playbackCard.setLayout(new BoxLayout(playbackCard, BoxLayout.Y_AXIS));
        playbackCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        stepLabel = UI.makeSubLabel("Langkah 0 / 0", 12f);
        stepLabel.setAlignmentX(LEFT_ALIGNMENT);
        playbackCard.add(stepLabel);
        playbackCard.add(UI.vSpace(8));


        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnPlay  = new UI.PastelButton("Play",  Theme.TEAL());
        btnReset = new UI.PastelButton("Reset", Theme.SURFACE2());

        btnPlay.setToolTipText("Mainkan animasi dari awal");
        btnReset.setToolTipText("Kembali ke posisi awal");

        btnPlay.addActionListener(e -> togglePlay());
        btnReset.addActionListener(e -> resetPlayback());

        btnRow.add(btnPlay);
        btnRow.add(btnReset);
        playbackCard.add(btnRow);
        playbackCard.add(UI.vSpace(8));

        JPanel speedRow = new JPanel(new BorderLayout(8, 0));
        speedRow.setOpaque(false);
        speedRow.setAlignmentX(LEFT_ALIGNMENT);
        speedRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        speedRow.add(UI.makeSubLabel("Lambat", 10f), BorderLayout.WEST);

        speedSlider = new JSlider(100, 1200, playDelay);
        speedSlider.setOpaque(false);
        speedSlider.setInverted(true); // geser ke kanan = lebih cepat
        speedSlider.addChangeListener(e -> {
            playDelay = speedSlider.getValue();
            if (playTimer != null && playTimer.isRunning()) {
                playTimer.setDelay(playDelay);
            }
        });
        speedRow.add(speedSlider, BorderLayout.CENTER);
        speedRow.add(UI.makeSubLabel("Cepat", 10f), BorderLayout.EAST);
        playbackCard.add(speedRow);

        add(playbackCard);
        add(Box.createVerticalGlue());

        setPlaybackEnabled(false);
    }


    public void setResult(Board board, SolveResult result, int algoChoice, int hChoice) {
        this.board = board;
        this.result = result;
        this.algoChoice = algoChoice;
        this.hChoice = hChoice;

        stopPlay();
        currentStep = 0;

        if (!result.found) {
            algoTag.setText(algoName(algoChoice));
            costVal.setText("—");
            iterVal.setText(String.valueOf(result.iterations));
            timeVal.setText(formatTime(result.timeMs));
            movesArea.setText("Tidak ada solusi");
            movesArea.setForeground(Theme.ERROR());
            setPlaybackEnabled(false);
            updateStepLabel();
            return;
        }

        // Isi statistik
        algoTag.setText(algoName(algoChoice)
            + (algoChoice != 1 ? " / " + Heuristic.name(hChoice) : ""));
        costVal.setText(String.valueOf((int) result.totalCost));
        iterVal.setText(String.valueOf(result.iterations));
        timeVal.setText(formatTime(result.timeMs));

        movesArea.setForeground(Theme.TEXT());
        movesArea.setText(result.movesToString());

        setPlaybackEnabled(true);
        updateStepLabel();
        showCurrentStep();
    }

    private void showEmpty() {
        movesArea.setText("—");
        stepLabel.setText("Posisi Awal");
        setPlaybackEnabled(false);
    }


    // Satu langkah animasi — dipanggil oleh timer, bukan oleh user
    private void doStep() {
        if (result == null || !result.found) return;
        if (currentStep >= result.stateHistory.size() - 1) return;

        List<State> history = result.stateHistory;
        State nextState = history.get(currentStep + 1);
        int dir = result.moves.get(currentStep);
        State cur = history.get(currentStep);
        Board.SlideResult slide = board.slide(cur.row, cur.col, dir, cur.nextCheckpoint);
        int[] tRow = slide.valid ? slide.tilesRow : new int[0];
        int[] tCol = slide.valid ? slide.tilesCol : new int[0];
        currentStep++;
        updateStepLabel();
        boardPanel.animateTo(nextState, tRow, tCol, null);
    }

    private void togglePlay() {
        if (playing) {
            stopPlay();
            btnPlay.setText("Play");
            btnPlay.setColor(Theme.TEAL());
        } else {
            // Selalu mulai dari awal
            currentStep = 0;
            showCurrentStep();
            updateStepLabel();
            startPlay();
            btnPlay.setText("Pause");
            btnPlay.setColor(Theme.AMBER());
        }
    }

    private void startPlay() {
        if (result == null || !result.found) return;
        playing = true;
        playTimer = new Timer(playDelay, null);
        playTimer.addActionListener(e -> {
            if (currentStep >= result.stateHistory.size() - 1) {
                stopPlay();
                btnPlay.setText("Play");
                btnPlay.setColor(Theme.TEAL());
                return;
            }
            doStep();
        });
        playTimer.start();
    }

    private void stopPlay() {
        playing = false;
        if (playTimer != null) { playTimer.stop(); playTimer = null; }
    }

    private void resetPlayback() {
        stopPlay();
        btnPlay.setText("Play");
        btnPlay.setColor(Theme.TEAL());
        currentStep = 0;
        updateStepLabel();
        if (board != null) boardPanel.reset();
    }

    private void showCurrentStep() {
        if (result == null || !result.found) return;
        boardPanel.setActorState(result.stateHistory.get(currentStep));
    }

    private void updateStepLabel() {
        int total = (result != null && result.found) ? result.stateHistory.size() - 1 : 0;
        if (total == 0 || currentStep == 0) {
            stepLabel.setText("Posisi Awal");
        } else if (currentStep >= total) {
            stepLabel.setText("Langkah " + total + " / " + total);
        } else {
            stepLabel.setText("Langkah " + currentStep + " / " + total);
        }
    }

    private JLabel statVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.fontBodyBold(13f));
        l.setForeground(Theme.TEXT());
        return l;
    }

    private void setPlaybackEnabled(boolean enabled) {
        btnPlay.setEnabled(enabled);
        btnReset.setEnabled(enabled);
        speedSlider.setEnabled(enabled);
    }

    private String algoName(int a) {
        switch (a) {
            case 1: return "UCS";
            case 2: return "GBFS";
            case 3: return "A*";
            default: return "?";
        }
    }

    private String formatTime(long ms) {
        return ms == 0 ? "< 1 ms" : ms + " ms";
    }

    public void applyTheme() {
        movesArea.setBackground(Theme.SURFACE2());
        movesArea.setForeground(Theme.TEXT());
        movesArea.setCaretColor(Theme.PURPLE());
        UI.applyTheme(this);
        repaint();
    }
}