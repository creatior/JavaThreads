import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameBoard extends JPanel {
    public static final int BOARD_SIZE = 30;
    public static final Color[] BOARD_COLORS = GameSettings.PLAYER_COLORS;

    private final JLabel[] cells = new JLabel[BOARD_SIZE];

    private int[] playerScores = new int[GameSettings.PLAYERS_COUNT];

    public GameBoard() {
        setLayout(new GridLayout(5, 6));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы
        initializeCells();
    }

    private void initializeCells() {
        Random random = new Random();
        for (int i = 0; i < BOARD_SIZE; i++) {
            cells[i] = new JLabel("", SwingConstants.CENTER);
            cells[i].setOpaque(true);
            cells[i].setBackground(BOARD_COLORS[random.nextInt(BOARD_COLORS.length)]);
            cells[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(cells[i]);
        }
    }

    public synchronized boolean placeToken(int cellIndex, int playerId, Color playerColor, GameMode mode) {
        if (cells[cellIndex].getText().isEmpty()) {
            // В последовательном режиме проверяем цвет клетки
            if (mode == GameMode.SEQUENTIAL && !cells[cellIndex].getBackground().equals(playerColor)) {
                return false;
            }

            cells[cellIndex].setText(String.valueOf(playerId + 1));
            cells[cellIndex].setForeground(Color.BLACK); // Фишки всегда черные для лучшей видимости
            playerScores[playerId]++;
            return true;
        }
        return false;
    }

    public boolean isBoardFull() {
        for (JLabel cell : cells) {
            if (cell.getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public synchronized void resetBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            cells[i].setText("");
            playerScores = new int[GameSettings.PLAYERS_COUNT];
        }
        Random random = new Random();
        for (JLabel cell : cells) {
            cell.setBackground(BOARD_COLORS[random.nextInt(BOARD_COLORS.length)]);
        }
    }

    public synchronized boolean isCellEmpty(int cellIndex) {
        return cells[cellIndex].getText().isEmpty();
    }

    public synchronized Color getCellColor(int cellIndex) {
        return cells[cellIndex].getBackground();
    }

    public int[] getPlayerScores() {
        return playerScores.clone();
    }

    public void announceWinner(GameLog log) {
        int maxScore = -1;
        int winnerId = -1;

        for (int i = 0; i < playerScores.length; i++) {
            if (playerScores[i] > maxScore) {
                maxScore = playerScores[i];
                winnerId = i;
            }
        }

        if (winnerId != -1) {
            log.addMessage("Победитель: Игрок " + (winnerId + 1) + " (" + getColorName(GameSettings.PLAYER_COLORS[winnerId]) +
                    ") с " + maxScore + " очками!");
        }
    }

    private String getColorName(Color color) {
        if (color.equals(Color.RED)) return "Красный";
        if (color.equals(Color.BLUE)) return "Синий";
        if (color.equals(Color.GREEN)) return "Зеленый";
        if (color.equals(Color.YELLOW)) return "Желтый";
        return "Неизвестный";
    }
}