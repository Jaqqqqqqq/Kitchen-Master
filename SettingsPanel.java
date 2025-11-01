import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel { // ✅ must extend JPanel
    private GameManager gm;
    private JSlider volumeSlider;
    private JButton howToPlayBtn;
    private JButton aboutBtn;
    private JButton backBtn;
    private JLabel titleLabel;

    public SettingsPanel(GameManager gm) {
        this.gm = gm;
        setLayout(null); // Absolute layout for precise placement
        setBackground(new Color(240, 230, 210));

        // Title
        titleLabel = new JLabel("⚙️ Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 36));
        titleLabel.setBounds(0, 50, 1280, 60);
        add(titleLabel);

        // Volume Slider
        JLabel volumeLabel = new JLabel("🎵 Volume:");
        volumeLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
        volumeLabel.setBounds(450, 180, 200, 40);
        add(volumeLabel);

        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setBounds(600, 180, 200, 40);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        add(volumeSlider);

        // Buttons
        howToPlayBtn = createButton("📘 How to Play", 540, 270);
        aboutBtn = createButton("👨‍🍳 About Us", 540, 350);
        backBtn = createButton("⬅️ Back to Menu", 540, 430);

        add(howToPlayBtn);
        add(aboutBtn);
        add(backBtn);

        // Action Listeners
        howToPlayBtn.addActionListener(e -> showHowToPlay());
        aboutBtn.addActionListener(e -> showAboutUs());
        backBtn.addActionListener(e -> gm.showMenu());
    }

    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Trebuchet MS", Font.BOLD, 22));
        btn.setBounds(x, y, 200, 60);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(255, 200, 100));
        btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        return btn;
    }

    private void showHowToPlay() {
        JOptionPane.showMessageDialog(
            this,
            """
            🕹️ HOW TO PLAY:

            1. Serve customers quickly to earn money.
            2. Switch between the front and kitchen using SPACEBAR.
            3. Click on cooking stations to prepare food.
            4. Deliver orders when food is ready!
            """,
            "How to Play",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showAboutUs() {
        JOptionPane.showMessageDialog(
            this,
            """
            👨‍🍳 ABOUT US:

            Kitchen Master is a restaurant tycoon game
            developed by Gadget (#JavaGameDeveloper).

            Build your kitchen empire and become the ultimate chef!
            """,
            "About Us",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Optional background or styling
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 240, 200));
        g2.fillRoundRect(400, 140, 480, 400, 30, 30);
    }
}
