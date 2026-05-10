package gui;

import src.Board;
import src.State;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;


public class BoardPanel extends JPanel {

    private Board board;
    private int actorRow, actorCol;
    private int nextCheckpoint;


    private List<int[]> trail = new ArrayList<>();


    private float animActorX = -1, animActorY = -1; 
    private float targetX, targetY;
    private Timer animTimer;
    private static final int ANIM_STEPS = 12;
    private static final int ANIM_DELAY = 18; 

    
    private int tileSize = 52;
    private int offsetX, offsetY; 

    public BoardPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(420, 420));
    }

   
    public void setBoard(Board board) {
        this.board = board;
        int[] start = board.getStart();
        actorRow = start[0];
        actorCol = start[1];
        nextCheckpoint = board.getNumCheckpoints() > 0 ? 0 : -1;
        trail.clear();
        animActorX = -1;
        recalcLayout();
        repaint();
    }

    public void setActorState(State state) {
        if (board == null) return;
        actorRow = state.row;
        actorCol = state.col;
        nextCheckpoint = state.nextCheckpoint;
        trail.clear();
        animActorX = -1;
        repaint();
    }

    public void animateTo(State nextState, int[] trailRows, int[] trailCols, Runnable onDone) {
        if (board == null) { onDone.run(); return; }

        
        trail.clear();
        if (trailRows != null) {
            for (int i = 0; i < trailRows.length; i++) {
                trail.add(new int[]{trailRows[i], trailCols[i]});
            }
        }


        recalcLayout();
        float startPX = offsetX + actorCol * tileSize + tileSize / 2f;
        float startPY = offsetY + actorRow * tileSize + tileSize / 2f;
        float endPX   = offsetX + nextState.col * tileSize + tileSize / 2f;
        float endPY   = offsetY + nextState.row * tileSize + tileSize / 2f;

        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        animActorX = startPX;
        animActorY = startPY;
        targetX = endPX;
        targetY = endPY;

        int[] step = {0};
        animTimer = new Timer(ANIM_DELAY, null);
        animTimer.addActionListener(e -> {
            step[0]++;
            float t = (float) step[0] / ANIM_STEPS;
            t = easeInOut(t);
            animActorX = startPX + (endPX - startPX) * t;
            animActorY = startPY + (endPY - startPY) * t;
            repaint();
            if (step[0] >= ANIM_STEPS) {
                animTimer.stop();
                actorRow = nextState.row;
                actorCol = nextState.col;
                nextCheckpoint = nextState.nextCheckpoint;
                animActorX = -1;
                repaint();
                if (onDone != null) onDone.run();
            }
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) {
            drawEmpty(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        recalcLayout();
        char[][] grid = board.getGrid();

        for (int r = 0; r < board.getN(); r++) {
            for (int c = 0; c < board.getM(); c++) {
                int px = offsetX + c * tileSize;
                int py = offsetY + r * tileSize;
                drawTile(g2, grid[r][c], r, c, px, py);
            }
        }
        drawActor(g2);

        g2.dispose();
    }

    private void drawTile(Graphics2D g2, char ch, int row, int col, int px, int py) {
        int ts = tileSize;
        int pad = 2;
        int inner = ts - pad * 2;
        int radius = Theme.RADIUS_SM;


        boolean isTrail = false;
        for (int[] t : trail) {
            if (t[0] == row && t[1] == col) { isTrail = true; break; }
        }


        Color bg;
        String label = "";
        Color labelColor = Theme.TEXT();
        boolean isActor = (row == actorRow && col == actorCol && animActorX < 0);

        switch (ch) {
            case 'X': bg = Theme.tileWall();  break;
            case 'L': bg = Theme.tileLava();  label = "Lava"; labelColor = Theme.ERROR();  break;
            case 'O': bg = Theme.tileGoal();  label = "O"; labelColor = Theme.GREEN();  break;
            case 'Z': bg = Theme.tileStart(); label = ""; /* aktor digambar terpisah */ break;
            case '*': bg = isTrail ? Theme.tilePath2() : Theme.tilePath(); break;
            default:
                if (ch >= '0' && ch <= '9') {
                    int digit = ch - '0';
                    boolean visited = (nextCheckpoint == -1) || (digit < nextCheckpoint);
                    bg = visited ? Theme.tilePath() : Theme.tileCP();
                    label = String.valueOf(digit + 1);
                    labelColor = visited ? Theme.TEXT_MUTED() : Theme.AMBER();
                } else {
                    bg = Theme.tilePath();
                }
        }

        if (isTrail && ch != 'X') {
            bg = Theme.tilePath2();
        }

        g2.setColor(bg);
        g2.fillRoundRect(px + pad, py + pad, inner, inner, radius, radius);


        Color border = (ch == 'X') ? Theme.tileWall().darker()
                     : Theme.withAlpha(Theme.BORDER(), 120);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(px + pad, py + pad, inner - 1, inner - 1, radius, radius);

        if (!label.isEmpty() && !(ch == 'Z' && animActorX < 0)) {
            g2.setFont(Theme.fontBodyBold(ch == 'L' ? 9f : 11f));
            FontMetrics fm = g2.getFontMetrics();
            int lx = px + ts / 2 - fm.stringWidth(label) / 2;
            int ly = py + ts / 2 + fm.getAscent() / 2 - 1;
            g2.setColor(labelColor);
            g2.drawString(label, lx, ly);
        }

    }

    private void drawActor(Graphics2D g2) {
        float ax, ay;
        if (animActorX >= 0) {
            ax = animActorX;
            ay = animActorY;
        } else {
            ax = offsetX + actorCol * tileSize + tileSize / 2f;
            ay = offsetY + actorRow * tileSize + tileSize / 2f;
        }

        int r = tileSize / 3;

        RadialGradientPaint glow = new RadialGradientPaint(
            ax, ay, r * 2,
            new float[]{0f, 1f},
            new Color[]{Theme.withAlpha(Theme.tileActor(), 100), Theme.withAlpha(Theme.tileActor(), 0)}
        );
        g2.setPaint(glow);
        g2.fillOval((int)(ax - r * 2), (int)(ay - r * 2), r * 4, r * 4);

        g2.setColor(Theme.tileActor());
        g2.fillOval((int)(ax - r), (int)(ay - r), r * 2, r * 2);


        g2.setColor(Theme.withAlpha(Color.WHITE, 120));
        g2.fillOval((int)(ax - r / 2), (int)(ay - r / 2 - 2), r / 2, r / 2);


        g2.setColor(Theme.withAlpha(Theme.tileActor().darker(), 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval((int)(ax - r), (int)(ay - r), r * 2, r * 2);
    }

    private void drawEmpty(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.TEXT_MUTED());
        g2.setFont(Theme.fontBody(13f));
        String msg = "Belum ada puzzle dimuat";
        FontMetrics fm = g2.getFontMetrics();
        int x = getWidth() / 2 - fm.stringWidth(msg) / 2;
        int y = getHeight() / 2;
        g2.drawString(msg, x, y);
        g2.dispose();
    }

    private void recalcLayout() {
        if (board == null) return;
        int maxCols = board.getM();
        int maxRows = board.getN();
        int availW = getWidth()  - Theme.PAD * 2;
        int availH = getHeight() - Theme.PAD * 2;
        tileSize = Math.max(24, Math.min(availW / maxCols, availH / maxRows));
        offsetX = (getWidth()  - maxCols * tileSize) / 2;
        offsetY = (getHeight() - maxRows * tileSize) / 2;
    }

    private float easeInOut(float t) {
        return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    public void reset() {
        if (board == null) return;
        int[] start = board.getStart();
        actorRow = start[0];
        actorCol = start[1];
        nextCheckpoint = board.getNumCheckpoints() > 0 ? 0 : -1;
        trail.clear();
        animActorX = -1;
        if (animTimer != null) animTimer.stop();
        repaint();
    }

    public Board getBoard() { return board; }
}