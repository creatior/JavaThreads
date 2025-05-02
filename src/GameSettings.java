import java.awt.*;

public class GameSettings {
    public static final int PLAYERS_COUNT = 4;
    public static final int TOKENS_PER_PLAYER = 10;
    public static final int BOARD_SIZE = 30;
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