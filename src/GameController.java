import java.util.concurrent.atomic.AtomicInteger;

public class GameController {
    private static final AtomicInteger currentPlayer = new AtomicInteger(0);

    public static int getCurrentPlayer() {
        return currentPlayer.get();
    }

    public static void nextPlayer() {
        currentPlayer.set((currentPlayer.get() + 1) % GameSettings.PLAYERS_COUNT);
    }

    public static void resetGame() {
        currentPlayer.set(0);
    }
}