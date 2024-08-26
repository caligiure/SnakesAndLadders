import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {

    private void printInfo(String s){
        System.out.println(s);
    }

    public Test() {
        final int ROWS = 10, COLUMNS = 3;
        // initialize the window
        setTitle("Scale e Serpenti");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // set layout of the container

        // create the board with GridLayout
        JPanel boardPanel = new JPanel(new GridLayout(ROWS, COLUMNS));
        JLabel[][] cells = new JLabel[ROWS][COLUMNS]; // create matrix of labels
        fillTheBoard(boardPanel, cells, ROWS, COLUMNS);
        // add the board component to the container
        add(boardPanel, BorderLayout.CENTER);

        // add a button to roll the dice (Only in manual mode)
        JButton rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(e -> rollDice());
        add(rollButton, BorderLayout.SOUTH);

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

    public static void main(String[] args) {
        new Test().setVisible(true);
    }

}
