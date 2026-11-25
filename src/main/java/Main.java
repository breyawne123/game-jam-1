import zero.App;
import zero.ui.Canvas;
import zero.ui.Button;
import zero.ui.Column;
import zero.ui.Label;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Main extends App {
    // Player coordinates and size
    double playerX = 50, playerY = 180, playerSize = 30;
    double speed = 4;

    // Game objects (x, y, width, height)
    double chargerX = 220, chargerY = 60, boxSize = 40;
    double beaconX = 500, beaconY = 180;
    double wallX = 160, wallY = 120, wallW = 25, wallH = 180;
    double hazardX = 360, hazardY = 50, hazardSize = 35;
    double hazardSpeedY = 3.5;

    // Game state & Battery Timer
    boolean isCharged = false;
    boolean hasWon = false;
    int chargeTimeLeft = 0;
    final int MAX_CHARGE_TIME = 240; // ~4 seconds at 60 FPS

    // Keyboard state
    boolean up, down, left, right;
    Label statusLabel;

    @Override
    public void settings() {
        title("Zero Signal Rescue");
        size(620, 480);
        padding(15);
    }

    @Override
    public void setup() {
        statusLabel = new Label("Use Arrow Keys or WASD! Touch YELLOW Charger, avoid RED hazards.");
        Canvas canvas = new Canvas(580, 380);
        Button restartBtn = new Button("Restart");
        restartBtn.setFocusTraversable(false); // Keeps arrow keys working on the game
        restartBtn.onClick(() -> restart());

        // Keyboard listeners on the scene
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            if (k == KeyCode.UP || k == KeyCode.W) up = true;
            if (k == KeyCode.DOWN || k == KeyCode.S) down = true;
            if (k == KeyCode.LEFT || k == KeyCode.A) left = true;
            if (k == KeyCode.RIGHT || k == KeyCode.D) right = true;
        });

        canvas.setOnKeyReleased(e -> {
            KeyCode k = e.getCode();
            if (k == KeyCode.UP || k == KeyCode.W) up = false;
            if (k == KeyCode.DOWN || k == KeyCode.S) down = false;
            if (k == KeyCode.LEFT || k == KeyCode.A) left = false;
            if (k == KeyCode.RIGHT || k == KeyCode.D) right = false;
        });

        show(new Column(10, statusLabel, canvas, restartBtn));
        canvas.requestFocus();

        // 60 FPS Game Loop
        GraphicsContext gc = canvas.getGraphicsContext2D();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(canvas.getWidth(), canvas.getHeight());
                draw(gc, canvas.getWidth(), canvas.getHeight());
            }
        }.start();
    }

    public void restart() {
        playerX = 50;
        playerY = 180;
        isCharged = false;
        hasWon = false;
        chargeTimeLeft = 0;
        hazardY = 50;
        statusLabel.setText("Game restarted! Touch YELLOW Charger for 4s of power.");
    }

    private void update(double width, double height) {
        if (hasWon) return;

        // 1. Move Player
        if (up && playerY > 0) playerY -= speed;
        if (down && playerY < height - playerSize) playerY += speed;
        if (left && playerX > 0) playerX -= speed;
        if (right && playerX < width - playerSize) playerX += speed;

        // 2. Move Patrol Hazard
        hazardY += hazardSpeedY;
        if (hazardY <= 10 || hazardY >= height - hazardSize - 10) {
            hazardSpeedY = -hazardSpeedY;
        }

        // 3. Battery Timer Countdown
        if (isCharged) {
            chargeTimeLeft--;
            double seconds = Math.round((chargeTimeLeft / 60.0) * 10.0) / 10.0;
            statusLabel.setText("⚡ CHARGED! Reach the GREEN Beacon: " + seconds + "s left");
            if (chargeTimeLeft <= 0) {
                isCharged = false;
                statusLabel.setText("Battery ran out! Recharge at the YELLOW Charger.");
            }
        }

        // 4. Obstacle Collisions -> Instant Restart
        if (collides(playerX, playerY, playerSize, playerSize, wallX, wallY, wallW, wallH) ||
            collides(playerX, playerY, playerSize, playerSize, hazardX, hazardY, hazardSize, hazardSize)) {
            restart();
            statusLabel.setText("💥 Hit an obstacle! Restarted.");
            return;
        }

        // 5. Charger Collision
        if (collides(playerX, playerY, playerSize, playerSize, chargerX, chargerY, boxSize, boxSize)) {
            isCharged = true;
            chargeTimeLeft = MAX_CHARGE_TIME;
        }

        // 6. Beacon Collision (Win Rule)
        if (collides(playerX, playerY, playerSize, playerSize, beaconX, beaconY, boxSize, boxSize) && isCharged) {
            hasWon = true;
            statusLabel.setText("🎉 SIGNAL SENT! YOU WIN! Click Restart to play again.");
        }
    }

    private void draw(GraphicsContext gc, double width, double height) {
        // Background
        gc.setFill(Color.web("#1e1e2e"));
        gc.fillRect(0, 0, width, height);

        // Charger (Yellow)
        gc.setFill(Color.GOLD);
        gc.fillRect(chargerX, chargerY, boxSize, boxSize);
        gc.setFill(Color.WHITE);
        gc.fillText("Charger", chargerX - 2, chargerY - 6);

        // Beacon (Green when unlocked, Gray when locked)
        gc.setFill(isCharged ? Color.LIMEGREEN : Color.GRAY);
        gc.fillRect(beaconX, beaconY, boxSize, boxSize);
        gc.setFill(Color.WHITE);
        gc.fillText("Beacon", beaconX, beaconY - 6);

        // Obstacles (Red)
        gc.setFill(Color.CRIMSON);
        gc.fillRect(wallX, wallY, wallW, wallH);
        gc.fillRect(hazardX, hazardY, hazardSize, hazardSize);

        // Player Bot
        gc.setFill(isCharged ? Color.CYAN : Color.LIGHTGRAY);
        gc.fillRect(playerX, playerY, playerSize, playerSize);

        // Shrinking Battery Bar above player
        if (isCharged && !hasWon) {
            double barWidth = (chargeTimeLeft / (double) MAX_CHARGE_TIME) * playerSize;
            gc.setFill(Color.LIMEGREEN);
            gc.fillRect(playerX, playerY - 8, barWidth, 5);
        }
    }

    // Simple rectangle overlap check
    private boolean collides(double x1, double y1, double w1, double h1,
                             double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    public static void main(String[] args) { launch(args); }
}