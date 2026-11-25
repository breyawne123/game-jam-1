import javafx.scene.input.KeyCode;
import zero.Color;
import zero.GameApp;
import zero.ui.Button;
import zero.ui.Label;
import zero.ui.Row;

public class Main extends GameApp {

    // Player position, size, and speed (pixels per second)
    double playerX = 60;
    double playerY = 230;
    double playerSize = 32;
    double speed = 220;

    // Charging Station (centre coordinates and diameter)
    double chargerX = 350;
    double chargerY = 120;
    double chargerDiameter = 36;

    // Distress Beacon (top-left coordinates and size)
    double beaconX = 680;
    double beaconY = 225;
    double beaconSize = 44;

    // Game state booleans (Lesson 14, Step 4)
    boolean isCharged = false;
    boolean hasWon = false;

    // HUD status label
    Label statusLabel;

    @Override
    public void settings() {
        size(800, 500);
        title("Zero-Signal Rescue");
    }

    @Override
    public void setup() {
        statusLabel = new Label("Battery low: Visit the gold charger first!");
        Button restartButton = new Button("Restart");
        restartButton.onClick(this::restart);

        // Mount HUD status and Restart button below the canvas
        controls(new Row(16, restartButton, statusLabel));
    }

    @Override
    public void update(double seconds) {
        if (hasWon) {
            return; // Freeze player movement after winning
        }

        // Move player using elapsed seconds so speed stays consistent
        if (keyDown(KeyCode.UP) || keyDown(KeyCode.W)) {
            playerY -= speed * seconds;
        }
        if (keyDown(KeyCode.DOWN) || keyDown(KeyCode.S)) {
            playerY += speed * seconds;
        }
        if (keyDown(KeyCode.LEFT) || keyDown(KeyCode.A)) {
            playerX -= speed * seconds;
        }
        if (keyDown(KeyCode.RIGHT) || keyDown(KeyCode.D)) {
            playerX += speed * seconds;
        }

        // Keep player inside canvas bounds
        playerX = Math.max(0, Math.min(playerX, width() - playerSize));
        playerY = Math.max(0, Math.min(playerY, height() - playerSize));

        // 1. Check contact with Charging Station (circle centered at chargerX/Y)
        double chargerRadius = chargerDiameter / 2.0;
        if (!isCharged && overlaps(
                playerX, playerY, playerSize, playerSize,
                chargerX - chargerRadius, chargerY - chargerRadius, chargerDiameter, chargerDiameter)) {
            isCharged = true;
            statusLabel.setText("Charged! Reach the green beacon to win!");
        }

        // 2. Check contact with Beacon (only wins if charged first!)
        if (isCharged && overlaps(
                playerX, playerY, playerSize, playerSize,
                beaconX, beaconY, beaconSize, beaconSize)) {
            hasWon = true;
            statusLabel.setText("Signal restored! You win!");
        }
    }

    @Override
    public void draw() {
        // Clear canvas with a dark background
        background(Color.rgb(22, 27, 38));

        // Draw Charging Station (Gold before visit, dark gray after)
        fill(isCharged ? Color.rgb(70, 75, 85) : Color.GOLD);
        circle(chargerX, chargerY, chargerDiameter);

        // Draw Distress Beacon (Bright green when unlocked, dim green when locked)
        fill(isCharged ? Color.rgb(50, 220, 100) : Color.rgb(25, 95, 45));
        rect(beaconX, beaconY, beaconSize, beaconSize);

        // Draw Player Bot (Cyan when charged, blue when low battery)
        fill(isCharged ? Color.rgb(0, 220, 255) : Color.BLUE);
        rect(playerX, playerY, playerSize, playerSize);

        // On-canvas banner text
        fill(Color.WHITE);
        textSize(20);
        if (hasWon) {
            textCentered("SIGNAL RESTORED! YOU WIN!", width() / 2, 50);
        } else if (!isCharged) {
            textCentered("Step 1: Touch the gold charger", width() / 2, 40);
        } else {
            textCentered("Step 2: Reach the green beacon!", width() / 2, 40);
        }
    }

    // Lesson 14, Step 4: Reset player position, charger visit, and win state
    public void restart() {
        playerX = 60;
        playerY = 230;
        isCharged = false;
        hasWon = false;
        statusLabel.setText("Battery low: Visit the gold charger first!");
        canvas().requestFocus(); // Return keyboard focus to the game canvas
    }

    // Bounding-box contact check helper
    private boolean overlaps(double ax, double ay, double aw, double ah,
                             double bx, double by, double bw, double bh) {
        return ax < bx + bw &&
               ax + aw > bx &&
               ay < by + bh &&
               ay + ah > by;
    }
}