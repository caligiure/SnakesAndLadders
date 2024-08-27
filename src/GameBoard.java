import javax.swing.*;
import java.awt.*;

public class GameBoard {
    // Game Rules and Settings
    private final PrimaryRulesRecord primaryRules;
    private final SpecialRulesRecord specialRules;
    // Game Board
    private BoardFrame boardFrame;
    private JLabel[][] cells;
    // Players
    private String[] playersName;
    private int[] playersPosition;
    private int currentTurn; // indicates the number of the player who must roll the dices

    public GameBoard(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;
        playersName = new String[primaryRules.nPlayers()]; // da cambiare con il passaggio dei nomi da GameConfiguration
        playersPosition = new int[primaryRules.nPlayers()];
        boardFrame = new BoardFrame();
        boardFrame.setVisible(true);
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
            // add the board component to the container
            add(boardPanel, BorderLayout.CENTER);

            // creates a panel which will hold all the buttons
            JPanel buttonsPanel = getButtonsPanel();
            // add the buttons panel to the frame
            add(buttonsPanel, BorderLayout.SOUTH);

            // add the players to the board
            initializePlayers();
        }

        private JPanel getButtonsPanel() {
            JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            // add exit button
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> exitGame());
            buttonsPanel.add(exitButton);
            // add a button to roll the dice (only if autoAdvance is off)
            if( !specialRules.autoAdvance() ) {
                JButton rollButton = new JButton("Roll Dice");
                rollButton.addActionListener(e -> nextTurn());
                buttonsPanel.add(rollButton);
            }
            return buttonsPanel;
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
        }

        private void fillTheBoard(JPanel boardPanel) {
            int rows = primaryRules.nRows();
            int cols = primaryRules.nCols();
            // fill the board from last to first cell
            for(int i = 0; i < rows; i++) {
                for(int j = 0; j < cols; j++) {
                    int cellLabel = indexPosition(i, j);
                    cells[i][j] = new JLabel(String.valueOf(cellLabel), SwingConstants.CENTER);
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

        private void initializePlayers() {
            for(int i=0; i<primaryRules.nPlayers(); i++){
                String name = "👤"+(i+1); // questo non sarà necessario dopo aver importato i giocatori da GameConfig
                playersName[i] = name;
                playersPosition[i] = 1; // the starting point for every player is cell 1
                updatePlayerPosition(i, 1);
            }
        }

        private void updatePlayerPosition(int playerIndex, int newPosition) {
            // remove player from the old cell
            int oldPosition = playersPosition[playerIndex];
            if(oldPosition >= 1) {
                JLabel cell = getCellLabel(oldPosition);
                StringBuilder newText = new StringBuilder(oldPosition + ".");
                for (int i = 0; i < primaryRules.nPlayers(); i++) {
                    if (i != playerIndex)
                        newText.append(" ").append(playersName[i]); // the other players must be re-added if they were in this cell
                }
                cell.setText(newText.toString());
            }
            // add the player to the new position
            playersPosition[playerIndex] = newPosition;
            JLabel cell = getCellLabel(newPosition);
            StringBuilder newText = new StringBuilder(newPosition + ".");
            for(int i = 0; i < primaryRules.nPlayers(); i++) {
                newText.append(" ").append(playersName[i]); // the other players must be re-added if they were in this cell
            }
            cell.setText(newText.toString());
        }

        private void nextTurn() { // updates the turn and makes a player roll the dice
            int currentPlayer = currentTurn;
            currentTurn = (currentTurn+1)%primaryRules.nPlayers();
            rollDice(currentPlayer);
        }

        private void rollDice(int playerIndex){
            // roll the dices
            int diceSum = 0; // sums the total of nDices
            for (int i = 0; i < primaryRules.nDices(); i++) {
                diceSum += (int) (Math.random()*7);
            }
            // calculate the new position
            int currentPosition = playersPosition[playerIndex];
            int newPosition = currentPosition + diceSum;
            int finalPosition = primaryRules.nRows()*primaryRules.nCols();
            if (newPosition > finalPosition) { // manage overshoot
                newPosition = finalPosition - (newPosition - finalPosition);
            }
            updatePlayerPosition(playerIndex, newPosition);
        }

    }

}
