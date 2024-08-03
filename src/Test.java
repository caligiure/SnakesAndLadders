import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {
    public static final int ROWS = 3, COLUMNS=8;

    private void printInfo(String s){
        System.out.println(s);
    }

    public Test() {
        // initialize the window
        setTitle("Scale e Serpenti");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // set layout of the container

        // create the board with GridLayout
        JPanel boardPanel = new JPanel(new GridLayout(ROWS, COLUMNS));
        JLabel[][] cells = new JLabel[ROWS][COLUMNS]; // create matrix of labels
        // fill the board (from last to first cell)
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                int cellLabel = (ROWS - i - 1) * COLUMNS + j +1;
                if (i % 2 == 1)
                    cellLabel = (ROWS - i) * COLUMNS - j;
                cells[i][j] = new JLabel(String.valueOf(cellLabel), SwingConstants.CENTER);
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cells[i][j].setOpaque(true); //CONTROLLA POI
                printInfo("Adding cell " + cellLabel + " to board");
                boardPanel.add(cells[i][j]);
            }
        }
        // add the board component to the container
        add(boardPanel, BorderLayout.CENTER);


    }

    public static void main(String[] args) {
        new Test().setVisible(true);
    }

}
