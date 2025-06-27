import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Клас для зберігання налаштувань гри
class GameSettings {
    private int boardSize;
    private String player1Name;
    private String player2Name;

    public GameSettings() {
        this.boardSize = 3;
        this.player1Name = "Player 1";
        this.player2Name = "Player 2";
    }

    public GameSettings(int boardSize, String player1Name, String player2Name) {
        this.boardSize = boardSize;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    // Getters
    public int getBoardSize() { return boardSize; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }

    // Setters
    public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
    public void setPlayer1Name(String player1Name) { this.player1Name = player1Name; }
    public void setPlayer2Name(String player2Name) { this.player2Name = player2Name; }

    // Метод для створення рядка конфігурації
    public String toConfigString() {
        StringBuilder sb = new StringBuilder();
        sb.append("boardSize=").append(boardSize).append("\n");
        sb.append("player1=").append(player1Name).append("\n");
        sb.append("player2=").append(player2Name).append("\n");
        return sb.toString();
    }

    // Метод для парсингу рядка конфігурації
    public void parseConfigLine(String line) {
        String[] parts = line.split("=", 2);
        if (parts.length == 2) {
            String key = parts[0];
            String value = parts[1];

            switch (key) {
                case "boardSize" -> this.boardSize = Integer.parseInt(value);
                case "player1" -> this.player1Name = value;
                case "player2" -> this.player2Name = value;
            }
        }
    }
}

// Клас для зберігання статистики однієї гри
class GameStatistic {
    private String timestamp;
    private int boardSize;
    private String player1Name;
    private String player2Name;
    private String winner;

    public GameStatistic(GameSettings settings, String winner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = LocalDateTime.now().format(formatter);
        this.boardSize = settings.getBoardSize();
        this.player1Name = settings.getPlayer1Name();
        this.player2Name = settings.getPlayer2Name();
        this.winner = winner;
    }

    // Getters
    public String getTimestamp() { return timestamp; }
    public int getBoardSize() { return boardSize; }
    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public String getWinner() { return winner; }

    // Метод для перетворення в рядок для збереження
    public String toStatString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Дата: ").append(timestamp).append("\n");
        sb.append("Розмір дошки: ").append(boardSize).append("x").append(boardSize).append("\n");
        sb.append("Гравець 1: ").append(player1Name).append(" (X)\n");
        sb.append("Гравець 2: ").append(player2Name).append(" (O)\n");
        sb.append("Переможець: ").append(winner).append("\n");
        return sb.toString();
    }
}

// Клас для управління колекцією статистик
class GameStatistics {
    private List<GameStatistic> statistics;

    public GameStatistics() {
        this.statistics = new ArrayList<>();
    }

    public void addStatistic(GameStatistic statistic) {
        statistics.add(statistic);
    }

    public List<GameStatistic> getStatistics() {
        return statistics;
    }

    public String getAllStatisticsString() {
        StringBuilder sb = new StringBuilder();
        for (GameStatistic stat : statistics) {
            sb.append(stat.toStatString()).append("\n");
        }
        return sb.toString();
    }
}

// Клас для представлення ігрового поля
class GameBoard {
    private char[][] displayBoard;
    private int size;
    private int rows;
    private int cols;

    public GameBoard(int size) {
        this.size = size;
        this.rows = size * 2 + 1;
        this.cols = size * 4 - 1;
        this.displayBoard = createBoard();
    }

    private char[][] createBoard() {
        char[][] board = new char[rows][cols];

        // Ініціалізація порожніми символами
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = ' ';
            }
        }

        // Додавання номерів рядків та колонок
        for (int i = 0; i < size; i++) {
            board[0][i * 4 + 2] = (char) ('1' + i);
            board[i * 2 + 2][0] = (char) ('1' + i);
        }

        // Додавання горизонтальних ліній
        for (int i = 1; i < rows; i += 2) {
            for (int j = 1; j < cols; j++) {
                board[i][j] = '-';
            }
        }

        // Додавання вертикальних ліній
        for (int i = 0; i < rows; i++) {
            for (int j = 4; j < cols; j += 4) {
                board[i][j - 1] = '|';
            }
        }

        return board;
    }

    public void display() {
        for (char[] rowArray : displayBoard) {
            System.out.println(rowArray);
        }
    }

    public boolean isValidMove(int row, int col) {
        return row >= 1 && row <= size &&
                col >= 1 && col <= size &&
                displayBoard[(row - 1) * 2 + 2][(col - 1) * 4 + 2] == ' ';
    }

    public void makeMove(int row, int col, char player) {
        int displayRow = (row - 1) * 2 + 2;
        int displayCol = (col - 1) * 4 + 2;
        displayBoard[displayRow][displayCol] = player;
    }

    public boolean checkWin(char player) {
        // Перевірка рядків
        for (int i = 2; i < rows; i += 2) {
            int count = 0;
            for (int j = 2; j < cols; j += 4) {
                if (displayBoard[i][j] == player) {
                    count++;
                    if (count == size) return true;
                } else {
                    count = 0;
                }
            }
        }

        // Перевірка колонок
        for (int j = 2; j < cols; j += 4) {
            int count = 0;
            for (int i = 2; i < rows; i += 2) {
                if (displayBoard[i][j] == player) {
                    count++;
                    if (count == size) return true;
                } else {
                    count = 0;
                }
            }
        }

        // Перевірка головної діагоналі
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (displayBoard[i * 2 + 2][i * 4 + 2] == player) {
                count++;
                if (count == size) return true;
            } else {
                count = 0;
            }
        }

        // Перевірка побічної діагоналі
        count = 0;
        for (int i = 0; i < size; i++) {
            if (displayBoard[i * 2 + 2][(size - 1 - i) * 4 + 2] == player) {
                count++;
                if (count == size) return true;
            } else {
                count = 0;
            }
        }

        return false;
    }

    public boolean checkDraw() {
        for (int i = 2; i < rows; i += 2) {
            for (int j = 2; j < cols; j += 4) {
                if (displayBoard[i][j] == ' ') return false;
            }
        }
        return true;
    }

    public int getSize() { return size; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
}

