import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

class GameFrame extends JFrame { // GameFrame contains all the graphic elements of the game
    private final PrimaryRulesRecord primaryRules;
    private final SpecialRulesRecord specialRules;
    private final String[] playersName; // used in playersTable
    private final String[] playersTag; // used in playersTable
    private DefaultTableModel playersTable;
    private final JTextArea gameLog = new JTextArea();  // Text area for gameLog
    // elements to represent and visualize snakes, ladders and special cells
    private final JLabel[][] cells; // create matrix of labels;
    private final LinkedList<int[]> ladders = new LinkedList<>(); // bottom and top position of every ladder
    private final LinkedList<int[]> snakes = new LinkedList<>(); // head and tail position of every snake
    private final Content[][] cellsContent; // the type of content of every cell

    public GameFrame(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules, String[] playersName) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;
        this.playersName = playersName;
        playersTag = new String[primaryRules.nPlayers()];
        cells = new JLabel[primaryRules.nRows()][primaryRules.nCols()];
        cellsContent = new Content[primaryRules.nRows()][primaryRules.nCols()];

        initializeFrame(); // sets the name, size and layout of the frame
        initializePlayersTag(); // sets a tag for every player, which will be displayed on the board
        randomizeCellsContent(); // chooses randomly the content of every cell (snake, ladder, special cells)

        BoardPanel boardPanel = new BoardPanel(); // panel that contains the cells of the board
        add(boardPanel, BorderLayout.CENTER);
        JPanel sidePanel = buildSidePanel(); // create a side panel to show game log and players table
        add(sidePanel, BorderLayout.EAST);
        JPanel bottomPanel = buildBottomPanel(); // creates a bottom panel that will hold the buttons
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initializeFrame() {
        setTitle("Scale e Serpenti");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // set layout of the container
    } // sets the name, size and layout of the frame

