import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class DayPanel extends JPanel {
    private GameManager gm;
    private Image shopBackground;
    private Ellipse2D.Float dayButton; 
    private Rectangle backButton;
    private int currentDay = 1;
    private boolean hoverDay = false;
    private boolean hoverBack = false;

    public DayPanel(GameManager gm) {
        this.gm = gm;

        setPreferredSize(new Dimension(1280, 720));
        setFocusable(true);

        // Load background
        try {
            shopBackground = new ImageIcon(getClass().getResource("images/shop.jpg")).getImage();
        } catch (Exception e) {
            System.out.println("⚠️ shop.png not found!");
        }

        // ✅ Define circular button position (lower-right corner)
        int diameter = 150;
        int marginX = 60; // distance from right edge
        int marginY = 50; // distance from bottom
        float x = 1280 - diameter - marginX;
        float y = 720 - diameter - marginY;
        dayButton = new Ellipse2D.Float(x, y, diameter, diameter);

        // ✅ Keep back button at top-left
        backButton = new Rectangle(40, 40, 150, 60);

        // Mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();

                if (dayButton.contains(p)) {
                    System.out.println("✅ Day " + currentDay + " started!");
                    gm.startGame(); // Switch to GamePanel
                } else if (backButton.contains(p)) {
                    gm.showMenu(); // Go back to menu
                }
            }
        });

        // Enable hover detection
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                boolean wasHoverDay = hoverDay;
                boolean wasHoverBack = hoverBack;
                hoverDay = dayButton.contains(p);
                hoverBack = backButton.contains(p);
                if (hoverDay != wasHoverDay || hoverBack != wasHoverBack) {
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        if (shopBackground != null) {
            g2.drawImage(shopBackground, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 🟠 Draw circular "Day" button
        g2.setColor(hoverDay ? new Color(255, 180, 100) : new Color(255, 200, 120));
        g2.fill(dayButton);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(4));
        g2.draw(dayButton);

        // Text inside the circle
        g2.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        g2.setColor(Color.BLACK);
        String text = "Day " + currentDay;
        FontMetrics fm = g2.getFontMetrics();
        int textX = (int) (dayButton.x + (dayButton.width - fm.stringWidth(text)) / 2);
        int textY = (int) (dayButton.y + (dayButton.height + fm.getAscent()) / 2 - 4);
        g2.drawString(text, textX, textY);

        // ⬅️ Draw back button
        g2.setColor(hoverBack ? new Color(255, 180, 180) : new Color(255, 150, 150));
        g2.fillRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 20, 20);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 20, 20);

        g2.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        g2.setColor(Color.BLACK);
        g2.drawString("Back", backButton.x + 25, backButton.y + 38);
    }

}
