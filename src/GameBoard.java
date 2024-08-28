import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GameBoard {
    // Game Rules and Settings
    private final PrimaryRulesRecord primaryRules;
    private final SpecialRulesRecord specialRules;
    // Game Board
    private BoardFrame boardFrame;
    private JLabel[][] cells;
    // Players
    private String[] playersTag;
    private final String[] playersName;
    private int[] playersPosition;
    private int nextPlayer = 0; // indicates the number of the player who must roll the dices
    // Game Log
    private DefaultTableModel playersTable;  // shows ID, name and position of every player
    private JTextArea gameLog;  // Area di testo per il resoconto del gioco

    public GameBoard(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;

        playersName = new String[primaryRules.nPlayers()]; // da cambiare con il passaggio dei nomi da GameConfiguration

        initializePlayersTag();

        boardFrame = new BoardFrame();
        boardFrame.setVisible(true);

        startGame();
    }

    private void initializePlayersTag() {
        playersTag = new String[primaryRules.nPlayers()];
        for(int i=0; i<primaryRules.nPlayers(); i++){
            String id = "👤"+(i+1);
            playersTag[i] = id;
        }

        playersPosition = new int[primaryRules.nPlayers()];
    }

    private class insertNameFrame extends JFrame {
        public insertNameFrame(int i) {

        }
    }

    private class BoardFrame extends JFrame {
        public BoardFrame() {
            // initialize the frame
            setTitle("Scale e Serpenti");
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout()); // set layout of the container

            // create the board with GridLayout
            JPanel boardPanel = new JPanel(new GridLayout(primaryRules.nRows(), primaryRules.nCols()));
            cells = new JLabel[primaryRules.nRows()][primaryRules.nCols()]; // create matrix of labels
            fillTheBoard(boardPanel);
            add(boardPanel, BorderLayout.CENTER);

            // creates a bottom panel which will hold the buttons
            JPanel bottomPanel = buildBottomPanel();
            add(bottomPanel, BorderLayout.SOUTH);

            // create a side panel to show game log and players table
            JPanel sidePanel = buildSidePanel();
            add(sidePanel, BorderLayout.EAST);
        }

        private void fillTheBoard(JPanel boardPanel) {
            int rows = primaryRules.nRows();
            int cols = primaryRules.nCols();
            // fill the board from last to first cell
            for(int i = 0; i < rows; i++) {
                for(int j = 0; j < cols; j++) {
                    int cellLabel = indexPosition(i, j);
                    String text = "<html>" + cellLabel + ")</html>";
                    cells[i][j] = new JLabel(text, SwingConstants.CENTER);
                    cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    //cells[i][j].setOpaque(true); //CONTROLLA POI
                    boardPanel.add(cells[i][j]);
                }
            }
        }

        private int indexPosition(int x, int y) {
            int rows = primaryRules.nRows();
            int cols = primaryRules.nCols();
            // the starting point must always be in the bottom-left corner
            int rowFromBottom = rows - 1 - x;  // Number of the row counting from the bottom
            boolean isEvenRow = rowFromBottom % 2 == 0; // even row counting from the bottom
            int index;
            if (isEvenRow) {
                index = (rowFromBottom * cols) + y + 1; // counting from left to right
            } else {
                index = (rowFromBottom * cols) + (cols - y); // counting from right to left
            }
            return index;
        }

        private int[] findCoordinates(int index) {
            int rows = primaryRules.nRows();
            int cols = primaryRules.nCols();
            int rowFromBottom = (index - 1) / cols; // Number of the row counting from the bottom
            int x = rows - 1 - rowFromBottom;       // x coordinate
            boolean isEvenRow = rowFromBottom % 2 == 0; // even row counting from the bottom
            int y;
            if (isEvenRow) {
                y = (index - 1) % cols; // counting from left to right
            } else {
                y = cols - 1 - ((index - 1) % cols); // counting from right to left
            }
            return new int[]{x, y};
        }

        private JLabel getCellLabel(int i) {
            int[] cord = findCoordinates(i);
            return cells[cord[0]][cord[1]];
        }

        private JPanel buildBottomPanel() {
            JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            // add exit button
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> exitGame());
            buttonsPanel.add(exitButton);
            // add rules button which shows the rules of the game when clicked
            JButton rulesButton = new JButton("Rules");
            rulesButton.addActionListener(e -> showRules());
            buttonsPanel.add(rulesButton);
            // add a button to roll the dice (only if autoAdvance is off)
            if( !specialRules.autoAdvance() ) {
                JButton rollButton = new JButton("Roll Dice");
                rollButton.addActionListener(e -> rollDice());
                buttonsPanel.add(rollButton);
            }
            return buttonsPanel;
        }

        private void showRules() {
            // TO-DO
        }

        private void exitGame() {
            int result = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to exit the game?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                new GameConfiguration();
                this.dispose();
            }
        } // goes back to main menu

        private JPanel buildSidePanel() {
            JPanel sidePanel = new JPanel();
            sidePanel.setLayout(new BorderLayout());
            sidePanel.setPreferredSize(new Dimension(250, getHeight()));

            JScrollPane pTable = buildPlayersTable(); // create players table
            sidePanel.add(pTable, BorderLayout.NORTH); // add the table to the side panel

            JScrollPane logScrollPane = buildGameLog();
            sidePanel.add(logScrollPane, BorderLayout.CENTER);

            return sidePanel;
        }

        private JScrollPane buildGameLog() {
            gameLog = new JTextArea();
            gameLog.setEditable(false);
            return new JScrollPane(gameLog);
        }

        private JScrollPane buildPlayersTable() {
            // Side table with players ID and name
            String[] columnNames = {"Tag", "Name", "Position"};
            Object[][] data = new Object[primaryRules.nPlayers()][3];
            for (int i = 0; i < primaryRules.nPlayers(); i++) {
                data[i][0] = playersTag[i];
                data[i][1] = playersName[i];
                data[i][2] = playersPosition[i];
            }
            playersTable = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(playersTable); // contains the default table model
            return new JScrollPane(table); // JScrollPane makes the table scrollable
        }

        private void initializePlayersPosition() {
            for(int i=0; i<primaryRules.nPlayers(); i++){
                playersPosition[i] = 1; // the starting point for every player is cell 1
                boardFrame.updatePlayerPosition(i, 1);
            }
        }

        private void nextTurn() {
            nextPlayer = (nextPlayer +1) % primaryRules.nPlayers();  // updates the turn
            gameLog.append("Next Player: " + playersTag[nextPlayer] + ".\n");
        }

        private void rollDice(){
            int currentPlayer = nextPlayer;
            int newPosition = calculateNewPosition(currentPlayer);
            movePlayer(currentPlayer, newPosition);
            checkTile(currentPlayer, newPosition); // check for snakes, ladders, special tiles or final cell

            nextTurn();
        }

        private int calculateNewPosition(int currentPlayer) {
            // roll the dices
            int diceSum = 0; // sums the total of nDices
            for (int i = 0; i < primaryRules.nDices(); i++) {
                diceSum += (int) (Math.random()*7);
            }
            // calculate the new position
            int currentPosition = playersPosition[currentPlayer];
            int newPosition = currentPosition + diceSum;
            int finalCell = primaryRules.nRows()*primaryRules.nCols();
            if (newPosition > finalCell) { // manage overshoot
                newPosition = finalCell - (newPosition - finalCell);
            }
            return newPosition;
        }

        private void movePlayer(int playerIndex, int newPosition) {
            updatePlayerPosition(playerIndex, newPosition);
            updatePlayersTableAndGameLog(playerIndex, newPosition);
        }

        private void updatePlayerPosition(int playerIndex, int newPosition) {
            // remove player from the old cell
            int oldPosition = playersPosition[playerIndex];
            if(oldPosition >= 1) {
                JLabel cell = getCellLabel(oldPosition);
                StringBuilder newText = new StringBuilder("<html>"+oldPosition + ")");
                for (int otherPlayer = 0; otherPlayer < primaryRules.nPlayers(); otherPlayer++) {
                    if (otherPlayer != playerIndex && playersPosition[otherPlayer] == oldPosition)
                        newText.append(" ").append(playersTag[otherPlayer]); // the other players must be re-added if they are still in the old position
                }
                newText.append("</html>");
                cell.setText(newText.toString());
            }
            // add the player to the new position
            playersPosition[playerIndex] = newPosition;
            JLabel cell = getCellLabel(newPosition);
            StringBuilder newText = new StringBuilder("<html>"+newPosition + ")");
            for(int player = 0; player < primaryRules.nPlayers(); player++) {
                if(playersPosition[player] == newPosition)
                    newText.append(" ").append(playersTag[player]); // the other players must be re-added if they were in this cell
            }
            newText.append("</html>");
            cell.setText(newText.toString());
        }

        private void updatePlayersTableAndGameLog(int playerIndex, int newPosition) {
            playersTable.setValueAt(newPosition, playerIndex, 2);
            gameLog.append("Player " + playersTag[playerIndex] + " moves to cell " + newPosition + ".\n");
        }

        private void checkTile(int currentPlayer, int position) {
            int finalCell = primaryRules.nRows()*primaryRules.nCols();
            if(position == finalCell) {
                endGame(currentPlayer);
            }

        }

        private void endGame(int currentPlayer) {
            String winnerMessage = playersName[currentPlayer] + " wins the game!";
            JOptionPane.showMessageDialog(this, winnerMessage, "Winner!", JOptionPane.INFORMATION_MESSAGE);
            // exit or restart the game
            int response = JOptionPane.showConfirmDialog(this, "Do you want to restart this match?", "Restart?", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                new GameBoard(primaryRules, specialRules);  // reset this game with the same configuration
                boardFrame.dispose();
            } else {
                new GameConfiguration(); // goes back to main menu
                boardFrame.dispose();
            }
        }

    }

    private void startGame() {
        boardFrame.initializePlayersPosition();
        gameLog.append("Next Player: " + playersTag[0] + ".\n");
    }
}