    private void initializePlayersTag() {
        for(int i = 0; i< primaryRules.nPlayers(); i++){
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
        for(int i = 0; i< primaryRules.nLadders(); i++) {
            int ladderBottom = putInRandomEmptyPosition(2, nRows*nCols-1, Content.ladderBottom); // the second-last cell can't contain the bottom of a ladder
            int ladderTop = putInRandomEmptyPosition(ladderBottom+1, nRows*nCols, Content.ladderTop); // ladderTop must be after startingPoint
            ladders.add( new int[]{ladderBottom, ladderTop} ); // add ladder to ladders list
        }
        // choose randomly the head and tail point of every snake, avoiding the cells that already contain something
        for(int i = 0; i< primaryRules.nSnakes(); i++) {
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
        int[] coords = findCoordinates(position, primaryRules.nRows(), primaryRules.nCols());
        while ( !cellsContent[coords[0]][coords[1]].equals(Content.empty) ) {
            position = random.nextInt(origin, bound);
            coords = findCoordinates(position, primaryRules.nRows(), primaryRules.nCols());
        }
        cellsContent[coords[0]][coords[1]] = content;
        return position;
    } // used in randomizeCellsContent, chooses a random empty cell and puts the specified content inside it, then returns the position of the cell

    private int getNumOfSpecialCellsPerType(int nRows, int nCols) {
        int percentageOfSpecialCells = ((nRows * nCols) / 100)*40; // the board will contain at max 40% of special cells
        int numOfEmptyCells =  nRows * nCols - (primaryRules.nSnakes()+ primaryRules.nLadders()) * 2 - 2;
        int maxNumOfSpecialCells = percentageOfSpecialCells;
        if (numOfEmptyCells < maxNumOfSpecialCells) {
            maxNumOfSpecialCells = numOfEmptyCells;
        }
        return maxNumOfSpecialCells / 4; // there are four types of special cells
    } // used in randomizeCellsContent, returns the number of special cells of every type which can be added to the board, based on the dimensions of the board and the number of snakes and ladders

    public static int[] findCoordinates(int position, int nRows, int nCols) {
        int rowFromBottom = (position - 1) / nCols; // Number of the row counting from the bottom
        int x = nRows - 1 - rowFromBottom;       // x coordinate
        boolean isEvenRow = rowFromBottom % 2 == 0; // even row counting from the bottom
        int y;
        if (isEvenRow) {
            y = (position - 1) % nCols; // counting from left to right
        } else {
            y = nCols - 1 - ((position - 1) % nCols); // counting from right to left
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
            int[] coords = findCoordinates(position, primaryRules.nRows(), primaryRules.nCols());
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
            startButton.addActionListener(new GameManagerAutoAdvance(primaryRules, specialRules, this));
            buttonsPanel.add(startButton);
        } else {
            JButton rollButton = new JButton("Roll Dice");
            rollButton.addActionListener(new GameManagerManualAdvance(primaryRules, specialRules, this));
            buttonsPanel.add(rollButton);
        }
        return buttonsPanel;
    } // builds the bottom panel containing the buttons

    private void showRules() {
        JFrame rulesFrame = new JFrame("Snakes and Ladders Rules"); // Create a new JFrame for the rules
        rulesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rulesFrame.setSize(1000, 500);
        rulesFrame.setLocationRelativeTo(null); // set the frame in the center of the screen
        JTextArea rulesTextArea = new JTextArea(); // create a JTextArea to display the rules
        rulesTextArea.setFont(gameLog.getFont().deriveFont(17f));
        rulesTextArea.setText(getGameRules());
        rulesTextArea.setEditable(false); // text area is read-only
        JScrollPane scrollPane = new JScrollPane(rulesTextArea); // JScrollPane in case the text is too long
        rulesFrame.add(scrollPane);
        rulesFrame.setVisible(true);
    } // shows the game rules

    private String getGameRules() {
        return """
                Snakes and Ladders Rules:
                1.  Each player starts at the bottom left corner (tile 1).
                2.  Players take turns rolling the dice and moving forward by the sum of the numbers rolled.
                3.  The first player to reach the last cell wins the game, but it must be reached with an exact number of steps.
                     If a player rolls a number that would take them beyond the last cell, they get to the last cell and then
                     retreat by the number of steps in excess.
                4.  If a player lands on the bottom of a ladder (GREEN tiles), they move up to the top of the ladder ⏫.
                5.  If a player lands on the head of a snake (RED tiles), they slide down to the tail of the snake ⏬.
                6.  If a player lands on a BLUE tile, they must ROLL AGAIN the dice ↺⚀⚅.
                7.  If a player lands on a CYAN tile, they must MOVE AGAIN ⏩ by the same number of steps they already moved.
                8.  If a player lands on a MAGENTA tile, they must STOP ❌.
                     A stopped player will have to wait ⏳ for a turn before being able to move again.
                     A player can avoid getting stopped if they have a DENY STOP ✋ card.
                9.  If a player lands on a YELLOW tile, they must DRAW A CARD ♠♣♥♦.
                     There are 4 types of cards: ROLL AGAIN ↺⚀⚅, MOVE AGAIN ⏩, STOP ❌ and DENY STOP ✋.
                     The first 3 types of card have the same effect of the tile with the same name.
                     The DENY STOP card is a special card that the player can hold in their hands until needed.
                     When that player gets stopped, they will consume their DENY STOP ✋ card and avoid getting stopped.
                10. If a player rolled a DOUBLE SIX, at the end of their turn they will roll the dice a second time and move again.
                """;
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
            data[i][1] = playersName[i];
            data[i][2] = 1;
        }
        // Game Log shows ID, name and position of every player
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
        gameLog.setFont(gameLog.getFont().deriveFont(17f));
        gameLog.setEditable(false);
        return new JScrollPane(gameLog);
    } // builds a gameLog to show all the game infos

    public String getPlayerTag(int player) {
        return playersTag[player];
    } // used in GameManager, returns the tag of the specified player

    public JLabel getCellLabel(int i) {
        int[] cord = findCoordinates(i, primaryRules.nRows(), primaryRules.nCols());
        return cells[cord[0]][cord[1]];
    } // used in GameManager.updatePlayerPosition, returns the label of a cell, given its index

    public Content getcellContent(int[] coords) {
        return cellsContent[coords[0]][coords[1]];
    } // used in GameManager.checkTile, returns the content of the given cell

    public void appendGameLog(String log) {
        gameLog.append(log);
    } // used in GameManager, appends the specified log String to the gameLog

    public void setPositionOnPlayersTable(int newPosition, int currentPlayer) {
        playersTable.setValueAt(newPosition, currentPlayer, 2);
    } // used in GameManager.movePlayer, sets the new position on the players table

    public int checkLadder(int position) {
        for(int[] ladder : ladders)
            if (ladder[0] == position)
                return ladder[1];
        return -1;
    } // used in GameManager.checkTile, checks if the specified position contains a ladder bottom and returns the position of the top of the ladder

    public int checkSnake(int position) {
        for(int[] snake : snakes)
            if (snake[0] == position)
                return snake[1];
        return -1;
    } // used in GameManager.checkTile, checks if the specified position contains a snake head and returns the position of the tail of the snake

} // GameFrame contains all the graphic elements of the game
