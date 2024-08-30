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
    private RollDiceListener rollDiceListener;
    // Players
    private String[] playersTag;
    private final String[] playersName;
    private int[] playersPosition;
    private int nextPlayer = 0; // indicates the number of the player who must roll the dices
    // Game Log
    private DefaultTableModel playersTable;  // shows ID, name and position of every player
    private JTextArea gameLog;  // Text area for gameLog
    // snakes, ladders and special cells
    private LinkedList<int[]> ladders; // bottom and top position of every ladder
    private LinkedList<int[]> snakes; // head and tail position of every snake
    private Content[][] cellsContent; // the type of content of every cell
    private enum Content {
        empty, ladderBottom, ladderTop, snakeHead, snakeTail, stop, moveAgain, rollAgain, drawCard
    }
    private final LinkedList<Integer> stoppedPlayers = new LinkedList<>(); // players that are currently stopped

    public GameBoard(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;

        playersName = new String[primaryRules.nPlayers()]; // da cambiare con l'inserimento dei nomi
        initializePlayersTag();

        initializeCellsContent();

        boardFrame = new BoardFrame();
        boardFrame.setVisible(true);

        initializePlayersPosition();
        gameLog.append("Next Player: " + playersTag[0] + ".\n");
    }

    private void initializePlayersTag() {
        playersTag = new String[primaryRules.nPlayers()];
        for(int i=0; i<primaryRules.nPlayers(); i++){
            String id = "👤"+(i+1);
            playersTag[i] = id;
        }
        playersPosition = new int[primaryRules.nPlayers()];
    } // sets a tag for every player, which will be displayed on the board

    private void initializeCellsContent() {
        int nRows = primaryRules.nRows();
        int nCols = primaryRules.nCols();
        ladders = new LinkedList<>();
        snakes = new LinkedList<>();
        cellsContent = new Content[nRows][nCols]; // contains snakes, ladders and special cells
        for(int i = 0; i < primaryRules.nRows(); i++) {
            for(int j = 0; j < primaryRules.nCols(); j++) {
                cellsContent[i][j]= Content.empty; // initialize every cell as empty
            }
        }
        // choose randomly the bottom and top point of every ladder, avoiding the cells that already contain something
        // the first and last cell of the board cannot contain ladders
        for(int i=0; i<primaryRules.nLadders(); i++) {
            int ladderBottom = putInRandomEmptyPosition(2, nRows*nCols-1, Content.ladderBottom); // the second-last cell can't contain the bottom of a ladder
            int ladderTop = putInRandomEmptyPosition(ladderBottom+1, nRows*nCols, Content.ladderTop); // ladderTop must be after startingPoint
            ladders.add( new int[]{ladderBottom, ladderTop} ); // add ladder to ladders list
        }
        // choose randomly the head and tail point of every snake, avoiding the cells that already contain something
        for(int i=0; i<primaryRules.nSnakes(); i++) {
            int snakeHead = putInRandomEmptyPosition(2, nRows*nCols, Content.snakeHead); // the first and final cells cannot be a head point for a snake
            int snakeTail = putInRandomEmptyPosition(1, snakeHead, Content.snakeTail); // snakeTail must be before headPoint
            snakes.add( new int[]{snakeHead, snakeTail} ); // add snake to snakes list
        }
        // initialize special Cells
        int numOfSpecialCellsPerType = getNumOfSpecialCellsPerType(nRows, nCols); // there are four types of special cells
        for(int i=0; i<numOfSpecialCellsPerType; i++) {
            if(specialRules.stopTiles()) { // a type of content must be added only if the relative rule is active
                putInRandomEmptyPosition(2, nRows*nCols, Content.stop); // first and last cell can't contain a special item
            }
            if(specialRules.moveAgainTiles()) {
                putInRandomEmptyPosition(2, nRows*nCols, Content.moveAgain);
            }
            if(specialRules.rollAgainTiles()) {
                putInRandomEmptyPosition(2, nRows*nCols, Content.rollAgain);
            }
            if(specialRules.addCards()) {
                putInRandomEmptyPosition(2, nRows*nCols, Content.drawCard);
            }
        }
    } // initialize the content of every cell (snake, ladder, special cells)

    private int putInRandomEmptyPosition(int origin, int bound, Content content){
        Random random = new Random();
        int position = random.nextInt(origin, bound);
        int[] coords = findCoordinates(position);
        while ( !cellsContent[coords[0]][coords[1]].equals(Content.empty) ) {
            position = random.nextInt(origin, bound);
            coords = findCoordinates(position);
        }
        cellsContent[coords[0]][coords[1]] = content;
        return position;
    } // chooses a random empty cell and puts the specified content inside it, then returns the position of the cell

    private int getNumOfSpecialCellsPerType(int nRows, int nCols) {
        int percentageOfSpecialCells = ((nRows * nCols) / 100)*40; // the board will contain at max 40% of special cells
        int numOfEmptyCells =  nRows * nCols - (primaryRules.nSnakes()+primaryRules.nLadders()) * 2 - 2;
        int maxNumOfSpecialCells = percentageOfSpecialCells;
        if (numOfEmptyCells < maxNumOfSpecialCells) {
            maxNumOfSpecialCells = numOfEmptyCells;
        }
        return maxNumOfSpecialCells / 4; // there are four types of special cells
    }

    private int[] findCoordinates(int position) {
        int rows = primaryRules.nRows();
        int cols = primaryRules.nCols();
        int rowFromBottom = (position - 1) / cols; // Number of the row counting from the bottom
        int x = rows - 1 - rowFromBottom;       // x coordinate
        boolean isEvenRow = rowFromBottom % 2 == 0; // even row counting from the bottom
        int y;
        if (isEvenRow) {
            y = (position - 1) % cols; // counting from left to right
        } else {
            y = cols - 1 - ((position - 1) % cols); // counting from right to left
        }
        return new int[]{x, y};
    } // returns an array containing the x and y coordinates of a cell in the cells matrix, given its position

    private int findPosition(int x, int y) {
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

    private void initializePlayersPosition() {
        for(int i=0; i<primaryRules.nPlayers(); i++){
            playersPosition[i] = 1; // the starting point for every player is cell 1
            boardFrame.updatePlayerPosition(i, 1);
        }
    } // sets the starting position of every player

    private class insertPlayerNamesFrame extends JFrame {
        public insertPlayerNamesFrame(int i) {

        }
    }

    private class BoardFrame extends JFrame {
        public BoardFrame() {
            initializeFrame(); // sets the name, size and layout of the frame

            BoardPanel boardPanel = new BoardPanel(); // panel that contains the cells of the board
            add(boardPanel, BorderLayout.CENTER);

            JPanel bottomPanel = buildBottomPanel(); // creates a bottom panel that will hold the buttons
            add(bottomPanel, BorderLayout.SOUTH);

            JPanel sidePanel = buildSidePanel(); // create a side panel to show game log and players table
            add(sidePanel, BorderLayout.EAST);
        }

        private void initializeFrame() {
            setTitle("Scale e Serpenti");
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout()); // set layout of the container
        } // sets the name, size and layout of the frame

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
                rollDiceListener = new RollDiceListener();
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
            sidePanel.setLayout(new GridLayout(2, 1, 10, 10));
            sidePanel.setPreferredSize(new Dimension(325, getHeight()));

            JScrollPane pTable = buildPlayersTable(); // create players table
            sidePanel.add(pTable); // add the table to the side panel

            JScrollPane logScrollPane = buildGameLog(); // create gameLog
            sidePanel.add(logScrollPane);

            return sidePanel;
        } // builds the side panel containing the names table and the game log

        private JScrollPane buildGameLog() {
            gameLog = new JTextArea();
            gameLog.setFont(gameLog.getFont().deriveFont(17f));
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
            boardFrame.repaint();
        } // removes a player from the old cell and adds it to the new one

        private void logPlayerMovement(int playerIndex, int newPosition) {
            playersTable.setValueAt(newPosition, playerIndex, 2);
            gameLog.append("Player " + playersTag[playerIndex] + " moves to cell " + newPosition + ".\n");
        } // updates the position of the player on the playersTable and adds a new log on the gameLog regarding its movement

    } // frame that contains the board, the names table and the game log

    private class BoardPanel extends JPanel {
        public BoardPanel() {
            super(new GridLayout(primaryRules.nRows(), primaryRules.nCols()));
            cells = new JLabel[primaryRules.nRows()][primaryRules.nCols()]; // create matrix of labels
            int rows = primaryRules.nRows();
            int cols = primaryRules.nCols();
            // fill the board from last to first cell
            for(int i = 0; i < rows; i++) {
                for(int j = 0; j < cols; j++) {
                    int cellLabel = findPosition(i, j); // returns the index of the cell, given the coordinates od the cells matrix
                    String text = "<html>" + cellLabel + ")</html>";
                    cells[i][j] = new JLabel(text, SwingConstants.CENTER);
                    cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    if(cellsContent[i][j].equals(Content.rollAgain))
                        cells[i][j].setBackground(Color.BLUE);
                    if(cellsContent[i][j].equals(Content.moveAgain))
                        cells[i][j].setBackground(Color.CYAN);
                    if(cellsContent[i][j].equals(Content.stop))
                        cells[i][j].setBackground(Color.MAGENTA);
                    add(cells[i][j]);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            drawLaddersAndSnakes(g);
        }

        private void drawLaddersAndSnakes(Graphics g) {
            // Draw green ladders
            g.setColor(Color.GREEN);
            for (int[] ladder : ladders) {
                Point bottomPoint = calculateDrawingPoint(ladder[0]); // bottom
                Point topPoint = calculateDrawingPoint(ladder[1]); // top
                // draw 2 straight line that represent the ladder
                g.drawLine(bottomPoint.x-10, bottomPoint.y, topPoint.x-10, topPoint.y);
                g.drawLine(bottomPoint.x+10, bottomPoint.y, topPoint.x+10, topPoint.y);
                g.fillRect(bottomPoint.x-10, bottomPoint.y-1, 20, 10);
                g.fillRect(topPoint.x-10, topPoint.y-1, 20, 10);
            }
            // Draw red snakes
            g.setColor(Color.RED);
            for (int[] snake : snakes) {
                Point headPoint  = calculateDrawingPoint(snake[0]);
                Point tailPoint = calculateDrawingPoint(snake[1]);
                // draw 2 straight line that represent the snake
                g.drawLine(tailPoint.x-10, tailPoint.y, headPoint.x-10, headPoint.y);
                g.drawLine(tailPoint.x+10, tailPoint.y, headPoint.x+10, headPoint.y);
                g.fillOval(headPoint.x-10, headPoint.y-2, 20, 12);
            }
        }

        private Point calculateDrawingPoint(int position) {
            int[] coords = findCoordinates(position);
            int row = coords[0];
            int col = coords[1];
            // dimensions of each cell
            int cellWidth = cells[row][col].getWidth();
            int cellHeight = cells[row][col].getHeight();
            // pixel coordinates of the point for drawing
            int pixelX = col * cellWidth + cellWidth / 2;
            int pixelY = row * cellHeight  + cellHeight / 2;
            return new Point(pixelX, pixelY);
        }
    } // panel that contains the cells of the board

    private class RollDiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            rollDice();
            nextTurn();
        }

        private void rollDice() {
            int currentPlayer = nextPlayer;
            int currentPosition = playersPosition[currentPlayer];
            int diceSum;
            boolean doubleSix = false;
            if(stoppedPlayers.contains(currentPlayer)) {
                gameLog.append("Player " + playersTag[currentPlayer] + " is stopped and will play on the next turn.\n");
                stoppedPlayers.remove(currentPlayer);
            } else {
                if (specialRules.singleDice() && currentPosition >= primaryRules.nRows() * primaryRules.nCols() - 6) // if the player is on one of the last 6 cells and the rule singleDice is active
                    diceSum = sumDices(currentPlayer, 1);
                else
                    diceSum = sumDices(currentPlayer, primaryRules.nDices());
                if (specialRules.doubleSix() && diceSum == 12) { // if the rule doubleSix is active
                    gameLog.append("Player " + playersTag[currentPlayer] + " got a double six!" + diceSum + ".\n");
                    doubleSix = true;
                }
                int newPosition = calculateNewPosition(currentPlayer, diceSum);
                movePlayer(currentPlayer, newPosition);
                checkTile(currentPlayer, newPosition, diceSum); // check for snakes, ladders, special tiles or final cell
                if (doubleSix) {
                    rollDice(); // if the player got a double six, he must roll again
                }
            }
        }

        private int sumDices(int currentPlayer, int nDices) {
            Random rand = new Random();
            int diceSum = 0; // sums the total of nDices
            StringBuilder visualResult = new StringBuilder();
            for (int i = 0; i < nDices; i++) {
                int res = rand.nextInt(1,7);
                diceSum += res;
                switch (res) {
                    case 1:
                        visualResult.append("⚀");
                        break;
                    case 2:
                        visualResult.append("⚁");
                        break;
                    case 3:
                        visualResult.append("⚂");
                        break;
                    case 4:
                        visualResult.append("⚃");
                        break;
                    case 5:
                        visualResult.append("⚄");
                        break;
                    default:
                        visualResult.append("⚅");
                        break;
                }
            }
            gameLog.append("Player " + playersTag[currentPlayer] + " got a " + diceSum + visualResult + ".\n");
            return diceSum;
        }

        private int calculateNewPosition(int currentPlayer, int diceSum) {
            int currentPosition = playersPosition[currentPlayer];
            int newPosition = currentPosition + diceSum;
            int finalCell = primaryRules.nRows()*primaryRules.nCols();
            if (newPosition > finalCell) { // the last cell must be reached with an exact shot
                newPosition = finalCell - (newPosition - finalCell);
            }
            return newPosition;
        }

        private void movePlayer(int currentPlayer, int newPosition) {
            boardFrame.updatePlayerPosition(currentPlayer, newPosition);
            boardFrame.logPlayerMovement(currentPlayer, newPosition);
        }

        private void checkTile(int currentPlayer, int position, int diceSum) {
            int finalCell = primaryRules.nRows()*primaryRules.nCols();
            int[] coords = findCoordinates(position);
            if(position == finalCell) {
                endGame(currentPlayer);
            } else if(cellsContent[coords[0]][coords[1]].equals(Content.ladderBottom)) {
                for(int[] ladder : ladders) {
                    if (ladder[0] == position){
                        gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a ladder ⤴.\n");
                        movePlayer(currentPlayer, ladder[1]);
                    }

                }
            } else if(cellsContent[coords[0]][coords[1]].equals(Content.snakeHead)) {
                for(int[] snake : snakes) {
                    if(snake[0] == position) {
                        gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a snake ⤵.\n");
                        movePlayer(currentPlayer, snake[1]);
                    }
                }
            } else if(cellsContent[coords[0]][coords[1]].equals(Content.rollAgain)) {
                gameLog.append("Player " + playersTag[currentPlayer] + " must roll again!\n");
                rollDice();
            } else if(cellsContent[coords[0]][coords[1]].equals(Content.moveAgain)) {
                gameLog.append("Player " + playersTag[currentPlayer] + " must move again!\n");
                int newPosition = calculateNewPosition(currentPlayer, diceSum);
                movePlayer(currentPlayer, newPosition);
            } else if(cellsContent[coords[0]][coords[1]].equals(Content.stop)) {
                gameLog.append("Player " + playersTag[currentPlayer] + " is stopped for a turn!\n");
                stoppedPlayers.add(currentPlayer);
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
