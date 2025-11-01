import javax.swing.*;

public class GameManager {
    private JFrame window;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;

    public GameManager() {
        window = new JFrame("Kitchen Master");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        showMenu();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void showMenu() {
        menuPanel = new MenuPanel(this);
        window.setContentPane(menuPanel);
        window.pack();
        window.setSize(1280, 720);
    }

    public void startGame() {
        try {
            System.out.println("GameManager: startGame() called");
            gamePanel = new GamePanel(this); // Pass GameManager to GamePanel
            window.setContentPane(gamePanel);
            window.pack();
            window.setSize(1280, 720);
            window.repaint();

            gamePanel.requestFocusInWindow();
            try {
                gamePanel.startGameThread();
                System.out.println("GameManager: game thread started");
            } catch (Exception e) {
                System.out.println("Error starting game thread: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception ex) {
            System.out.println("Error in startGame(): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void showDayScreen() {
        DayPanel dayPanel = new DayPanel(this);
        window.setContentPane(dayPanel);
        window.pack();
        window.setSize(1280, 720);
    }

    public void showSettings() {
        SettingsPanel settingsPanel = new SettingsPanel(this);
        window.setContentPane(settingsPanel);
        window.pack();
        window.setSize(1280, 720);
    }
}
