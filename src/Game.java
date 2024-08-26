import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class Game {
    // record of the primary rules
    private record PrimaryRulesSettings(
        int nPlayers,
        int nRows,
        int nCols,
        int nDices,
        int nLadders,
        int nSnakes
    ) {}
    // record of the special rules
    private record SpecialRulesSettings(
            boolean autoAdvance,
            boolean singleDice,
            boolean doubleSix,
            boolean stopTiles,
            boolean moveAgain,
            boolean rollAgain,
            boolean addCards,
            boolean dontStopCard
    ) {}
    // Since the Special Rules depend on the values of the Primary Rules,
    // the user must first set the Primary Rules
    // Example: The "singleDice" special rule can't be enabled
    // unless the "nDices" primary rules is set on a number greater than 1
    private PrimaryRulesSettings primaryRules;
    private SpecialRulesSettings specialRules;

    private JFrame configGame;
    private JFrame primaryRulesFrame;
    private JFrame specialRulesFrame;

    public Game() {
        primaryRulesFrame = new PrimaryRulesFrame();
        primaryRulesFrame.setVisible(true);
    }

    private class ConfigGame extends JFrame {
        public ConfigGame() {

        }
    }

    private class PrimaryRulesFrame extends JFrame {
        // Primary fields for configuration
        private final JTextField playersField;
        private final JTextField rowsField;
        private final JTextField columnsField;
        private final JTextField laddersField;
        private final JTextField snakesField;
        private final JComboBox<String> diceComboBox;

        public PrimaryRulesFrame() {
            // Set the JFrame
            setTitle("Configure the Snakes And Ladders game");
            setSize(600, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            // Primary fields
            playersField = new JTextField("2");
            rowsField = new JTextField("10");
            columnsField = new JTextField("10");
            laddersField = new JTextField("7");
            snakesField = new JTextField("7");
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
            nextButton.addActionListener(e -> nextFrame());
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
                    primaryRules = new PrimaryRulesSettings(numPlayers, numRows, numCols, numDices, numLadders, numSnakes);
                    // next frame
                    specialRulesFrame = new SpecialRulesFrame();
                    specialRulesFrame.setVisible(true);
                    this.setVisible(false);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The typed values aren't valid numbers.", "Values Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SpecialRulesFrame extends JFrame {
        // rules fields
        private final JCheckBox autoAdvanceCheckBox;
        private final JCheckBox singleDiceCheckBox;
        private final JCheckBox doubleSixCheckBox;
        private final JCheckBox stopTilesCheckBox;
        private final JCheckBox moveAgainCheckBox;
        private final JCheckBox rollAgainCheckBox;
        private final JCheckBox addCardsCheckBox;
        private final JCheckBox dontStopCardCheckBox;

        public SpecialRulesFrame() {
            // Set the JFrame
            setTitle("Select special rules");
            setSize(600, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(10, 1, 10, 10));
            // Rules fields
            autoAdvanceCheckBox = new JCheckBox("Automatically roll the dices and advance");
            singleDiceCheckBox = new JCheckBox("Use a single dice in the last 6 tiles");
            if( primaryRules.nDices<2 )
                singleDiceCheckBox.setEnabled(false);
            doubleSixCheckBox = new JCheckBox("Double Roll if you get Double Six");
            stopTilesCheckBox = new JCheckBox("Add Stopping tiles: bench stops you for 1 turn, tavern stops you for 3 turns");
            moveAgainCheckBox = new JCheckBox("Add special tiles that let you move again without rolling the dice");
            rollAgainCheckBox = new JCheckBox("Add special tiles that let you roll the dice again");
            addCardsCheckBox = new JCheckBox("Add special tiles that let you draw a card");
            dontStopCardCheckBox = new JCheckBox("Add a special card that you can keep to avoid getting stopped");
            dontStopCardCheckBox.setEnabled(false);
            dontStopCardCheckBox.setSelected(false);
            addCardsCheckBox.addActionListener(e -> manageButtonDontStopCard());
            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> StartGame()); // Add action listener to button
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> previousFrame());
            // Add fields to panel
            panel.add(autoAdvanceCheckBox);
            panel.add(singleDiceCheckBox);
            panel.add(doubleSixCheckBox);
            panel.add(stopTilesCheckBox);
            panel.add(moveAgainCheckBox);
            panel.add(rollAgainCheckBox);
            panel.add(addCardsCheckBox);
            panel.add(dontStopCardCheckBox);
            panel.add(nextButton);
            panel.add(backButton);
            add(panel); // Add panel to frame
        }

        private void manageButtonDontStopCard() {
            dontStopCardCheckBox.setEnabled( addCardsCheckBox.isSelected() );
            if (!addCardsCheckBox.isSelected()) {
                dontStopCardCheckBox.setSelected(false);
            }
        }

        private void SaveConfig() {

        }

        private void StartGame() {
            specialRules = new SpecialRulesSettings(
                    autoAdvanceCheckBox.isSelected(),
                    singleDiceCheckBox.isSelected(),
                    doubleSixCheckBox.isSelected(),
                    stopTilesCheckBox.isSelected(),
                    moveAgainCheckBox.isSelected(),
                    rollAgainCheckBox.isSelected(),
                    addCardsCheckBox.isSelected(),
                    dontStopCardCheckBox.isSelected() );

            //this.setVisible(false);
        }

        private void previousFrame() {
            primaryRulesFrame.setVisible(true);
            this.dispose();
        }
    }

}
