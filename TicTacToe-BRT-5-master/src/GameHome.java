import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class GameHome extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 500;

    public GameHome() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setLayout(new BorderLayout());

        // Make the panel opaque to show the background
        setOpaque(true);

        // Title Panel with transparent background
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Make title panel transparent
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Classic Games Collection");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(20, 20, 20));
        titlePanel.add(titleLabel);

        // Buttons Panel with transparent background
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonsPanel.setOpaque(false); // Make panel transparent

        JButton tictactoeBtn = createGameButton("Tic Tac Toe");
        JButton connectFourBtn = createGameButton("Connect Four");
        JButton othelloBtn = createGameButton("Othello");

        // Add action listeners
        tictactoeBtn.addActionListener(e -> openGame("tictactoe"));
        connectFourBtn.addActionListener(e -> openGame("connectfour"));
        othelloBtn.addActionListener(e -> openGame("othello"));

        buttonsPanel.add(tictactoeBtn);
        buttonsPanel.add(connectFourBtn);
        buttonsPanel.add(othelloBtn);

        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
    }

    private JButton createGameButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Create gradient for button
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(100, 150, 255),
                        0, getHeight(),
                        new Color(80, 120, 220));

                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Add shine effect
                g2d.setPaint(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight()/2, 15, 15);

                // Set text color and draw string
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(text, textX, textY);

                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(200, 60));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Create gradient background
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, getHeight());
        float[] dist = {0.0f, 0.3f, 0.7f, 1.0f};
        Color[] colors = {
                new Color(70, 130, 180),   // Steel Blue
                new Color(100, 149, 237),  // Cornflower Blue
                new Color(135, 206, 235),  // Sky Blue
                new Color(176, 224, 230)   // Powder Blue
        };
        LinearGradientPaint gradient = new LinearGradientPaint(
                start, end, dist, colors);

        // Fill background with gradient
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add some decorative circles in background
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 10; i++) {
            int size = (int)(Math.random() * 100) + 50;
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            g2d.fillOval(x, y, size, size);
        }

        g2d.dispose();
    }

    private void openGame(String gameType) {
        JFrame gameFrame = new JFrame();

        switch (gameType) {
            case "tictactoe":
                gameFrame.setTitle("Tic Tac Toe");
                gameFrame.setContentPane(new GameMain());
                break;
            case "connectfour":
                gameFrame.setTitle("Connect Four");
                gameFrame.setContentPane(new ConnectFour());
                break;
            case "othello":
                gameFrame.setTitle("Othello");
                gameFrame.setContentPane(new Othello());
                break;
        }

        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            // Set sistem look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Classic Games Collection");
            frame.setContentPane(new GameHome());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}