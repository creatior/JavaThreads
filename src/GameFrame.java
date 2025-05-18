import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameFrame extends JFrame {
    private final GameBoard board;
    private final GameLog log;
    private final GameSettings settings;
    private static Player[] players;

    public GameFrame() {
        settings = new GameSettings(GameMode.SEQUENTIAL);
        setTitle("Лото");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        board = new GameBoard();
        log = new GameLog();

        // Инициализация игроков с передачей settings
        players = new Player[GameSettings.PLAYERS_COUNT];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i, board, log, settings);
        }

        // Панель управления
        JPanel controlPanel = new JPanel();
        JButton switchModeBtn = new JButton("Переключить режим");
        switchModeBtn.addActionListener(this::switchGameMode);
        controlPanel.add(switchModeBtn);

        JButton startBtn = new JButton("Начать игру");
        startBtn.addActionListener(e -> startGame());
        controlPanel.add(startBtn);

        // Основная панель
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(board, BorderLayout.CENTER);
        mainPanel.add(log, BorderLayout.EAST);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void startGame() {
        stopCurrentPlayers();
        GameController.resetGame();
        log.clear();
        board.resetBoard();
        initializePlayers();

        for (Player player : players) {
            player.start();
        }
    }

    public static Player[] getPlayers() {
        return players;
    }

    private void stopCurrentPlayers() {
        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    player.stopPlayer();
                }
            }
        }
    }

    private void initializePlayers() {
        players = new Player[GameSettings.PLAYERS_COUNT];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(i, board, log, settings);
        }
    }

    private void switchGameMode(ActionEvent e) {
        GameMode newMode = settings.getMode() == GameMode.SEQUENTIAL
                ? GameMode.RANDOM : GameMode.SEQUENTIAL;
        settings.setMode(newMode);
        log.clear();
        log.addMessage("Режим изменён на: " +
                (newMode == GameMode.SEQUENTIAL ? "Последовательный" : "Случайный"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame game = new GameFrame();
            game.setVisible(true);
        });
    }
}