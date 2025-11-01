import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    // SCREEN SETTINGS
    final int SCREEN_WIDTH = 1280;
    final int SCREEN_HEIGHT = 700;
    
    // Cooking area dimensions
    private final int panX = 580;
    private final int panY = 360;
    private final int panW = 200;
    private final int panH = 120;

    // Mouse position tracking
    private int mouseX = -1;
    private int mouseY = -1;

    // GAME LOOP
    Thread gameThread;
    boolean running = false;
    final int FPS = 60;

    // Game state
    private GameState gameState;

    // BACKGROUNDS
    private BufferedImage frontBg;
    private BufferedImage kitchenBg;
    private BufferedImage insideKitchenBg;

    // Station state
    private enum Station {
        FRONT,
        KITCHEN,
        INSIDE_KITCHEN
    }
    private Station currentStation = Station.FRONT;

    // Pause functionality
    private boolean paused = false;
    private GameManager gm;

    // Kitchen assets and state
    private BufferedImage burgerBunImg;
    private BufferedImage cookingPattyImg;
    private BufferedImage cookedPattyImg;
    private BufferedImage rawPattyImg;
    private BufferedImage rawFriesImg;
    private BufferedImage cookingFriesImg;
    private BufferedImage cookedFriesImg;
    private BufferedImage rawHotdogImg;
    private BufferedImage hotdogBunImg;
    private BufferedImage cookedHotdogImg;
    private BufferedImage cupPileImg;
    private BufferedImage sodaMachineImg;
    private BufferedImage sodaMachineFillImg;
    private BufferedImage burgerFinishedImg;
    private BufferedImage friesContainerImg;
    private BufferedImage hotdogWithBunImg;
    private BufferedImage hotdogImg;
    private BufferedImage sodaImg;
    private BufferedImage trayImg;
    private BufferedImage ketchupImg;
    private BufferedImage mustardImg;

    // Cooking and preparation states
    private boolean pattyOnGrill = false;
    private boolean friesInFryer = false;
    private boolean hotdogOnGrill = false;
    private boolean hotdogHasBun = false;
    private boolean hotdogHasKetchup = false;
    private boolean hotdogHasMustard = false;
    private boolean cupSelected = false;

    // Items on tray states
    private boolean burgerOnKitchenTray = false;
    private boolean friesOnKitchenTray = false;
    private boolean hotdogOnKitchenTray = false;
    private boolean sodaOnKitchenTray = false;
    
    // Front view tray states
    private boolean burgerOnFrontTray = false;
    private boolean friesOnFrontTray = false;
    private boolean hotdogOnFrontTray = false;
    private boolean sodaOnFrontTray = false;

    // Tray and cooking state
    private Set<String> trayItems = new HashSet<>();
    private boolean sodaMachineFilling = false;
    private boolean trayOnTable = false; // when true, show tray on front view
    private Timer cookTimer = new Timer();
    // Inside-kitchen interaction state
    private Rectangle burgerBunHotspot = new Rectangle(807, 120, 140, 145);
    private Rectangle rawPattyHotspot = new Rectangle(444, 120, 140, 145);
    private Rectangle rawFriesHotspot = new Rectangle(627, 120, 140, 145);
    private Rectangle rawHotdogHotspot = new Rectangle(940, 120, 80, 80);
    private Rectangle hotdogBunHotspot = new Rectangle(980, 200, 80, 80);
    private Rectangle cupsHotspot = new Rectangle(1150, 160, 80, 80);
    private Rectangle sodaMachineHotspot = new Rectangle(1080, 240, 120, 140);



    private boolean pattyCooking = false;
    private boolean pattyCooked = false;
    private boolean friesCooking = false;
    private boolean friesCooked = false;
    private boolean hotdogCooking = false;
    private boolean hotdogCooked = false;
    private Set<String> currentAssembly = new HashSet<>();



    public GamePanel(GameManager gm) {
        this.gm = gm;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setLayout(null);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.gameState = new GameState();

        // Load Background Images
        try {
            frontBg = ImageIO.read(getClass().getResourceAsStream("images/restaurant_front.jpg"));
            kitchenBg = ImageIO.read(getClass().getResourceAsStream("images/restaurant_back.jpg"));
            insideKitchenBg = ImageIO.read(getClass().getResourceAsStream("images/inside_kitchen.png"));
            // Load kitchen assets
            // Use updated raw ingredient images (from images/ folder)
            burgerBunImg = ImageIO.read(getClass().getResourceAsStream("images/burgerBuns.png"));
            cookingPattyImg = ImageIO.read(getClass().getResourceAsStream("images/cookingPatty.png"));
            cookedPattyImg = ImageIO.read(getClass().getResourceAsStream("images/cookedPatty.png"));
            rawPattyImg = ImageIO.read(getClass().getResourceAsStream("images/patties.png"));
            rawFriesImg = ImageIO.read(getClass().getResourceAsStream("images/friess.png"));
            cookingFriesImg = ImageIO.read(getClass().getResourceAsStream("images/cookingFries.png"));
            cookedFriesImg = ImageIO.read(getClass().getResourceAsStream("images/cookedFries.png"));
            rawHotdogImg = ImageIO.read(getClass().getResourceAsStream("images/hotdogs.png"));
            hotdogBunImg = ImageIO.read(getClass().getResourceAsStream("images/hotdogBuns.png"));
            cookedHotdogImg = ImageIO.read(getClass().getResourceAsStream("images/cookedHotdog.png"));
            cupPileImg = ImageIO.read(getClass().getResourceAsStream("images/cupPile.png"));
            sodaMachineImg = ImageIO.read(getClass().getResourceAsStream("images/sodaMachine.png"));
            sodaMachineFillImg = ImageIO.read(getClass().getResourceAsStream("images/sodaMachineFill.png"));
            burgerFinishedImg = ImageIO.read(getClass().getResourceAsStream("images/burger.png"));
            friesContainerImg = ImageIO.read(getClass().getResourceAsStream("images/friesContainer.png"));
            hotdogWithBunImg = ImageIO.read(getClass().getResourceAsStream("images/hotdogwBun.png"));
            hotdogImg = ImageIO.read(getClass().getResourceAsStream("images/hotdog.png"));
            sodaImg = ImageIO.read(getClass().getResourceAsStream("images/soda.png"));
            trayImg = ImageIO.read(getClass().getResourceAsStream("images/tray.png"));
            ketchupImg = ImageIO.read(getClass().getResourceAsStream("images/ketchup.png"));
            mustardImg = ImageIO.read(getClass().getResourceAsStream("images/mustard.png"));

            System.out.println("✅ Backgrounds loaded successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error loading backgrounds:");
            e.printStackTrace();
        }
        // Log creation for debugging DayPanel -> startGame flow
        System.out.println("GamePanel: constructed");
    }

    public void startGameThread() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int frameCount = 0;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                if (!paused) {
                    update();
                }
                repaint();
                delta--;
                frameCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + frameCount + " | Station: " + currentStation + (paused ? " (Paused)" : ""));
                frameCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        // Game logic only runs when not paused
    }

    private Rectangle settingsButton;
    private Rectangle homeButton;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw background depending on station
        switch (currentStation) {
            case FRONT:
                if (frontBg != null) g2.drawImage(frontBg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
                try {
                    drawCustomerAndDialog(g2);  // Draw customer and dialog
                } catch (Exception e) {
                    System.out.println("Error drawing customer: " + e.getMessage());
                }
                drawTrayOnFront(g2);
                break;
            case KITCHEN:
                if (kitchenBg != null) g2.drawImage(kitchenBg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
                break;
            case INSIDE_KITCHEN:
                if (insideKitchenBg != null) {
                    g2.drawImage(insideKitchenBg, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
                    // Draw ingredient hotspots (icons) on top of inside kitchen for interaction
                    // Burger bun
                    if (burgerBunImg != null) g2.drawImage(burgerBunImg, burgerBunHotspot.x, burgerBunHotspot.y, burgerBunHotspot.width, burgerBunHotspot.height, null);
                    // Raw patty
                    if (rawPattyImg != null) g2.drawImage(rawPattyImg, rawPattyHotspot.x, rawPattyHotspot.y, rawPattyHotspot.width, rawPattyHotspot.height, null);
                    // Fries
                    if (rawFriesImg != null) g2.drawImage(rawFriesImg, rawFriesHotspot.x, rawFriesHotspot.y, rawFriesHotspot.width, rawFriesHotspot.height, null);
                    // Hotdog
                    if (rawHotdogImg != null) g2.drawImage(rawHotdogImg, rawHotdogHotspot.x, rawHotdogHotspot.y, rawHotdogHotspot.width, rawHotdogHotspot.height, null);
                    // Cups and soda machine
                    if (cupPileImg != null) g2.drawImage(cupPileImg, cupsHotspot.x, cupsHotspot.y, cupsHotspot.width, cupsHotspot.height, null);
                    if (sodaMachineFilling) {
                        if (sodaMachineFillImg != null) g2.drawImage(sodaMachineFillImg, sodaMachineHotspot.x, sodaMachineHotspot.y, sodaMachineHotspot.width, sodaMachineHotspot.height, null);
                    } else {
                        if (sodaMachineImg != null) g2.drawImage(sodaMachineImg, sodaMachineHotspot.x, sodaMachineHotspot.y, sodaMachineHotspot.width, sodaMachineHotspot.height, null);
                    }

                    // Draw cooking areas
                    int panX = 580, panY = 360, panW = 200, panH = 120;
                    
                    // Burger cooking area
                    if (pattyCooking) {
                        g2.drawImage(rawPattyImg, panX, panY, panW, panH, null);
                    } else if (pattyCooked) {
                        g2.drawImage(cookedPattyImg, panX, panY, panW, panH, null);
                    }
                    
                    // Fries cooking area
                    if (friesCooking) {
                        g2.drawImage(cookingFriesImg, panX + 220, panY, panW, panH, null);
                    } else if (friesCooked) {
                        g2.drawImage(cookedFriesImg, panX + 220, panY, panW, panH, null);
                    }
                    
                    // Hotdog cooking area
                    if (hotdogCooking) {
                        g2.drawImage(rawHotdogImg, panX + 440, panY, panW, panH, null);
                    } else if (hotdogCooked) {
                        g2.drawImage(cookedHotdogImg, panX + 440, panY, panW, panH, null);
                    }
                    
                    // Draw kitchen tray and its contents
                    int trayX = 500, trayY = 500;
                    g2.drawImage(trayImg, trayX, trayY, 300, 150, null);
                    
                    if (burgerOnKitchenTray) {
                        g2.drawImage(burgerFinishedImg, trayX + 20, trayY + 20, 80, 80, null);
                    }
                    if (friesOnKitchenTray) {
                        g2.drawImage(friesContainerImg, trayX + 110, trayY + 20, 80, 80, null);
                    }
                    if (hotdogOnKitchenTray) {
                        g2.drawImage(hotdogImg, trayX + 200, trayY + 20, 80, 80, null);
                    }
                    if (sodaOnKitchenTray) {
                        g2.drawImage(sodaImg, trayX + 20, trayY + 60, 80, 80, null);
                    }
                } else {
                    g2.setColor(Color.GRAY);
                    g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Trebuchet MS", Font.BOLD, 48));
                    g2.drawString("Inside Kitchen View", 400, 350);
                }
                break;
        }

        // Draw UI on top for all stations
        drawTopBar(g2);
        drawUI(g2);

        // Overlay when paused
        // Draw mouse coordinates
        drawMouseCoordinates(g2);

        if (paused) {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Trebuchet MS", Font.BOLD, 40));
            g2.drawString("⏸ GAME PAUSED", SCREEN_WIDTH / 2 - 180, SCREEN_HEIGHT / 2 - 20);

            g2.setFont(new Font("Trebuchet MS", Font.PLAIN, 24));
            g2.drawString("Press P to Resume", SCREEN_WIDTH / 2 - 110, SCREEN_HEIGHT / 2 + 30);
            g2.drawString("Press H to Return to Menu", SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2 + 70);
        }

        g2.dispose();
    }

    private void drawTopBar(Graphics2D g2) {
        // Draw semi-transparent black bar at the top
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, SCREEN_WIDTH, 60);

        // Draw settings and home buttons
        settingsButton = new Rectangle(SCREEN_WIDTH - 120, 10, 40, 40);
        homeButton = new Rectangle(SCREEN_WIDTH - 60, 10, 40, 40);

        // Draw buttons
        g2.setColor(Color.WHITE);
        g2.fill3DRect(settingsButton.x, settingsButton.y, settingsButton.width, settingsButton.height, true);
        g2.fill3DRect(homeButton.x, homeButton.y, homeButton.width, homeButton.height, true);

        // Draw icons
        g2.setColor(Color.BLACK);
        drawGearIcon(g2, settingsButton.x + 5, settingsButton.y + 5, 30);
        drawHomeIcon(g2, homeButton.x + 5, homeButton.y + 5, 30);
    }

    private void drawGearIcon(Graphics2D g2, int x, int y, int size) {
        g2.drawOval(x + size/4, y + size/4, size/2, size/2);
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            int x1 = x + size/2 + (int)(Math.cos(angle) * size/3);
            int y1 = y + size/2 + (int)(Math.sin(angle) * size/3);
            int x2 = x + size/2 + (int)(Math.cos(angle) * size/2);
            int y2 = y + size/2 + (int)(Math.sin(angle) * size/2);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawHomeIcon(Graphics2D g2, int x, int y, int size) {
        int[] xPoints = {x + size/2, x, x + size};
        int[] yPoints = {y, y + size/2, y + size/2};
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.fillRect(x + size/4, y + size/2, size/2, size/2);
    }

    private void drawUI(Graphics2D g2) {
        // Set up font and colors
        Font uiFont = new Font("Arial", Font.BOLD, 24);
        g2.setFont(uiFont);
        g2.setColor(Color.WHITE);

        // Draw time and day
        g2.drawString("DAY: 1", 20, 40);
        g2.drawString(gameState.getTimeString(), 150, 40);

        // Draw patience meter if there's a current customer
        Customer currentCustomer = gameState.getCurrentCustomer();
        if (currentCustomer != null) {
            int patienceWidth = 200;
            int patienceHeight = 30;
            int x = 300;
            int y = 15;

            // Background
            g2.setColor(Color.GRAY);
            g2.fillRect(x, y, patienceWidth, patienceHeight);

            // Patience bar
            if (currentCustomer.isAngry()) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.GREEN);
            }
            int width = (int)((currentCustomer.getPatience() / 100.0) * patienceWidth);
            g2.fillRect(x, y, width, patienceHeight);

            // Border
            g2.setColor(Color.WHITE);
            g2.drawRect(x, y, patienceWidth, patienceHeight);
            g2.drawString(String.format("%.0f%%", currentCustomer.getPatience()), x + patienceWidth + 10, y + 25);
        }

    // Draw money (display in Philippine Pesos)
    g2.drawString(String.format("₱%.2f", gameState.getTotalMoney()), SCREEN_WIDTH - 200, 40);
    }

    // Draw tray on front view when ready
    private void drawTrayOnFront(Graphics2D g2) {
        if (!trayOnTable) return;
        int trayX = SCREEN_WIDTH/2 - 150;
        int trayY = SCREEN_HEIGHT - 180;
        
        // Draw the tray image
        g2.drawImage(trayImg, trayX, trayY, 300, 150, null);
        
        // Draw items on the front tray
        if (burgerOnFrontTray) {
            g2.drawImage(burgerFinishedImg, trayX + 20, trayY + 20, 80, 80, null);
        }
        if (friesOnFrontTray) {
            g2.drawImage(friesContainerImg, trayX + 110, trayY + 20, 80, 80, null);
        }
        if (hotdogOnFrontTray) {
            g2.drawImage(hotdogImg, trayX + 200, trayY + 20, 80, 80, null);
        }
        if (sodaOnFrontTray) {
            g2.drawImage(sodaImg, trayX + 20, trayY + 60, 80, 80, null);
        }
    }

    private void checkAssemblyForOrder() {
        Customer c = gameState.getCurrentCustomer();
        if (c == null) return;
        switch (c.getOrderType()) {
            case BURGER:
                if (currentAssembly.contains("bun") && currentAssembly.contains("cookedPatty")) {
                    trayItems.add("BURGER");
                    currentAssembly.remove("bun");
                    currentAssembly.remove("cookedPatty");
                    trayOnTable = true;
                    currentAssembly.clear();
                    currentStation = Station.FRONT;
                    System.out.println("Burger assembled and moved to tray/table");
                }
                break;
            case HOTDOG:
                if (currentAssembly.contains("HOTDOG_COOKED")) {
                    trayItems.add("HOTDOG");
                    currentAssembly.remove("HOTDOG_COOKED");
                    trayOnTable = true;
                    currentAssembly.clear();
                    currentStation = Station.FRONT;
                    System.out.println("Hotdog assembled and moved to tray/table");
                }
                break;
            case FRIES:
                if (currentAssembly.contains("FRIES")) {
                    trayItems.add("FRIES");
                    currentAssembly.remove("FRIES");
                    trayOnTable = true;
                    currentAssembly.clear();
                    currentStation = Station.FRONT;
                    System.out.println("Fries moved to tray/table");
                }
                break;
            case SODA:
                if (trayItems.contains("SODA")) {
                    trayOnTable = true;
                    currentStation = Station.FRONT;
                    System.out.println("Soda moved to tray/table");
                }
                break;
        }
    }

    private void drawCustomerAndDialog(Graphics2D g2) throws IOException {
        Customer currentCustomer = gameState.getCurrentCustomer();
        if (currentCustomer != null) {
            try {
                // Draw customer image
                int customerImageIndex = currentCustomer.getImageIndex();
                BufferedImage customerImage;
                
                if (currentCustomer.isAngry()) {
                    customerImage = ImageIO.read(getClass().getResourceAsStream("images/customer" + (customerImageIndex + 1) + "angry.png"));
                } else {
                    customerImage = ImageIO.read(getClass().getResourceAsStream("images/customer" + (customerImageIndex + 1) + ".png"));
                }
                
                if (customerImage != null) {
                    g2.drawImage(customerImage, 50, SCREEN_HEIGHT/2, 200, 300, null);
                }
            } catch (IOException e) {
                System.out.println("Error loading customer image: " + e.getMessage());
            }

            // Draw speech bubble
            g2.setColor(Color.WHITE);
            int bubbleX = 280;
            int bubbleY = SCREEN_HEIGHT/2 - 50;
            g2.fillRoundRect(bubbleX, bubbleY, 400, 100, 20, 20);
            g2.setColor(Color.BLACK);
            g2.drawString(currentCustomer.getOrder(), bubbleX + 20, bubbleY + 50);

            // Draw response options
            g2.setColor(Color.WHITE);
            g2.fillRect(bubbleX, bubbleY + 120, 100, 40);
            g2.setColor(Color.BLACK);
            g2.drawString("Okay", bubbleX + 20, bubbleY + 145);
        }
    }

    // --- INPUT HANDLERS ---
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // ✅ Press P to toggle pause/resume
        if (code == KeyEvent.VK_P) {
            paused = !paused;
            System.out.println(paused ? "Game Paused" : "Game Resumed");
            repaint();
            return;
        }

        // ✅ Press H to return to menu (only works when paused)
        if (paused && code == KeyEvent.VK_H) {
            System.out.println("Returning to Main Menu...");
            paused = false;
            running = false; // stop the game loop
            gm.showMenu();   // go back to menu screen
            return;
        }

        // Ignore other input when paused
        if (paused) return;

        // Station switching logic
        if (code == KeyEvent.VK_SPACE) {
            if (currentStation == Station.FRONT)
                currentStation = Station.KITCHEN;
            else if (currentStation == Station.KITCHEN)
                currentStation = Station.FRONT;
            System.out.println("Switched to " + currentStation + " view.");
        } else if (code == KeyEvent.VK_ENTER) {
            if (currentStation == Station.KITCHEN) {
                currentStation = Station.INSIDE_KITCHEN;
                System.out.println("Entered INSIDE_KITCHEN view.");
            }
        } else if (code == KeyEvent.VK_BACK_SPACE) {
            if (currentStation == Station.INSIDE_KITCHEN) {
                currentStation = Station.KITCHEN;
                System.out.println("Returned to KITCHEN view.");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (paused) return; // disable mouse actions when paused
        int x = e.getX();
        int y = e.getY();
        
        // Check for settings and home button clicks first (available in all stations)
        if (settingsButton != null && settingsButton.contains(x, y)) {
            gm.showSettings();
            return;
        }
        
        if (homeButton != null && homeButton.contains(x, y)) {
            gm.showMenu();
            return;
        }
        
        if (currentStation == Station.FRONT) {
            // Front view tray handling
            if (trayOnTable) {
                int trayX = SCREEN_WIDTH/2 - 150;
                int trayY = SCREEN_HEIGHT - 180;
                Rectangle trayRect = new Rectangle(trayX, trayY, 300, 150);
                
                Customer c = gameState.getCurrentCustomer();
                if (c != null) {
                    boolean correctOrder = false;
                    
                    // Check if clicked item matches order
                    if (c.getOrderType() == Customer.OrderType.BURGER && burgerOnFrontTray &&
                        new Rectangle(trayX + 20, trayY + 20, 80, 80).contains(x, y)) {
                        correctOrder = true;
                        burgerOnFrontTray = false;
                    } else if (c.getOrderType() == Customer.OrderType.FRIES && friesOnFrontTray &&
                             new Rectangle(trayX + 110, trayY + 20, 80, 80).contains(x, y)) {
                        correctOrder = true;
                        friesOnFrontTray = false;
                    } else if (c.getOrderType() == Customer.OrderType.HOTDOG && hotdogOnFrontTray &&
                             new Rectangle(trayX + 200, trayY + 20, 80, 80).contains(x, y)) {
                        correctOrder = true;
                        hotdogOnFrontTray = false;
                    } else if (c.getOrderType() == Customer.OrderType.SODA && sodaOnFrontTray &&
                             new Rectangle(trayX + 20, trayY + 60, 80, 80).contains(x, y)) {
                        correctOrder = true;
                        sodaOnFrontTray = false;
                    }
                    
                    if (correctOrder) {
                        // Correct order served
                        gameState.addMoney(c.getOrderPrice());
                        System.out.println("Customer served correctly. Earned: " + c.getOrderPrice());
                        gameState.completeCurrentCustomer();
                    } else if (trayRect.contains(x, y)) {
                        // Wrong order served
                        System.out.println("Wrong order served!");
                        // Wrong order handling - customer becomes angry and patience drops
                        c.decreasePatience();
                        // Refund handling could be added here
                    }
                    
                    // Hide tray if empty
                    if (!burgerOnFrontTray && !friesOnFrontTray && !hotdogOnFrontTray && !sodaOnFrontTray) {
                        trayOnTable = false;
                    }
                    
                    repaint();
                    return;
                }
            }

            // Check if clicked on "Okay" button
            int bubbleX = 280;
            int bubbleY = SCREEN_HEIGHT/2 - 50;
            Rectangle okayButton = new Rectangle(bubbleX, bubbleY + 120, 100, 40);
            
            if (okayButton.contains(x, y)) {
                gameState.startCurrentCustomerTimer(); // Start countdown when okay is clicked
                currentStation = Station.KITCHEN;
            }
        } else if (currentStation == Station.INSIDE_KITCHEN) {
            // Inside kitchen interactions: ingredients and machines
            // Burger preparation
            if (rawPattyHotspot.contains(x, y) && !pattyCooking && !pattyCooked) {
                pattyCooking = true;
                System.out.println("Started cooking patty");
                cookTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        pattyCooking = false;
                        pattyCooked = true;
                        System.out.println("Patty cooked");
                        repaint();
                    }
                }, 5000);
            } else if (burgerBunHotspot.contains(x, y) && pattyCooked) {
                pattyCooked = false;
                burgerOnKitchenTray = true;
                System.out.println("Burger assembled");
                repaint();
            }

            // Fries preparation
            if (rawFriesHotspot.contains(x, y) && !friesCooking && !friesCooked) {
                friesCooking = true;
                System.out.println("Started cooking fries");
                cookTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        friesCooking = false;
                        friesCooked = true;
                        System.out.println("Fries cooked");
                        repaint();
                    }
                }, 5000);
            } else if (friesCooked) {
                if (new Rectangle(panX + 220, panY, panW, panH).contains(x, y)) {
                    friesCooked = false;
                    friesOnKitchenTray = true;
                    System.out.println("Fries moved to tray");
                    repaint();
                }
            }

            // Hotdog preparation
            if (rawHotdogHotspot.contains(x, y) && !hotdogCooking && !hotdogCooked) {
                hotdogCooking = true;
                System.out.println("Started cooking hotdog");
                cookTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        hotdogCooking = false;
                        hotdogCooked = true;
                        System.out.println("Hotdog cooked");
                        repaint();
                    }
                }, 5000);
            } else if (hotdogCooked && !hotdogHasBun) {
                Rectangle ketchupRect = new Rectangle(1000, 200, 50, 50);
                Rectangle mustardRect = new Rectangle(1060, 200, 50, 50);
                
                if (hotdogBunHotspot.contains(x, y)) {
                    hotdogHasBun = true;
                    System.out.println("Added bun to hotdog");
                } else if (ketchupRect.contains(x, y) && hotdogHasBun) {
                    hotdogHasKetchup = true;
                    System.out.println("Added ketchup");
                } else if (mustardRect.contains(x, y) && hotdogHasBun && hotdogHasKetchup) {
                    hotdogHasMustard = true;
                    hotdogCooked = false;
                    hotdogHasBun = false;
                    hotdogHasKetchup = false;
                    hotdogOnKitchenTray = true;
                    System.out.println("Hotdog complete and on tray");
                }
                repaint();
            }

            // Soda preparation
            if (cupsHotspot.contains(x, y) && !cupSelected) {
                cupSelected = true;
                System.out.println("Cup selected");
            } else if (sodaMachineHotspot.contains(x, y) && cupSelected && !sodaMachineFilling) {
                sodaMachineFilling = true;
                System.out.println("Filling soda");
                cookTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        sodaMachineFilling = false;
                        cupSelected = false;
                        sodaOnKitchenTray = true;
                        System.out.println("Soda ready on tray");
                        repaint();
                    }
                }, 5000);
            }

            // Kitchen tray handling
            Rectangle kitchenTrayRect = new Rectangle(500, 500, 300, 150);
            if (kitchenTrayRect.contains(x, y)) {
                if (burgerOnKitchenTray || friesOnKitchenTray || hotdogOnKitchenTray || sodaOnKitchenTray) {
                    // Transfer items to front tray
                    burgerOnFrontTray = burgerOnKitchenTray;
                    friesOnFrontTray = friesOnKitchenTray;
                    hotdogOnFrontTray = hotdogOnKitchenTray;
                    sodaOnFrontTray = sodaOnKitchenTray;
                    
                    // Clear kitchen tray
                    burgerOnKitchenTray = false;
                    friesOnKitchenTray = false;
                    hotdogOnKitchenTray = false;
                    sodaOnKitchenTray = false;
                    
                    // Show front tray
                    trayOnTable = true;
                    currentStation = Station.FRONT;
                    System.out.println("Items moved to front tray");
                    repaint();
                }
            }
        }
        System.out.println("Mouse clicked at: " + x + ", " + y);
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {
        mouseX = -1;
        mouseY = -1;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    private void drawMouseCoordinates(Graphics2D g2) {
        if (mouseX >= 0 && mouseY >= 0) {
            // Draw coordinate text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Consolas", Font.BOLD, 16));
            String coords = String.format("Mouse Position - X: %d, Y: %d", mouseX, mouseY);
            g2.drawString(coords, 10, SCREEN_HEIGHT - 20);

            // Draw crosshair at mouse position
            g2.setColor(Color.RED);
            g2.drawLine(mouseX - 5, mouseY, mouseX + 5, mouseY);
            g2.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 5);
        }
    }
}
