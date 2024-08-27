import javax.swing.*;
import java.awt.*;

public class GameBoard {
    // Game Rules and Settings
    private final PrimaryRulesRecord primaryRules;
    private final SpecialRulesRecord specialRules;
    // Game Board
    private BoardFrame boardFrame;

    public GameBoard(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;
        boardFrame = new BoardFrame();
        boardFrame.setVisible(true);
    }

    private class BoardFrame extends JFrame {
        public BoardFrame() {
            // initialize the frame
            setTitle("Scale e Serpenti");
            setSize(600, 600);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout()); // set layout of the container
            // create the board with GridLayout
            JPanel boardPanel = new JPanel(new GridLayout(primaryRules.nRows(), primaryRules.nCols()));
            JLabel[][] cells = new JLabel[primaryRules.nRows()][primaryRules.nCols()]; // create matrix of labels
            fillTheBoard(boardPanel, cells, primaryRules.nRows(), primaryRules.nCols());
            // add the board component to the container
            add(boardPanel, BorderLayout.CENTER);
            // add a button to roll the dice (Only if autoAdvance is false)
            if( !specialRules.autoAdvance() ) {
                JButton rollButton = new JButton("Roll Dice");
                rollButton.addActionListener(e -> rollDice());
                add(rollButton, BorderLayout.SOUTH);
            }
        }

        private static void fillTheBoard(JPanel boardPanel, JLabel[][] cells, int row, int col) {
            // fill the board from last to first cell
            for(int i = 0; i < row; i++) {
                for(int j = 0; j < col; j++) {
                    int cellLabel = (row - i - 1) * col + j +1;
                    if (row % 2 == 1 &&  i % 2 == 1 || row % 2 == 0 &&  i % 2 == 0)
                        cellLabel = (row - i) * col - j;
                    cells[i][j] = new JLabel(String.valueOf(cellLabel), SwingConstants.CENTER);
                    cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    cells[i][j].setOpaque(true); //CONTROLLA POI
                    boardPanel.add(cells[i][j]);
                }
            }
        }

        private static void rollDice(){

        }
    }

}
