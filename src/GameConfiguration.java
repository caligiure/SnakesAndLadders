import javax.swing.*;
import java.awt.*;
import java.io.*;

public class GameConfiguration {
    // Since the Special Rules depend on the values of the Primary Rules,
    // the user must first set the Primary Rules
    // Example: The "singleDice" special rule can't be enabled
    // unless the "nDices" primary rule is set on a number greater than 1
    private PrimaryRulesRecord primaryRules;
    private SpecialRulesRecord specialRules;

    private final JFrame configFrame;
    private JFrame primaryRulesFrame;

    public GameConfiguration() {
        configFrame = new ConfigFrame();
        configFrame.setVisible(true);
    }

    private class ConfigFrame extends JFrame {
        public ConfigFrame() {
            // Set the JFrame
            setTitle("Snakes And Ladders");
            setSize(300, 300);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Buttons panel
            JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
            // Set buttons
            JButton setButton = new JButton("Start a new configuration");
            setButton.addActionListener(e -> newConfig()); // Add action listener to button
            JButton loadButton = new JButton("Load a configuration");
            loadButton.addActionListener(e -> loadConfig());
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> exitGame());
            // Add buttons to panel
            panel.add(setButton);
            panel.add(loadButton);
            panel.add(exitButton);
            add(panel);
        }

        private void newConfig() {
            // default values
            primaryRules = new PrimaryRulesRecord(2, 10, 10, 2, 7, 7);
            specialRules = new SpecialRulesRecord(false, true, true, false, false, false, false,false);
            primaryRulesFrame = new PrimaryRulesFrame();
            primaryRulesFrame.setVisible(true);
            this.setVisible(false); // dispose later
        }

        private void loadConfig() {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                    // Load the records from the file
                    PrimaryRulesRecord pR = (PrimaryRulesRecord) inputStream.readObject();
                    SpecialRulesRecord sR = (SpecialRulesRecord) inputStream.readObject();
                    primaryRules = pR;
                    specialRules = sR;
                    JOptionPane.showMessageDialog(this, "Configuration loaded successfully.", "Configuration loaded", JOptionPane.INFORMATION_MESSAGE);
                    primaryRulesFrame = new PrimaryRulesFrame();
                    primaryRulesFrame.setVisible(true);
                    this.setVisible(false); // dispose later
                } catch (ClassNotFoundException | IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void exitGame() {
            int result = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to exit?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                this.dispose();
            }
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
            setTitle("Configure the Primary Rules");
            setSize(600, 600);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            // Primary fields
            playersField = new JTextField(""+primaryRules.nPlayers());
            rowsField = new JTextField(""+primaryRules.nRows());
            columnsField = new JTextField(""+primaryRules.nCols());
            laddersField = new JTextField(""+primaryRules.nLadders());
            snakesField = new JTextField(""+primaryRules.nSnakes());
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            diceComboBox.setSelectedIndex(primaryRules.nDices() - 1);
            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> nextFrame());
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> previousFrame());
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
                int numDices = diceComboBox.getSelectedIndex() + 1;
                if (numDices < 1)
                    numDices = 1;
                if (numPlayers <= 0)
                    JOptionPane.showMessageDialog(this, "The number of players cannot be 0.",
                            "Invalid Number of Players", JOptionPane.ERROR_MESSAGE);
                else if (numRows < 3 || numCols < 3)
                    JOptionPane.showMessageDialog(this, "Rows and Columns values must be at least 3.",
                            "Invalid Rows and Columns", JOptionPane.ERROR_MESSAGE);
                else if ( ((numLadders + numSnakes) * 2) > ((numCols * numRows) - 2) )
                    JOptionPane.showMessageDialog(this,
                            "A "+numRows+"x"+numCols+" board can contain a maximum of "+(((numCols*numRows)-2)/2)+" elements, summing both snakes and ladders",
                            "Too many snakes and ladders", JOptionPane.ERROR_MESSAGE);
                else {
                    // Save the values in the primaryRules record
                    primaryRules = new PrimaryRulesRecord(numPlayers, numRows, numCols, numDices, numLadders, numSnakes);
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
            setSize(600, 600);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            // Fields panel
            JPanel panel = new JPanel(new GridLayout(11, 1, 10, 10));
            // Rules fields
            autoAdvanceCheckBox = new JCheckBox("Automatically roll the dices and advance", specialRules.autoAdvance());
            singleDiceCheckBox = new JCheckBox("Use a single dice in the last 6 tiles", specialRules.singleDice());
            doubleSixCheckBox = new JCheckBox("Double Roll if you get Double Six", specialRules.doubleSix());
            if( primaryRules.nDices() < 2 ) { // rules not compatible with the use of a single dice
                singleDiceCheckBox.setEnabled(false);
                singleDiceCheckBox.setSelected(false);
                doubleSixCheckBox.setEnabled(false);
                doubleSixCheckBox.setSelected(false);
            }
            stopTilesCheckBox = new JCheckBox("Add Stopping tiles: bench stops you for 1 turn, tavern stops you for 3 turns", specialRules.stopTiles());
            moveAgainCheckBox = new JCheckBox("Add special tiles that let you move again without rolling the dice", specialRules.moveAgainTiles());
            rollAgainCheckBox = new JCheckBox("Add special tiles that let you roll the dice again", specialRules.rollAgainTiles());
            addCardsCheckBox = new JCheckBox("Add special tiles that let you draw a card", specialRules.addCards());
            denyStopCardCheckBox = new JCheckBox("Add a denyStop card that you can keep to avoid getting stopped", specialRules.denyStopCard());
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
