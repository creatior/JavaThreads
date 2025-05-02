import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameBoard extends JPanel {
    public static final int BOARD_SIZE = 30;
    public static final Color[] BOARD_COLORS = {
            Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY,
            Color.PINK, Color.ORANGE, Color.CYAN, Color.MAGENTA
    };

    private final JLabel[] cells = new JLabel[BOARD_SIZE];

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

    public synchronized boolean placeToken(int cellIndex, int playerId, Color playerColor) {
        if (cells[cellIndex].getText().isEmpty()) {
            cells[cellIndex].setText(String.valueOf(playerId + 1));
            cells[cellIndex].setForeground(playerColor);
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
            cells[i].setForeground(Color.BLACK);
        }
    }

    public synchronized boolean isCellEmpty(int cellIndex) {
        return cells[cellIndex].getText().isEmpty();
    }
}