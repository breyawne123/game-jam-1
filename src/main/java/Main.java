import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JPanel implements ActionListener, KeyListener {
    // Player coordinates and size
    int playerX = 50, playerY = 200, playerSize = 30;
    int speed = 5;

    // Charger and Beacon rectangles
    Rectangle charger = new Rectangle(250, 100, 40, 40);
    Rectangle beacon = new Rectangle(450, 200, 40, 40);

    // Game state booleans
    boolean isCharged = false;
    boolean hasWon = false;

    // Movement flags
    boolean up, down, left, right;

    Timer timer;

    public Main() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(this);

        // Add a Restart button at the top
        JButton restartBtn = new JButton("Restart");
        restartBtn.setFocusable(false); // Keeps keyboard focus on the game
        restartBtn.addActionListener(e -> restart());
        add(restartBtn);

        // Game loop running at ~60 FPS
        timer = new Timer(16, this);
        timer.start();
    }

    // Resets the player position, charger visit, and win state
    public void restart() {
        playerX = 50;
        playerY = 200;
        isCharged = false;
        hasWon = false;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!hasWon) {
            // Update player position
            if (up && playerY > 0) playerY -= speed;
            if (down && playerY < getHeight() - playerSize) playerY += speed;
            if (left && playerX > 0) playerX -= speed;
            if (right && playerX < getWidth() - playerSize) playerX += speed;

            // Create hitbox for player
            Rectangle playerBox = new Rectangle(playerX, playerY, playerSize, playerSize);

            // Check contact with Charger
            if (playerBox.intersects(charger)) {
                isCharged = true;
            }

            // Check contact with Beacon (only works if charged!)
            if (playerBox.intersects(beacon) && isCharged) {
                hasWon = true;
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Charger (Yellow if active, Gray if already used)
        g.setColor(isCharged ? Color.GRAY : Color.YELLOW);
        g.fillRect(charger.x, charger.y, charger.width, charger.height);
        g.setColor(Color.WHITE);
        g.drawString("Charger", charger.x - 2, charger.y - 5);

        // Draw Beacon (Green if unlocked, Red if locked)
        g.setColor(isCharged ? Color.GREEN : Color.RED);
        g.fillRect(beacon.x, beacon.y, beacon.width, beacon.height);
        g.setColor(Color.WHITE);
        g.drawString("Beacon", beacon.x, beacon.y - 5);

        // Draw Player Bot
        g.setColor(Color.CYAN);
        g.fillRect(playerX, playerY, playerSize, playerSize);

        // Draw Status / Win Message
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        if (hasWon) {
            g.drawString("SIGNAL SENT! YOU WIN! Press Restart to play again.", 100, 350);
        } else if (!isCharged) {
            g.drawString("Status: Low Battery. Visit the Charger first!", 20, 380);
        } else {
            g.drawString("Status: Charged! Reach the Green Beacon!", 20, 380);
        }
    }

    // Keyboard Controls (Arrow keys or WASD)
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) right = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Zero Signal Rescue");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Main());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}