public class pract17 {
    private static Scanner arbuz = new Scanner(System.in);
    private static GameSettings gameSettings = new GameSettings();
    private static final String CONFIG_FILE = "config.txt";
    private static final String STATS_FILE = "statistics.txt";

    public static void main(String[] args) {
        loadConfiguration();
        mainGameLoop();
        arbuz.close();
        System.out.println("Вихід");
    }

    private static void mainGameLoop() {
        boolean codeisrunning = true;
        while (codeisrunning) {
            displayMainMenu();
            if (!arbuz.hasNextLine()) {
                System.out.println("Спробуйте ще раз.");
                continue;
            }

            String input = arbuz.nextLine();
            if (input.isEmpty()) {
                System.out.println("Спробуйте ще раз.");
                continue;
            }

            char choice = input.charAt(0);
            switch (choice) {
                case '1' -> handleGameMenu();
                case '2' -> handleSettingsMenu();
                case '3' -> showStatistics();
                case '4' -> codeisrunning = handleExitMenu();
                default -> System.out.println("Спробуйте ще раз.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("""
                Головне меню
                1. Почати гру
                2. Налаштування
                3. Статистика
                4. Вихід""");
    }

    private static void handleGameMenu() {
        boolean inGameMenu = true;
        while (inGameMenu) {
            System.out.println(": Розмір дошки: " + gameSettings.getBoardSize() + "x" + gameSettings.getBoardSize());
            System.out.println("Гравці: " + gameSettings.getPlayer1Name() + " (X) проти " + gameSettings.getPlayer2Name() + " (O)");
            System.out.println("Готові? (1) Так! (2) Головне меню");

            String input = arbuz.nextLine();
            if (input.isEmpty()) {
                System.out.println("Спробуйте ще раз.");
                continue;
            }

            char choice = input.charAt(0);
            if (choice == '2') {
                inGameMenu = false;
            } else if (choice == '1') {
                playGame();
                inGameMenu = false;
            } else {
                System.out.println("Спробуйте ще раз.");
            }
        }
    }

    private static void playGame() {
        GameBoard board = new GameBoard(gameSettings.getBoardSize());
        char currentPlayer = 'X';
        boolean isGameOver = false;
        String winner = null;
        String currentPlayerName = gameSettings.getPlayer1Name();

        while (!isGameOver) {
            System.out.println("\nГрають: " + currentPlayerName + " (" + currentPlayer + ")");
            board.display();

            int[] move = getPlayerMove(board);
            if (move[0] == 0) {
                break;
            }

            board.makeMove(move[0], move[1], currentPlayer);

            if (board.checkWin(currentPlayer)) {
                System.out.println("Переміг " + currentPlayerName + " (" + currentPlayer + ") !!!");
                winner = currentPlayerName;
                isGameOver = true;
            } else if (board.checkDraw()) {
                System.out.println("Нічия");
                winner = "Draw";
                isGameOver = true;
            }

            if (currentPlayer == 'X') {
                currentPlayer = 'O';
                currentPlayerName = gameSettings.getPlayer2Name();
            } else {
                currentPlayer = 'X';
                currentPlayerName = gameSettings.getPlayer1Name();
            }
        }

        System.out.println("\nДошка зараз така:");
        board.display();

        if (winner != null) {
            saveGameStatistics(winner);
        }
    }

    private static int[] getPlayerMove(GameBoard board) {
        while (true) {
            System.out.println("Ряд (1-" + board.getSize() + ", або 0 щоб вийти):");
            String input = arbuz.nextLine();
            if (input.isEmpty()) continue;
            int row = input.charAt(0) - '0';

            if (row == 0) return new int[]{0, 0};

            System.out.println("Колонка (1-" + board.getSize() + "):");
            input = arbuz.nextLine();
            if (input.isEmpty()) continue;
            int col = input.charAt(0) - '0';

            if (board.isValidMove(row, col)) {
                return new int[]{row, col};
            }
            System.out.println("Спробуйте ще раз.");
        }
    }

    private static void handleSettingsMenu() {
        boolean inSettingsMenu = true;
        while (inSettingsMenu) {
            System.out.println("""
                Меню налаштувань
                1. Розмір дошки
                2. Ім'я гравця
                0. Головне меню""");

            String input = arbuz.nextLine();
            if (input.isEmpty()) {
                System.out.println("Спробуйте ще раз.");
                continue;
            }

            char choice = input.charAt(0);
            switch (choice) {
                case '1' -> changeBoardSize();
                case '2' -> changePlayerNames();
                case '0' -> inSettingsMenu = false;
                default -> System.out.println("Спробуйте ще раз.");
            }

            if (choice == '1' || choice == '2') {
                saveConfiguration();
            }
        }
    }

    private static void changeBoardSize() {
        System.out.println("""
            Розмір дошки:
            1. 3x3
            2. 5x5
            3. 7x7
            4. 9x9
            0. Скасувати""");

        String input = arbuz.nextLine();
        if (input.isEmpty()) {
            System.out.println("Спробуйте ще раз.");
            return;
        }

        char choice = input.charAt(0);
        switch (choice) {
            case '1' -> gameSettings.setBoardSize(3);
            case '2' -> gameSettings.setBoardSize(5);
            case '3' -> gameSettings.setBoardSize(7);
            case '4' -> gameSettings.setBoardSize(9);
            case '0' -> { return; }
            default -> {
                System.out.println("Спробуйте ще раз.");
                return;
            }
        }
        System.out.println("Встановлено розмір " + gameSettings.getBoardSize() + "x" + gameSettings.getBoardSize());
    }

    private static void changePlayerNames() {
        System.out.println("Введіть ім'я для гравця 1 (X): ");
        String input = arbuz.nextLine();
        if (!input.isEmpty()) {
            gameSettings.setPlayer1Name(input);
        }

        System.out.println("Введіть ім'я для гравця 2 (O): ");
        input = arbuz.nextLine();
        if (!input.isEmpty()) {
            gameSettings.setPlayer2Name(input);
        }

        System.out.println("Імена гравців оновлено: " + gameSettings.getPlayer1Name() + " (X) проти " + gameSettings.getPlayer2Name() + " (O)");
    }

    private static boolean handleExitMenu() {
        System.out.println("Впевнені? (1(Так) або 2(Ні)");
        String input = arbuz.nextLine();
        if (input.isEmpty()) {
            System.out.println("Спробуйте ще раз.");
            return true;
        }

        char choice = input.charAt(0);
        if (choice == '1') {
            return false;
        } else if (choice == '2') {
            System.out.println("Повернення в головне меню");
            return true;
        } else {
            System.out.println("Спробуйте ще раз");
            return true;
        }
    }

    private static void saveConfiguration() {
        try {
            Path path = Paths.get(CONFIG_FILE);
            Files.writeString(path, gameSettings.toConfigString());
            System.out.println("Конфігурація збережена");
        } catch (IOException e) {
            System.out.println("Помилка збереження конфігурації: " + e.getMessage());
        }
    }

    private static void loadConfiguration() {
        Path path = Paths.get(CONFIG_FILE);
        if (!Files.exists(path)) {
            System.out.println("Файл конфігурації не знайдено.");
            return;
        }

        try {
            BufferedReader reader = Files.newBufferedReader(path);
            String line;
            while ((line = reader.readLine()) != null) {
                gameSettings.parseConfigLine(line);
            }
            reader.close();
            System.out.println("Конфігурацію успішно завантажено.");
        } catch (IOException e) {
            System.out.println("Помилка завантаження конфігурації:" + e.getMessage());
        }
    }

    private static void saveGameStatistics(String winner) {
        try {
            GameStatistic statistic = new GameStatistic(gameSettings, winner);

            Path path = Paths.get(STATS_FILE);
            String content = statistic.toStatString();

            if (!Files.exists(path)) {
                Files.writeString(path, content);
            } else {
                Files.writeString(path, content, StandardOpenOption.APPEND);
            }

            System.out.println("Статистика гри збережена.");
        } catch (IOException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private static void showStatistics() {
        Path path = Paths.get(STATS_FILE);
        if (!Files.exists(path)) {
            System.out.println("Статистики не знайдено");
            return;
        }

        System.out.println("\nІгрова статистика");
        try {
            String content = Files.readString(path);
            System.out.println(content);

            System.out.println("\nНатисність Enter");
            arbuz.nextLine();
        } catch (IOException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }
}