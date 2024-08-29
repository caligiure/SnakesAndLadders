import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

public class GameBoard {
    // Game Rules and Settings
    private final PrimaryRulesRecord primaryRules;
    private final SpecialRulesRecord specialRules;
    // Game Board
    private final BoardFrame boardFrame;
    private JLabel[][] cells;
    private final RollDiceListener rollDiceListener;
    // Players
    private String[] playersTag;
    private final String[] playersName;
    private int[] playersPosition;
    private int nextPlayer = 0; // indicates the number of the player who must roll the dices
    // Game Log
    private DefaultTableModel playersTable;  // shows ID, name and position of every player
    private JTextArea gameLog;  // Text area for gameLog
    // snakes and ladders
    private int[][] snakes, ladders;
    LinkedList<Integer> cellsContainingSomething; // indicates the indexes of the cells which contain a snake or a ladder or are special cells

    public GameBoard(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;

        playersName = new String[primaryRules.nPlayers()]; // da cambiare con l'inserimento dei nomi

        initializePlayersTag();

        rollDiceListener = new RollDiceListener();

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
    } // sets a tag for every player, which will be displayed on the board

    private class insertPlayerNamesFrame extends JFrame {
        public insertPlayerNamesFrame(int i) {

        }
    }

    private class BoardFrame extends JFrame {
        public BoardFrame() {
            initializeFrame(); // sets the name, size and layout of the frame

            JPanel boardPanel = buildBoardPanel(); // builds the panel which contains the cells of the board
            add(boardPanel, BorderLayout.CENTER);

            JPanel bottomPanel = buildBottomPanel(); // creates a bottom panel which will hold the buttons
            add(bottomPanel, BorderLayout.SOUTH);

            JPanel sidePanel = buildSidePanel(); // create a side panel to show game log and players table
            add(sidePanel, BorderLayout.EAST);

            initializeSnakesAndLadders();
            showSnakesAndLadders();
        }

        private void initializeFrame() {
            setTitle("Scale e Serpenti");
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout()); // set layout of the container
        } // sets the name, size and layout of the frame

