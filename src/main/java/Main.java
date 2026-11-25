import zero.App;
import zero.ui.Label;
import zero.ui.Button;
import zero.ui.Column;
import zero.ui.Row;

public class Main extends App {
    // Player Grid Position (5x5 grid)
    int playerX = 0;
    int playerY = 2;
    int battery = 0; // Battery countdown steps
    boolean hasWon = false;
    boolean gameOver = false;

    // Game locations on the grid
    final int chargerX = 2, chargerY = 1;
    final int beaconX = 4, beaconY = 2;
    final int hazardX = 3, hazardY = 2; // Red obstacle

    Label statusLabel;
    Label batteryLabel;
    Label posLabel;

    @Override public void settings() {
        title("Zero Signal Rescue");
        size(450, 400);
        padding(20);
    }

    @Override public void setup() {
        statusLabel = new Label("Mission: Visit the Charger at (2,1), then reach the Beacon!");
        batteryLabel = new Label("Battery: Empty (0 steps)");
        posLabel = new Label("Position: (0, 2)");

        Button upBtn = new Button("↑ Up");
        Button downBtn = new Button("↓ Down");
        Button leftBtn = new Button("← Left");
        Button rightBtn = new Button("→ Right");
        Button restartBtn = new Button("🔄 Restart");

        upBtn.onClick(() -> move(0, -1));
        downBtn.onClick(() -> move(0, 1));
        leftBtn.onClick(() -> move(-1, 0));
        rightBtn.onClick(() -> move(1, 0));
        restartBtn.onClick(() -> restart());

        Row moveControls = new Row(10, leftBtn, upBtn, downBtn, rightBtn);

        show(new Column(15, 
            new Label("🤖 Zero Signal Rescue"),
            statusLabel,
            batteryLabel,
            posLabel,
            new Label("Controls:"),
            moveControls,
            restartBtn
        ));
    }

    public void move(int dx, int dy) {
        if (hasWon || gameOver) return;

        int newX = playerX + dx;
        int newY = playerY + dy;

        // Check grid boundaries
        if (newX < 0 || newX > 4 || newY < 0 || newY > 4) {
            statusLabel.setText("Hit the boundary! Stay inside the 5x5 grid.");
            return;
        }

        playerX = newX;
        playerY = newY;
        posLabel.setText("Position: (" + playerX + ", " + playerY + ")");

        // Drain battery per step if charged
        if (battery > 0) {
            battery--;
            batteryLabel.setText("Battery: " + battery + " steps remaining");
            if (battery <= 0) {
                statusLabel.setText("Battery ran out! Recharge at the Charger station.");
            }
        }

        // Check hazard obstacle collision
        if (playerX == hazardX && playerY == hazardY) {
            gameOver = true;
            statusLabel.setText("💥 Hit a hazard obstacle! Game Over. Click Restart.");
            batteryLabel.setText("Battery: Dead");
            return;
        }

        // Check Charger collision
        if (playerX == chargerX && playerY == chargerY) {
            battery = 4; // 4 steps of charge power
            statusLabel.setText("⚡ Charged! You have 4 steps to reach the Beacon!");
            batteryLabel.setText("Battery: 4 steps remaining");
            return;
        }

        // Check Beacon collision (Win condition)
        if (playerX == beaconX && playerY == beaconY) {
            if (battery > 0) {
                hasWon = true;
                statusLabel.setText("🎉 SIGNAL SENT! You won the game!");
            } else {
                statusLabel.setText(" Beacon reached, but battery is dead! Visit the charger first.");
            }
            return;
        }
    }

    public void restart() {
        playerX = 0;
        playerY = 2;
        battery = 0;
        hasWon = false;
        gameOver = false;
        statusLabel.setText("Game restarted. Visit the Charger at (2,1)!");
        batteryLabel.setText("Battery: Empty (0 steps)");
        posLabel.setText("Position: (0, 2)");
    }

    public static void main(String[] args) { launch(args); }
}