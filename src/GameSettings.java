import java.awt.*;

public class GameSettings {
    public static final int PLAYERS_COUNT = 4;
    public static final int TOKENS_PER_PLAYER = 14;
    public static final int BOARD_ROWS = 6;
    public static final int BOARD_COLS = 8;
    public static final int BOARD_SIZE = BOARD_ROWS * BOARD_COLS;
    public static final Color[] PLAYER_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW
    };

    private GameMode mode;

    public GameSettings(GameMode mode) {
        this.mode = mode;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }
}