        private JPanel buildBoardPanel() {
            // create the board with GridLayout
            JPanel boardPanel = new JPanel(new GridLayout(primaryRules.nRows(), primaryRules.nCols()));
            cells = new JLabel[primaryRules.nRows()][primaryRules.nCols()]; // create matrix of labels
            int rows = primaryRules.nRows();
            int cols = primaryRules.nCols();
            // fill the board from last to first cell
            for(int i = 0; i < rows; i++) {
                for(int j = 0; j < cols; j++) {
                    int cellLabel = indexPosition(i, j); // returns the index of the cell, given the coordinates od the cells matrix
                    String text = "<html>" + cellLabel + ")</html>";
                    cells[i][j] = new JLabel(text, SwingConstants.CENTER);
                    cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    //cells[i][j].setOpaque(true); //CONTROLLA POI
                    boardPanel.add(cells[i][j]);
                }
            }
            return boardPanel;
        } // builds the panel which contains the cells of the board

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
        } // returns the index of the cell, given the coordinates od the cells matrix

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
        } // returns an array containing the x and y coordinates of a cell in the cells matrix, given its index

        private JLabel getCellLabel(int i) {
            int[] cord = findCoordinates(i);
            return cells[cord[0]][cord[1]];
        } // returns a cell, given its index

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
                rollButton.addActionListener(rollDiceListener);
                buttonsPanel.add(rollButton);
            }
            return buttonsPanel;
        } // builds the bottom panel containing the buttons

        private void showRules() {
            // da fare in futuro
        } // shows the game rules

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
        } // builds the side panel containing the names table and the game log

        private JScrollPane buildGameLog() {
            gameLog = new JTextArea();
            gameLog.setEditable(false);
            return new JScrollPane(gameLog);
        } // builds a gameLog to show all the game infos

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
        } // builds a player to show every player's tag, name and position

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
        } // removes a player from the old cell and adds it to the new one

        private void updatePlayersTableAndGameLog(int playerIndex, int newPosition) {
            playersTable.setValueAt(newPosition, playerIndex, 2);
            gameLog.append("Player " + playersTag[playerIndex] + " moves to cell " + newPosition + ".\n");
        } // updates the position of the player on the playersTable and adds a new log on the gameLog regarding its movement

        private void initializeSnakesAndLadders() {
            Random random = new Random();
            int nRows = primaryRules.nRows();
            int nCols = primaryRules.nCols();
            // every row of a matrix represents a ladder/snake,
            // the first two columns of the row are the coordinates of the starting cell,
            // the second two columns of the row are the coordinates of the arrival cell
            ladders = new int[primaryRules.nLadders()][4];
            snakes = new int[primaryRules.nSnakes()][4];
            cellsContainingSomething = new LinkedList<>(); // indicates the indexes of the cells which contain a snake or a ladder or are special cells
            // choose randomly the starting and arrival point of every ladder, avoiding the cells that already contain something
            // the first and last cell of the board cannot contain ladders
            for(int i=0; i<primaryRules.nLadders(); i++) {
                int startingPoint = random.nextInt(2, nRows*nCols-1); // the second-last cell can't be a starting point for a ladder
                while( cellsContainingSomething.contains(startingPoint) ) {
                    startingPoint = random.nextInt(2, nRows*nCols-1);
                }
                cellsContainingSomething.add(startingPoint);
                int[] startingCoordinates = findCoordinates(startingPoint);
                ladders[i][0] = startingCoordinates[0]; // x coordinate
                ladders[i][1] = startingCoordinates[1]; // y coordinate
                // arrivalPoint must be after startingPoint
                int arrivalPoint = random.nextInt(startingPoint+1, nRows*nCols);
                while( cellsContainingSomething.contains(arrivalPoint) ) {
                    arrivalPoint = random.nextInt(startingPoint+1, nRows*nCols);
                }
                cellsContainingSomething.add(arrivalPoint);
                int[] arrivalCoordinates = findCoordinates(arrivalPoint);
                ladders[i][2] = arrivalCoordinates[0]; // x
                ladders[i][3] = arrivalCoordinates[1]; // y
            }
            // choose randomly the head and tail point of every snake, avoiding the cells that already contain something
            for(int i=0; i<primaryRules.nSnakes(); i++) {
                int headPoint = random.nextInt(1, nRows*nCols); // the first and final cells cannot be a head point for a snake
                while( cellsContainingSomething.contains(headPoint) ) {
                    headPoint = random.nextInt(1, nRows*nCols);
                }
                cellsContainingSomething.add(headPoint);
                int[] headCoordinates = findCoordinates(headPoint);
                snakes[i][0] = headCoordinates[0]; // x coordinate
                snakes[i][1] = headCoordinates[1]; // y coordinate
                // tailPoint must be before headPoint
                int tailPoint = random.nextInt(1, headPoint);
                while( cellsContainingSomething.contains(tailPoint) ) {
                    tailPoint = random.nextInt(1, headPoint);
                }
                cellsContainingSomething.add(tailPoint);
                int[] tailCoordinates = findCoordinates(tailPoint);
                ladders[i][2] = tailCoordinates[0]; // x
                ladders[i][3] = tailCoordinates[1]; // y
            }
        } // choose randomly the position for every ladder and snake

        private void showSnakesAndLadders() {
            // lofarò
        }
    }

    private void startGame() {
        initializePlayersPosition();
        gameLog.append("Next Player: " + playersTag[0] + ".\n");
    }

    private void initializePlayersPosition() {
        for(int i=0; i<primaryRules.nPlayers(); i++){
            playersPosition[i] = 1; // the starting point for every player is cell 1
            boardFrame.updatePlayerPosition(i, 1);
        }
    } // sets the starting position ov every player

    private class RollDiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            rollDice();
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
            boardFrame.updatePlayerPosition(playerIndex, newPosition);
            boardFrame.updatePlayersTableAndGameLog(playerIndex, newPosition);
        }

        private void checkTile(int currentPlayer, int position) {
            int finalCell = primaryRules.nRows()*primaryRules.nCols();
            if(position == finalCell) {
                endGame(currentPlayer);
            }
        }

        private void endGame(int currentPlayer) {
            String winnerMessage = playersName[currentPlayer] + " (Player " + playersTag[currentPlayer] + ") wins the game!";
            JOptionPane.showMessageDialog(boardFrame, winnerMessage, "Winner!", JOptionPane.INFORMATION_MESSAGE);
            // exit or restart the game
            int response = JOptionPane.showConfirmDialog(boardFrame, "Do you want to restart this match?", "Restart?", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                new GameBoard(primaryRules, specialRules);  // reset this game with the same configuration
                boardFrame.dispose();
            } else {
                new GameConfiguration(); // goes back to main menu
                boardFrame.dispose();
            }
        }

        private void nextTurn() {
            nextPlayer = (nextPlayer +1) % primaryRules.nPlayers();  // updates the turn
            gameLog.append("Next Player: " + playersTag[nextPlayer] + ".\n");
        }

    }

}
