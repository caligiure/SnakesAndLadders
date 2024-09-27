import javax.swing.*;
import java.awt.*;
import java.io.*;

public class GameConfiguration {
    private AbsRules primaryRules;

    private final JFrame configFrame;

    public GameConfiguration(Rules rules) {
        configFrame = new ConfigFrame();
        configFrame.setVisible(true);
    }

    private class ConfigFrame extends AbsFrame {
        // Fields for configuration
        private final JTextField playersField;
        private final JTextField rowsField;
        private final JTextField columnsField;
        private final JTextField laddersField;
        private final JTextField snakesField;
        private final JComboBox<String> diceComboBox;

        public ConfigFrame() {
            setTitle("Configure the rules of the game");
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            // Primary fields
            playersField = new JTextField(""+primaryRules.nPlayers());
            rowsField = new JTextField(""+primaryRules.nRows());
            columnsField = new JTextField(""+primaryRules.nCols());
            laddersField = new JTextField(""+primaryRules.nLadders());
            snakesField = new JTextField(""+primaryRules.nSnakes());
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            diceComboBox.setSelectedIndex(primaryRules.nDice() - 1);
            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> nextFrame());
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> previousFrame());
            // Add fields to panel
        }

    }

    private class ConcRules implements Rules {

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
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            // Primary fields
            playersField = new JTextField(""+primaryRules.nPlayers());
            rowsField = new JTextField(""+primaryRules.nRows());
            columnsField = new JTextField(""+primaryRules.nCols());
            laddersField = new JTextField(""+primaryRules.nLadders());
            snakesField = new JTextField(""+primaryRules.nSnakes());
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            diceComboBox.setSelectedIndex(primaryRules.nDice() - 1);
            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> nextFrame());
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> previousFrame());
            // Add fields to panel
            panel.add(new JLabel("Number of players:"));
            panel.add(playersField);
            panel.add(new JLabel("Number of rows:"));
            panel.add(rowsField);
            panel.add(new JLabel("Number of columns:"));
            panel.add(columnsField);
            panel.add(new JLabel("Number of ladders:"));
            panel.add(laddersField);
            panel.add(new JLabel("Number of snakes:"));
            panel.add(snakesField);
            panel.add(new JLabel("Number of dice:"));
            panel.add(diceComboBox);
            panel.add(backButton);
            panel.add(nextButton);
            // Add panel to frame
            add(panel);
        }

        private void nextFrame() {
            try {
                int numPlayers = Integer.parseInt(playersField.getText());
                int numRows = Integer.parseInt(rowsField.getText());
                int numCols = Integer.parseInt(columnsField.getText());
                int numLadders = Integer.parseInt(laddersField.getText());
                int numSnakes = Integer.parseInt(snakesField.getText());
                int numDice = diceComboBox.getSelectedIndex() + 1;
                if (numDice < 1)
                    numDice = 1;
                if (numPlayers <= 0)
                    JOptionPane.showMessageDialog(this, "The number of players cannot be 0.",
                            "Invalid Number of Players", JOptionPane.ERROR_MESSAGE);
                else if (numRows < 3 || numCols < 3)
                    JOptionPane.showMessageDialog(this, "Rows and Columns values must be at least 3.",
                            "Invalid Rows and Columns", JOptionPane.ERROR_MESSAGE);
                else if ( (numLadders + numSnakes) > ((numCols-2) * (numRows/2)) )
                    JOptionPane.showMessageDialog(this,
                            "A "+numRows+"x"+numCols+" board can contain a maximum of "+( (numCols-2) * (numRows/2) )+" elements, summing both snakes and ladders",
                            "Too many snakes and ladders", JOptionPane.ERROR_MESSAGE);
                else {
                    // Save the values in the primaryRules record
                    primaryRules = new PrimaryRulesRecord(numPlayers, numRows, numCols, numDice, numLadders, numSnakes);
                    // next frame
                    JFrame specialRulesFrame = new SpecialRulesFrame();
                    specialRulesFrame.setVisible(true);
                    this.setVisible(false); // dispose later
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The typed values aren't valid numbers.", "Values Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void previousFrame() {
            configFrame.setVisible(true);
            this.dispose();
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
        private final JCheckBox denyStopCardCheckBox;

        public SpecialRulesFrame() {
            // Set the JFrame
            setTitle("Select special rules");
            setSize(650, 600);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(11, 1, 10, 10));
            // Rules fields
            autoAdvanceCheckBox = new JCheckBox("Automatically roll the dice and advance", specialRules.autoAdvance());
            singleDiceCheckBox = new JCheckBox("Use a single die in the last 6 tiles, to reduce the risk of overshooting", specialRules.singleDice());
            doubleSixCheckBox = new JCheckBox("If you got double six, you can roll the dice a second time and move again before ending your turn", specialRules.doubleSix());
            if( primaryRules.nDice() < 2 ) { // rules not compatible with the use of a single dice
                singleDiceCheckBox.setEnabled(false);
                singleDiceCheckBox.setSelected(false);
                doubleSixCheckBox.setEnabled(false);
                doubleSixCheckBox.setSelected(false);
            }
            stopTilesCheckBox = new JCheckBox("Add stop tiles: bench stops you for 1 turn, tavern stops you for 3 turns", specialRules.stopTiles());
            moveAgainCheckBox = new JCheckBox("Add 'move again' tiles that let you move again without rolling the dice", specialRules.moveAgainTiles());
            rollAgainCheckBox = new JCheckBox("Add 'roll again' tiles that let you roll the dice a second time and move again", specialRules.rollAgainTiles());
            addCardsCheckBox = new JCheckBox("Add special tiles that let you draw a card", specialRules.addCards());
            denyStopCardCheckBox = new JCheckBox("Add a 'deny stop' card that you can use to avoid getting stopped", specialRules.denyStopCard());
            if( !addCardsCheckBox.isSelected() ) {
                denyStopCardCheckBox.setEnabled(false);
                denyStopCardCheckBox.setSelected(false);
            }
            addCardsCheckBox.addActionListener(e -> manageButtonDontStopCard());
            JButton startButton = new JButton("Start game");
            startButton.addActionListener(e -> startGame()); // Add action listener to button
            JButton saveButton = new JButton("Save configuration");
            saveButton.addActionListener(e -> saveConfig()); // Add action listener to button
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
            panel.add(denyStopCardCheckBox);
            panel.add(startButton);
            panel.add(saveButton);
            panel.add(backButton);
            add(panel); // Add panel to frame
        }

        private void manageButtonDontStopCard() {
            denyStopCardCheckBox.setEnabled( addCardsCheckBox.isSelected() );
            if (!addCardsCheckBox.isSelected()) {
                denyStopCardCheckBox.setSelected(false);
            }
        }

        private void saveConfig() {
            specialRules = new SpecialRulesRecord(
                    autoAdvanceCheckBox.isSelected(),
                    singleDiceCheckBox.isSelected(),
                    doubleSixCheckBox.isSelected(),
                    stopTilesCheckBox.isSelected(),
                    moveAgainCheckBox.isSelected(),
                    rollAgainCheckBox.isSelected(),
                    addCardsCheckBox.isSelected(),
                    denyStopCardCheckBox.isSelected()
            );
            // Save the records in a file
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                    outputStream.writeObject(primaryRules);
                    outputStream.writeObject(specialRules);
                    JOptionPane.showMessageDialog(this, "Configuration saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "The configuration couldn't be saved successfully.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void startGame() {
            specialRules = new SpecialRulesRecord(
                    autoAdvanceCheckBox.isSelected(),
                    singleDiceCheckBox.isSelected(),
                    doubleSixCheckBox.isSelected(),
                    stopTilesCheckBox.isSelected(),
                    moveAgainCheckBox.isSelected(),
                    rollAgainCheckBox.isSelected(),
                    addCardsCheckBox.isSelected(),
                    denyStopCardCheckBox.isSelected()
            );

            this.setVisible(false);
            new Game(primaryRules, specialRules); // creates the board
            configFrame.dispose();
            primaryRulesFrame.dispose();
            this.dispose();
        }

        private void previousFrame() {
            primaryRulesFrame.setVisible(true);
            this.dispose();
        }
    }
}
