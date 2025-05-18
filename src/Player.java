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

        if (tokensPlaced >= GameSettings.TOKENS_PER_PLAYER || board.isBoardFull()) {
            checkGameEnd();
        }
    }

    private boolean allPlayersFinished() {
        // Простая проверка - в реальной игре нужно более сложное решение
        for (Player player : GameFrame.getPlayers()) {
            if (player.tokensPlaced < GameSettings.TOKENS_PER_PLAYER) {
                return false;
            }
        }
        return true;
    }

    private void placeSequentialToken() {
        synchronized (board) {
            try {
                while (GameController.getCurrentPlayer() != playerId && !board.isBoardFull()) {
                    board.wait();
                }
                if (board.isBoardFull()) {
                    checkGameEnd();
                    return;
                }

                int cellIndex = findNextEmptyCellOfMyColor();

                if (cellIndex == -1) {
                    // Не нашли подходящую клетку - пропускаем ход
                    log.addMessage(String.format("Игрок %d (%s) не нашел подходящую клетку - пропуск хода",
                            playerId + 1, getColorName()));

                    GameController.nextPlayer();
                    board.notifyAll();
                    return;
                }

                if (board.placeToken(cellIndex, playerId,
                        GameSettings.PLAYER_COLORS[playerId], settings.getMode())) {

                    tokensPlaced++;
                    lastCellIndex = cellIndex;
                    log.addMessage(String.format("Игрок %d (%s), фишка %d в ячейку %d",
                            playerId + 1, getColorName(), tokensPlaced, cellIndex + 1));

                    if (tokensPlaced >= GameSettings.TOKENS_PER_PLAYER) {
                        checkGameEnd();
                    }

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
            if (board.placeToken(cellIndex, playerId, GameSettings.PLAYER_COLORS[playerId], settings.getMode())) {
                tokensPlaced++;
                log.addMessage(String.format("Игрок %d (%s), фишка %d в ячейку %d",
                        playerId + 1, getColorName(), tokensPlaced, cellIndex + 1));
                return;
            }
            attempts++;
        }
    }

    private int findNextEmptyCellOfMyColor() {
        Color myColor = GameSettings.PLAYER_COLORS[playerId];
        int attempts = 0;

        while (attempts < GameBoard.BOARD_SIZE) {
            int currentIndex = (lastCellIndex + 1 + attempts) % GameBoard.BOARD_SIZE;

            if (board.isCellEmpty(currentIndex)) {
                if (settings.getMode() == GameMode.SEQUENTIAL) {
                    if (board.getCellColor(currentIndex).equals(myColor)) {
                        return currentIndex;
                    }
                } else {
                    return currentIndex;
                }
            }
            attempts++;
        }

        return -1;
    }

    private void checkGameEnd() {
        // Проверяем, все ли игроки завершили ходить
        boolean allFinished = true;
        for (Player player : GameFrame.getPlayers()) {
            if (player.tokensPlaced < GameSettings.TOKENS_PER_PLAYER && player.isAlive()) {
                allFinished = false;
                break;
            }
        }

        if (allFinished || board.isBoardFull()) {
            board.announceWinner(log);
        }
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