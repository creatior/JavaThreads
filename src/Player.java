import java.awt.Color;
import java.util.Random;

public class Player extends Thread {
    private final int playerId;
    private final GameBoard board;
    private final GameLog log;
    private final GameSettings settings;
    private final Random random = new Random();
    private volatile boolean running = true;
    private int tokensPlaced = 0;
    private int lastCellIndex = -1; // Для последовательного режима

    public Player(int playerId, GameBoard board, GameLog log, GameSettings settings) {
        this.playerId = playerId;
        this.board = board;
        this.log = log;
        this.settings = settings;
    }

    public void stopPlayer() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running && tokensPlaced < GameSettings.TOKENS_PER_PLAYER && !board.isBoardFull()) {
            if (settings.getMode() == GameMode.SEQUENTIAL) {
                placeSequentialToken();
            } else {
                placeRandomToken();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void placeSequentialToken() {
        synchronized (board) {
            try {
                while (GameController.getCurrentPlayer() != playerId && !board.isBoardFull()) {
                    board.wait();
                }
                if (board.isBoardFull()) return;

                // Ищем следующую свободную ячейку
                int cellIndex = findNextEmptyCell();
                if (cellIndex != -1 && board.placeToken(cellIndex, playerId,
                        GameSettings.PLAYER_COLORS[playerId])) {

                    tokensPlaced++;
                    lastCellIndex = cellIndex;
                    log.addMessage(String.format("Игрок %d (%s), фишка %d в ячейку %d",
                            playerId + 1, getColorName(), tokensPlaced, cellIndex + 1));

                    GameController.nextPlayer();
                    board.notifyAll();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void placeRandomToken() {
        int attempts = 0;
        while (attempts < GameSettings.BOARD_SIZE * 2) {
            int cellIndex = random.nextInt(GameSettings.BOARD_SIZE);
            if (board.placeToken(cellIndex, playerId, GameSettings.PLAYER_COLORS[playerId])) {
                tokensPlaced++;
                log.addMessage(String.format("Игрок %d (%s), фишка %d в ячейку %d",
                        playerId + 1, getColorName(), tokensPlaced, cellIndex + 1));
                return;
            }
            attempts++;
        }
    }

    private int findNextEmptyCell() {
        // Начинаем поиск с последней занятой ячейки + 1
        int startIndex = lastCellIndex + 1;
        for (int i = 0; i < GameSettings.BOARD_SIZE; i++) {
            int currentIndex = (startIndex + i) % GameSettings.BOARD_SIZE;
            if (board.isCellEmpty(currentIndex)) {
                return currentIndex;
            }
        }
        return -1;
    }

    private String getColorName() {
        Color color = GameSettings.PLAYER_COLORS[playerId];
        if (color.equals(Color.RED)) return "Красный";
        if (color.equals(Color.BLUE)) return "Синий";
        if (color.equals(Color.GREEN)) return "Зеленый";
        if (color.equals(Color.YELLOW)) return "Желтый";
        return "Неизвестный";
    }
}