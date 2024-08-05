import javax.swing.*;
import java.awt.*;

public class ConfigGame {

    private int nPlayers;
    private int nRows;
    private int nCols;
    private int nDices;
    private int nLadders;
    private int nSnakes;

    public ConfigGame() {
        FirstFrame firstFrame = new FirstFrame();
        firstFrame.setVisible(true);
    }

    private class FirstFrame extends JFrame {
        // Primary fields for configuration
        private final JTextField playersField;
        private final JTextField rowsField;
        private final JTextField columnsField;
        private final JTextField laddersField;
        private final JTextField snakesField;
        private final JComboBox<String> diceComboBox;

        public FirstFrame() {
            // Set the JFrame
            setTitle("Configure the Snakes And Ladders game");
            setSize(600, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            // Primary fields
            playersField = new JTextField();
            rowsField = new JTextField();
            columnsField = new JTextField();
            laddersField = new JTextField();
            snakesField = new JTextField();
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            JButton nextButton = new JButton("Next");
            // Add fields to panel
            panel.add(new JLabel("Number of players:"));
            panel.add(playersField);
            panel.add(new JLabel("Number of Rows:"));
            panel.add(rowsField);
            panel.add(new JLabel("Number of Columns:"));
            panel.add(columnsField);
            panel.add(new JLabel("Number of stairs:"));
            panel.add(laddersField);
            panel.add(new JLabel("Number of snakes:"));
            panel.add(snakesField);
            panel.add(new JLabel("Number of dices:"));
            panel.add(diceComboBox);
            panel.add(new JLabel()); // placeholder
            panel.add(nextButton);
            // Add panel to frame
            add(panel);
            // Add action listener to button
            nextButton.addActionListener(_ -> nextFrame());
        }

        private void nextFrame() {
            try {
                int numPlayers = Integer.parseInt(playersField.getText());
                int numRows = Integer.parseInt(rowsField.getText());
                int numCols = Integer.parseInt(columnsField.getText());
                int numLadders = Integer.parseInt(laddersField.getText());
                int numSnakes = Integer.parseInt(snakesField.getText());
                int numDices = diceComboBox.getSelectedIndex() + 1;
                if(numDices < 0)
                    numDices = 0;
                if(numPlayers <= 0)
                    JOptionPane.showMessageDialog(this, "The number of players cannot be 0.", "Invalid Number of Players", JOptionPane.ERROR_MESSAGE);
                else if(numRows <= 0 && numCols <= 0)
                    JOptionPane.showMessageDialog(this, "Rows and Columns values cannot both be zero.", "Invalid Rows and Columns", JOptionPane.ERROR_MESSAGE);
                else {
                    // Set the values
                    nPlayers = numPlayers;
                    nRows = numRows;
                    nCols = numCols;
                    nLadders = numLadders;
                    nSnakes = numSnakes;
                    nDices = numDices;
                    // next frame
                    SecondFrame secondFrame = new SecondFrame();
                    secondFrame.setVisible(true);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The typed values aren't valid numbers.", "Values Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SecondFrame extends JFrame {
        // rules fields
        private final JCheckBox autoAdvanceCheckBox;
        private final JCheckBox singleDiceCheckBox;
        private final JCheckBox doubleSixCheckBox;
        private final JCheckBox stopTilesCheckBox;
        private final JCheckBox prizeTilesCheckBox;

        public SecondFrame() {
            // Set the JFrame
            setTitle("Select special rules");
            setSize(600, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            // Rules fields
            autoAdvanceCheckBox = new JCheckBox("Automatically roll the dices and advance");
            singleDiceCheckBox = new JCheckBox("Use a single dice in the last 6 tiles");
            doubleSixCheckBox = new JCheckBox("Double Roll when you get Double Six");
            stopTilesCheckBox = new JCheckBox("Add stop tiles: bench 1 turn, tavern 3 turns");
            prizeTilesCheckBox = new JCheckBox("Add prize tiles: Roll Again and Move Again");
            JButton nextButton = new JButton("Next");
            // Add fields to panel
            panel.add(autoAdvanceCheckBox);
            if(nDices>1)
                panel.add(singleDiceCheckBox);
            panel.add(nextButton);
            // Add panel to frame
            add(panel);
            // Add action listener to button
            nextButton.addActionListener(_ -> nextFrame());
        }

        private void nextFrame() {
            boolean specialTiles = specialTilesCheckBox.isSelected();
            boolean autoAdvance = autoAdvanceCheckBox.isSelected();
            boolean singleDice = singleDiceCheckBox.isSelected();


        }
    }

}
