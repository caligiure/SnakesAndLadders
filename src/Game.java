import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

// Da fare: autoAdvance, rules, reset button, executables

public class Game {
    private final PrimaryRulesRecord primaryRules;
    private final SpecialRulesRecord specialRules;
    private final String[] playerName;

    public Game(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;
        playerName = new String[primaryRules.nPlayers()];
        InsertPlayerNameFrame firstPlayerFrame = new InsertPlayerNameFrame(0); // frame to insert the first player's name
        firstPlayerFrame.setVisible(true);
    }

    private class InsertPlayerNameFrame extends JFrame {
        public InsertPlayerNameFrame(int i) {
            // Set the JFrame
            setTitle("Insert Player Name");
            setSize(500, 100);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10)); // this panel contains the other elements
            JTextField nameField = new JTextField("Player "+(i+1)); // field for the player's name
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> saveAndClose(i, nameField.getText())); // saves the name and closes the frame
            panel.add(new JLabel("Insert name for Player "+(i+1)+":"));
            panel.add(nameField);
            panel.add(okButton);
            add(panel);
        }

        private void saveAndClose(int i, String name) {
            playerName[i] = name;
            if(i < primaryRules.nPlayers()-1){
                InsertPlayerNameFrame nextPlayerFrame = new InsertPlayerNameFrame(i+1);
                nextPlayerFrame.setVisible(true);
            } else {
                GameFrame gameFrame = new GameFrame();
                gameFrame.setVisible(true);
            }
            this.dispose();
        } // saves the name, opens the next frame and closes itself
    } // frame to insert a player's name

    private class GameFrame extends JFrame {
        // Players
        private final String[] playersTag = new String[primaryRules.nPlayers()];
        private final int[] playersPosition = new int[primaryRules.nPlayers()];
        private int nextPlayer = 0; // indicates the number of the player who must roll the dices
        private final LinkedList<Integer> stoppedPlayers = new LinkedList<>(); // players that are currently stopped
        private final LinkedList<Integer> denyStopPlayers = new LinkedList<>(); // players that are currently holding a denyStop card
        // Game Log
        private DefaultTableModel playersTable;  // shows ID, name and position of every player
        private JTextArea gameLog;  // Text area for gameLog
        // Snakes, ladders and special cells
        private final JLabel[][] cells = new JLabel[primaryRules.nRows()][primaryRules.nCols()]; // create matrix of labels;
        private final LinkedList<int[]> ladders = new LinkedList<>(); // bottom and top position of every ladder
        private final LinkedList<int[]> snakes = new LinkedList<>(); // head and tail position of every snake
        private final Content[][] cellsContent = new Content[primaryRules.nRows()][primaryRules.nCols()]; // the type of content of every cell
        private enum Content {
            empty, ladderBottom, ladderTop, snakeHead, snakeTail, stop, moveAgain, rollAgain, drawCard
        } // types of content that a cell can contain, or that can be found in the cards

        public GameFrame() {
            initializeFrame(); // sets the name, size and layout of the frame
            initializePlayersTag(); // sets a tag for every player, which will be displayed on the board
            randomizeCellsContent(); // chooses randomly the content of every cell (snake, ladder, special cells)

            BoardPanel boardPanel = new BoardPanel(); // panel that contains the cells of the board
            add(boardPanel, BorderLayout.CENTER);

            JPanel bottomPanel = buildBottomPanel(); // creates a bottom panel that will hold the buttons
            add(bottomPanel, BorderLayout.SOUTH);

            JPanel sidePanel = buildSidePanel(); // create a side panel to show game log and players table
            add(sidePanel, BorderLayout.EAST);

            for(int i=0; i<primaryRules.nPlayers(); i++)
                updatePlayerPosition(i, 1); // after the board is created, puts every player on the board
            gameLog.append("Next Player: " + playersTag[0] + ".\n");
        }
        private void initializeFrame() {
            setTitle("Scale e Serpenti");
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout()); // set layout of the container
        } // sets the name, size and layout of the frame
        private void initializePlayersTag() {
            for(int i=0; i<primaryRules.nPlayers(); i++){
                String id = "👤"+(i+1);
                playersTag[i] = id;
            }
        } // sets a tag for every player, which will be displayed on the board
        private void randomizeCellsContent() {
            int nRows = primaryRules.nRows();
            int nCols = primaryRules.nCols();
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
        } // used in randomizeCellsContent, chooses a random empty cell and puts the specified content inside it, then returns the position of the cell
        private int getNumOfSpecialCellsPerType(int nRows, int nCols) {
            int percentageOfSpecialCells = ((nRows * nCols) / 100)*40; // the board will contain at max 40% of special cells
            int numOfEmptyCells =  nRows * nCols - (primaryRules.nSnakes()+primaryRules.nLadders()) * 2 - 2;
            int maxNumOfSpecialCells = percentageOfSpecialCells;
            if (numOfEmptyCells < maxNumOfSpecialCells) {
                maxNumOfSpecialCells = numOfEmptyCells;
            }
            return maxNumOfSpecialCells / 4; // there are four types of special cells
        } // used in randomizeCellsContent, returns the number of special cells of every type which can be added to the board, based on the dimensions of the board and the number of snakes and ladders

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

        private class BoardPanel extends JPanel {
            public BoardPanel() {
                super(new GridLayout(primaryRules.nRows(), primaryRules.nCols()));
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
                drawSpecialCells(g);
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
                }
                // Draw red snakes
                g.setColor(Color.RED);
                for (int[] snake : snakes) {
                    Point headPoint  = calculateDrawingPoint(snake[0]);
                    Point tailPoint = calculateDrawingPoint(snake[1]);
                    // draw 2 straight line that represent the snake
                    g.drawLine(tailPoint.x-10, tailPoint.y, headPoint.x-10, headPoint.y);
                    g.drawLine(tailPoint.x+10, tailPoint.y, headPoint.x+10, headPoint.y);
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
            } // represents ladders and snakes with lines

            private void drawSpecialCells(Graphics g) {
                for (int row = 0; row < primaryRules.nRows(); row++) {
                    for (int col = 0; col < primaryRules.nCols(); col++) {
                        Content content = cellsContent[row][col];
                        if( !(content.equals(Content.empty)) ) {
                            Point center = calculateDrawingPoint(findPosition(row, col));
                            if (content.equals(Content.rollAgain))
                                g.setColor(Color.BLUE);
                            else if (content.equals(Content.moveAgain))
                                g.setColor(Color.CYAN);
                            else if (content.equals(Content.stop))
                                g.setColor(Color.MAGENTA);
                            else if (content.equals(Content.drawCard))
                                g.setColor(Color.YELLOW);
                            else if (content.equals(Content.snakeHead) || content.equals(Content.snakeTail))
                                g.setColor(Color.RED);
                            else if (content.equals(Content.ladderBottom) || content.equals(Content.ladderTop))
                                g.setColor(Color.GREEN);
                            g.drawRoundRect(center.x - 19, center.y - 19, 41, 41, 10, 10);
                        }
                    }
                }
            } // draws a rectangle of a different color for each kind of special cell
        } // overrides the paintComponent method of JPanel to draw ladders, snakes and special cells on the board

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
            if( specialRules.autoAdvance() ) {
                JButton startButton = new JButton("Start");
                startButton.addActionListener(new AutoAdvanceGame());
                buttonsPanel.add(startButton);
            } else {
                JButton rollButton = new JButton("Roll Dice");
                rollButton.addActionListener(new RollDiceListener());
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
            sidePanel.setLayout(new GridLayout(3, 1, 10, 10));
            sidePanel.setPreferredSize(new Dimension(350, getHeight()));

            JScrollPane pTable = buildPlayersTable(); // create players table
            sidePanel.add(pTable); // add the table to the side panel

            JScrollPane legendTable = buildLegendTable(); // create legend table
            sidePanel.add(legendTable);

            JScrollPane logScrollPane = buildGameLog(); // create gameLog
            sidePanel.add(logScrollPane);

            return sidePanel;
        } // builds the side panel containing the names table and the game log

        private JScrollPane buildPlayersTable() {
            String[] columnNames = {"Tag", "Name", "Position"};
            Object[][] data = new Object[primaryRules.nPlayers()][3];
            for (int i = 0; i < primaryRules.nPlayers(); i++) {
                data[i][0] = playersTag[i];
                data[i][1] = playerName[i];
                data[i][2] = 1;
            }
            playersTable = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(playersTable); // contains the default table model
            table.setDefaultEditor(Object.class, null);
            return new JScrollPane(table); // JScrollPane makes the table scrollable
        } // builds a player table to show every player's tag, name and position

        private JScrollPane buildLegendTable() {
            String[] columnNames = {"Color", "Meaning"};
            String[][] data = new String[7][2];
            data[0][0] = "BLUE"; data[0][1] = "Roll Again ↺⚀⚅";
            data[1][0] = "CYAN"; data[1][1] = "Move Again ⏩";
            data[2][0] = "MAGENTA"; data[2][1] = "Stop ❌⏳";
            data[3][0] = "YELLOW"; data[3][1] = "Draw card ♠♣♥♦";
            data[4][0] = "RED"; data[4][1] = "Snake ⏬";
            data[5][0] = "GREEN"; data[5][1] = "Ladder ⏫";
            data[6][0] = "DENY STOP"; data[6][1] = "Denies a Stop ✋";
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(tableModel); // contains the default table model
            table.setDefaultEditor(Object.class, null);
            return new JScrollPane(table); // JScrollPane makes the table scrollable
        } // builds a legend table to show the meaning of each color

        private JScrollPane buildGameLog() {
            gameLog = new JTextArea();
            gameLog.setFont(gameLog.getFont().deriveFont(17f));
            gameLog.setEditable(false);
            return new JScrollPane(gameLog);
        } // builds a gameLog to show all the game infos

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
            this.repaint(); // to avoid graphic errors
        } // removes a player from the old cell and adds it to the new one
        private JLabel getCellLabel(int i) {
            int[] cord = findCoordinates(i);
            return cells[cord[0]][cord[1]];
        } // used in updatePlayerPosition, returns the label of a cell, given its index

        private GameFrame getGameFrame() {
            return this;
        } // used in RollDiceListener

        private class RollDiceListener extends GameManager {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollDice();
                nextTurn();
            }

            @Override
            void nextTurn() {
                nextPlayer = (nextPlayer +1) % primaryRules.nPlayers();  // updates the turn
                gameLog.append("Next Player: " + playersTag[nextPlayer] + ".\n");
            }
        }

        private class AutoAdvanceGame extends GameManager {

            @Override
            public void actionPerformed(ActionEvent e) {

            }

            @Override
            void nextTurn() {

            }
        }

        private abstract class GameManager implements ActionListener{

            void rollDice() {
                int currentPlayer = nextPlayer;
                int currentPosition = playersPosition[currentPlayer];
                int diceSum;
                boolean doubleSix = false;
                if(stoppedPlayers.contains(currentPlayer)) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " is stopped and will play on the next turn.\n");
                    stoppedPlayers.removeFirstOccurrence(currentPlayer);
                } else {
                    if (specialRules.singleDice() && currentPosition >= primaryRules.nRows() * primaryRules.nCols() - 6) // if the player is on one of the last 6 cells and the rule singleDice is active
                        diceSum = sumDices(currentPlayer, 1);
                    else
                        diceSum = sumDices(currentPlayer, primaryRules.nDices());
                    if (specialRules.doubleSix() && diceSum == 12) { // if the rule doubleSix is active
                        gameLog.append("Player " + playersTag[currentPlayer] + " got a double six!⚅⚅\n");
                        doubleSix = true;
                    }
                    int newPosition = calculateNewPosition(currentPosition, diceSum);
                    movePlayer(currentPlayer, newPosition);
                    checkTile(currentPlayer, newPosition, diceSum); // check for snakes, ladders, special tiles or final cell
                    if (doubleSix) {
                        rollDice(); // if the player got a double six, he must roll again
                    }
                }
            } // rolls the dice and manages the consequences

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
            } //calculates the sum of the dices and visualizes the results in the gameLog

            private int calculateNewPosition(int currentPosition, int diceSum) {
                int newPosition = currentPosition + diceSum;
                int finalCell = primaryRules.nRows()*primaryRules.nCols();
                if (newPosition > finalCell) { // the last cell must be reached with an exact shot
                    newPosition = finalCell - (newPosition - finalCell);
                }
                return newPosition;
            } // calculates the new position of a player, based on its currentPosition and the sum of the dice

            private void movePlayer(int currentPlayer, int newPosition) {
                updatePlayerPosition(currentPlayer, newPosition);
                playersTable.setValueAt(newPosition, currentPlayer, 2);
                gameLog.append("Player " + playersTag[currentPlayer] + " moves to cell " + newPosition + ".\n");
            }

            private void checkTile(int currentPlayer, int position, int diceSum) {
                int finalCell = primaryRules.nRows()*primaryRules.nCols();
                int[] coords = findCoordinates(position);
                if(position == finalCell) {
                    endGame(currentPlayer);
                } else if(cellsContent[coords[0]][coords[1]].equals(Content.ladderBottom)) {
                    for(int[] ladder : ladders) {
                        if (ladder[0] == position){
                            gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a ladder!⏫\n");
                            movePlayer(currentPlayer, ladder[1]);
                        }
                    }
                } else if(cellsContent[coords[0]][coords[1]].equals(Content.snakeHead)) {
                    for(int[] snake : snakes) {
                        if(snake[0] == position) {
                            gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a snake!⏬\n");
                            movePlayer(currentPlayer, snake[1]);
                        }
                    }
                } else if(cellsContent[coords[0]][coords[1]].equals(Content.rollAgain)) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a special tile!✨\n");
                    gameLog.append("Player " + playersTag[currentPlayer] + " must roll again!↺⚀⚅\n");
                    rollDice();
                } else if(cellsContent[coords[0]][coords[1]].equals(Content.moveAgain)) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a special tile!✨\n");
                    gameLog.append("Player " + playersTag[currentPlayer] + " must move again!⏩\n");
                    int newPosition = calculateNewPosition(playersPosition[currentPlayer], diceSum);
                    movePlayer(currentPlayer, newPosition);
                } else if(cellsContent[coords[0]][coords[1]].equals(Content.stop)) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a stop tile!❌\n");
                    if(denyStopPlayers.contains(currentPlayer)){
                        gameLog.append("Player " + playersTag[currentPlayer] + " uses a deny stop card to avoid getting stopped!✋\n");
                        denyStopPlayers.removeFirstOccurrence(currentPlayer);
                    } else {
                        gameLog.append("Player " + playersTag[currentPlayer] + " is stopped for 1 turn!⏳\n");
                        stoppedPlayers.add(currentPlayer);
                    }
                } else if(cellsContent[coords[0]][coords[1]].equals(Content.drawCard)) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " stepped on a card tile!♠♣♥♦\n");
                    drawCard(currentPlayer, diceSum);
                }
            }

            private void drawCard(int currentPlayer, int diceSum) {
                Random rand = new Random();
                int card; // there are 4 types of card: stop(0), moveAgain(1), rollAgain(2), denyStop(3)
                if(specialRules.denyStopCard()) // if the rule isn't active the card denyStop can't be drawn
                    card = rand.nextInt(0, 4);
                else
                    card = rand.nextInt(0, 3);
                if(card == 0) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " draws a stop card!❌\n");
                    if(denyStopPlayers.contains(currentPlayer)){
                        gameLog.append("Player " + playersTag[currentPlayer] + " uses a deny stop card to avoid getting stopped!✋\n");
                        denyStopPlayers.removeFirstOccurrence(currentPlayer);
                    } else {
                        gameLog.append("Player " + playersTag[currentPlayer] + " is stopped for 1 turn!⏳\n");
                        stoppedPlayers.add(currentPlayer);
                    }
                } else if(card == 1) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " draws a move again card!⏩\n");
                    gameLog.append("Player " + playersTag[currentPlayer] + " must move again!⏩\n");
                    int newPosition = calculateNewPosition(playersPosition[currentPlayer], diceSum);
                    movePlayer(currentPlayer, newPosition);
                } else if(card == 2) {
                    gameLog.append("Player " + playersTag[currentPlayer] + " draws a roll again card!↺⚀⚅\n");
                    gameLog.append("Player " + playersTag[currentPlayer] + " must roll again!↺⚀⚅\n");
                    rollDice();
                } else {
                    gameLog.append("Player " + playersTag[currentPlayer] + " draws a deny stop card!✋\n");
                    gameLog.append("Player " + playersTag[currentPlayer] + " will hold this card until needed!✋\n");
                    denyStopPlayers.add(currentPlayer);
                }
            } // draws a card randomly

            private void endGame(int currentPlayer) {
                String winnerMessage = playerName[currentPlayer] + " (Player " + playersTag[currentPlayer] + ") wins the game!";
                JOptionPane.showMessageDialog(getGameFrame(), winnerMessage, "Winner!", JOptionPane.INFORMATION_MESSAGE);
                // exit or restart the game
                int response = JOptionPane.showConfirmDialog(getGameFrame(), "Do you want to restart this match?", "Restart?", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    new Game(primaryRules, specialRules);  // reset this game with the same configuration
                    getGameFrame().dispose();
                } else {
                    new GameConfiguration(); // goes back to main menu
                    getGameFrame().dispose();
                }
            }

            abstract void nextTurn();

        }

    } // GameFrame contains all the graphic elements of the game

}
