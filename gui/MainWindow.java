package gui;

import src.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {

    private ControlPanel controlPanel;
    private BoardPanel boardPanel;
    private ResultPanel resultPanel;
    private JPanel topBar;
    private JPanel mainContent;
    private UI.PastelButton themeToggle;

    public MainWindow() {
        super("Tucil Miguel 13524069");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(null);

       
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        buildUI();
        applyThemeToAll();
        setVisible(true);
    }

    private void buildUI() {
   
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(Theme.bgGradient(getWidth(), getHeight()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(true);
        setContentPane(root);

        
        topBar = buildTopBar();
        root.add(topBar, BorderLayout.NORTH);

        mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setOpaque(false);
        root.add(mainContent, BorderLayout.CENTER);


        controlPanel = new ControlPanel(new ControlPanel.SolveListener() {
            @Override
            public void onBoardLoaded(Board board) {
                boardPanel.setBoard(board);
                resultPanel.applyTheme();
            }

            @Override
            public void onSolveResult(Board board, SolveResult result, int algo, int hChoice) {
                
                if (result.found && !result.stateHistory.isEmpty()) {
                    boardPanel.setActorState(result.stateHistory.get(0));
                }
                resultPanel.setResult(board, result, algo, hChoice);
            }

            @Override
            public void onError(String message) {
                JOptionPane.showMessageDialog(MainWindow.this,
                    message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        controlPanel.setPreferredSize(new Dimension(250, 0));

     
        boardPanel = new BoardPanel();

         
        resultPanel = new ResultPanel(boardPanel);
        resultPanel.setPreferredSize(new Dimension(290, 0));

        
        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setOpaque(false);
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(Theme.PAD, 0, Theme.PAD, 0));
        boardWrapper.add(boardPanel, BorderLayout.CENTER);

        mainContent.add(controlPanel, BorderLayout.WEST);
        mainContent.add(boardWrapper, BorderLayout.CENTER);
        mainContent.add(resultPanel, BorderLayout.EAST);

        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER()),
            BorderFactory.createEmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD / 2)
        ));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.BORDER()),
            BorderFactory.createEmptyBorder(Theme.PAD, Theme.PAD / 2, Theme.PAD, Theme.PAD)
        ));
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.withAlpha(Theme.SURFACE(), 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.BORDER());
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(10, Theme.PAD, 10, Theme.PAD));


        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JLabel appTitle = new JLabel("Tucil Miguel 13524069") {
            @Override public Color getForeground() { return Theme.TEXT(); }
        };
        appTitle.setFont(Theme.fontBodyBold(15f));

        left.add(appTitle);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        themeToggle = new UI.PastelButton("Light Mode", Theme.AMBER());
        themeToggle.addActionListener(e -> toggleTheme());
        right.add(themeToggle);

        bar.add(right, BorderLayout.EAST);
        bar.setPreferredSize(new Dimension(0, 48));
        return bar;
    }

    private void toggleTheme() {
        Theme.toggle();
        themeToggle.setText(Theme.isDark() ? "Light Mode" : "Dark Mode");
        themeToggle.setColor(Theme.isDark() ? Theme.AMBER() : new Color(0x9575CD));
        applyThemeToAll();
    }

    private void applyThemeToAll() {
        getContentPane().repaint();

        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER()),
            BorderFactory.createEmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD / 2)
        ));
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.BORDER()),
            BorderFactory.createEmptyBorder(Theme.PAD, Theme.PAD / 2, Theme.PAD, Theme.PAD)
        ));

        controlPanel.applyTheme();
        resultPanel.applyTheme();
        boardPanel.repaint();
        UI.applyTheme(topBar);
        topBar.repaint();

        SwingUtilities.updateComponentTreeUI(this);
        repaint();
        revalidate();
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            Theme.setDark(false); // mulai dengan light mode
            new MainWindow();
        });
    }
}