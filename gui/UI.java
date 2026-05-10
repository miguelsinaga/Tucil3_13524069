package gui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class UI {

   
   
    public static class PastelButton extends JButton {
        private Color baseColor;
        private float hoverAlpha = 0f;
        private boolean hovered = false;

        public PastelButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(Theme.fontBodyBold(13f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(Theme.TEXT());

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    animateHover(true);
                }
                @Override public void mouseExited(MouseEvent e) {
                    hovered = false;
                    animateHover(false);
                }
            });
        }

        private void animateHover(boolean in) {
            Timer t = new Timer(16, null);
            t.addActionListener(e -> {
                hoverAlpha += in ? 0.12f : -0.12f;
                hoverAlpha = Math.max(0f, Math.min(1f, hoverAlpha));
                repaint();
                if ((in && hoverAlpha >= 1f) || (!in && hoverAlpha <= 0f)) {
                    ((Timer) e.getSource()).stop();
                }
            });
            t.start();
        }

        public void setColor(Color c) { this.baseColor = c; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int r = Theme.RADIUS;

            
            g2.setColor(Theme.shadowColor());
            g2.fillRoundRect(2, 3, w - 2, h - 2, r, r);

            
            Color fill = baseColor;
            if (hoverAlpha > 0) {
                fill = blend(baseColor, baseColor.brighter(), hoverAlpha * 0.3f);
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 2, h - 2, r, r);


            g2.setColor(Theme.withAlpha(baseColor.darker(), 80));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 3, h - 3, r, r);

            g2.dispose();
            super.paintComponent(g);
        }

        private Color blend(Color a, Color b, float t) {
            int red   = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
            int green = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
            int blue  = (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
            return new Color(
                Math.max(0, Math.min(255, red)),
                Math.max(0, Math.min(255, green)),
                Math.max(0, Math.min(255, blue))
            );
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width + 24, d.height + 10);
        }
    }


    public static class Card extends JPanel {
        private Color bgColor;
        private boolean showShadow;

        public Card() { this(null, true); }
        public Card(Color bgColor, boolean showShadow) {
            this.bgColor = bgColor;
            this.showShadow = showShadow;
            setOpaque(false);
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int r = Theme.RADIUS;

            if (showShadow) {
                g2.setColor(Theme.shadowColor());
                g2.fillRoundRect(3, 5, w - 4, h - 4, r, r);
            }

            Color fill = bgColor != null ? bgColor : Theme.SURFACE();
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 3, h - 5, r, r);


            g2.setColor(Theme.BORDER());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 4, h - 6, r, r);

            g2.dispose();
        }

        public void applyTheme() {
            repaint();
        }
    }

    
    public static class Tag extends JLabel {
        private Color tagColor;

        public Tag(String text, Color color) {
            super(text);
            this.tagColor = color;
            setFont(Theme.fontBodyBold(11f));
            setForeground(Theme.TEXT());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.withAlpha(tagColor, 180));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_SM, Theme.RADIUS_SM);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    public static class Divider extends JPanel {
        public Divider() {
            setOpaque(false);
            setPreferredSize(new Dimension(1, 1));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Theme.BORDER());
            g.fillRect(0, 0, getWidth(), 1);
        }
    }

    public static class RoundBorder extends AbstractBorder {
        private int radius;
        public RoundBorder(int radius) { this.radius = radius; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.BORDER());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 12, 6, 12);
        }
    }

    public static JLabel makeLabel(String text, float size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(bold ? Theme.fontBodyBold(size) : Theme.fontBody(size));
        l.setForeground(Theme.TEXT());
        return l;
    }


    public static JLabel makeSubLabel(String text, float size) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.fontBody(size));
        l.setForeground(Theme.TEXT_SUB());
        return l;
    }


    public static JTextField makeField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(Theme.fontBody(13f));
        f.setForeground(Theme.TEXT());
        f.setBackground(Theme.SURFACE2());
        f.setCaretColor(Theme.PURPLE());
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(Theme.RADIUS_SM),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        f.setOpaque(true);

        f.putClientProperty("placeholder", placeholder);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.repaint(); }
            @Override public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    public static <T> JComboBox<T> makeCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(Theme.fontBody(13f));
        cb.setForeground(Theme.TEXT());
        cb.setBackground(Theme.SURFACE2());
        cb.setBorder(new RoundBorder(Theme.RADIUS_SM));
        cb.setFocusable(false);
        return cb;
    }

    
    public static JPanel vBox(int gap) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        if (gap > 0) p.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
        return p;
    }
    public static JPanel flow(int align) {
        JPanel p = new JPanel(new FlowLayout(align, 8, 4));
        p.setOpaque(false);
        return p;
    }

    public static Component vSpace(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    public static Component hSpace(int w) {
        return Box.createRigidArea(new Dimension(w, 0));
    }

    public static void applyTheme(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setForeground(Theme.TEXT());
            } else if (c instanceof JTextField) {
                JTextField f = (JTextField) c;
                f.setBackground(Theme.SURFACE2());
                f.setForeground(Theme.TEXT());
                f.setCaretColor(Theme.PURPLE());
            } else if (c instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) c;
                cb.setBackground(Theme.SURFACE2());
                cb.setForeground(Theme.TEXT());
            } else if (c instanceof JScrollPane) {
                ((JScrollPane) c).getViewport().setBackground(Theme.SURFACE());
            } else if (c instanceof JTextArea) {
                JTextArea ta = (JTextArea) c;
                ta.setBackground(Theme.SURFACE());
                ta.setForeground(Theme.TEXT());
                ta.setCaretColor(Theme.PURPLE());
            }
            if (c instanceof Container) applyTheme((Container) c);
        }
        root.repaint();
    }
}