import javax.swing.*;
import java.awt.*;
import java.io.*;

public class GameConfiguration {
    private ConcRules rules;
    private ConfigurationCareTaker careTaker;

    public GameConfiguration(Rules r, ConfigurationCareTaker careTaker) {
        if(r == null)
            rules = new ConcRules();
        else
            rules = (ConcRules) r;
        this.careTaker = careTaker;
        AbsFrame configFrame = new ConfigFrame();
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
        private final JCheckBox autoAdvanceCheckBox;
        private final JCheckBox singleDieCheckBox;
        private final JCheckBox doubleSixCheckBox;
        private final JCheckBox stopTilesCheckBox;
        private final JCheckBox moveAgainCheckBox;
        private final JCheckBox rollAgainCheckBox;
        private final JCheckBox addCardsCheckBox;
        private final JCheckBox denyStopCardCheckBox;

        public ConfigFrame() {
            setTitle("Configure the rules of the game");
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            // Primary fields
            JPanel panel1 = new JPanel(new GridLayout(6, 2, 10, 10));
            playersField = new JTextField(""+ rules.nPlayers);
            rowsField = new JTextField(""+ rules.nRows);
            columnsField = new JTextField(""+ rules.nCols);
            laddersField = new JTextField(""+ rules.nLadders);
            snakesField = new JTextField(""+ rules.nSnakes);
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            diceComboBox.setSelectedIndex(rules.nDice - 1);
            // Add fields to panel
            panel1.add(new JLabel("Number of players:"));
            panel1.add(playersField);
            panel1.add(new JLabel("Number of rows:"));
            panel1.add(rowsField);
            panel1.add(new JLabel("Number of columns:"));
            panel1.add(columnsField);
            panel1.add(new JLabel("Number of ladders:"));
            panel1.add(laddersField);
            panel1.add(new JLabel("Number of snakes:"));
            panel1.add(snakesField);
            panel1.add(new JLabel("Number of dice:"));
            panel1.add(diceComboBox);
            // Secondary fields
            JPanel panel2 = new JPanel(new GridLayout(8, 1, 10, 10));
            autoAdvanceCheckBox = new JCheckBox("Automatically roll the dice and advance", rules.autoAdvance);
            singleDieCheckBox = new JCheckBox("Use a single die in the last 6 tiles, to reduce the risk of overshooting", rules.singleDice);
            doubleSixCheckBox = new JCheckBox("If you got double six, you can roll the dice a second time and move again before ending your turn", rules.doubleSix);
            stopTilesCheckBox = new JCheckBox("Add stop tiles: bench stops you for 1 turn, tavern stops you for 3 turns", rules.stopTiles);
            moveAgainCheckBox = new JCheckBox("Add 'move again' tiles that let you move again without rolling the dice", rules.moveAgainTiles);
            rollAgainCheckBox = new JCheckBox("Add 'roll again' tiles that let you roll the dice a second time and move again", rules.rollAgainTiles);
            addCardsCheckBox = new JCheckBox("Add special tiles that let you draw a card", rules.addCards);
            denyStopCardCheckBox = new JCheckBox("Add a 'deny stop' card that you can use to avoid getting stopped", rules.denyStopCard);
            if(rules.nDice < 2) {
                singleDieCheckBox.setEnabled(false);
                singleDieCheckBox.setSelected(false);
                doubleSixCheckBox.setEnabled(false);
                doubleSixCheckBox.setSelected(false);
            }
            if( !addCardsCheckBox.isSelected() ) {
                denyStopCardCheckBox.setEnabled(false);
                denyStopCardCheckBox.setSelected(false);
            }
            diceComboBox.addActionListener(e -> {
                if( diceComboBox.getSelectedIndex() == 0 ) { // rules not compatible with the use of a single die
                    singleDieCheckBox.setEnabled(false);
                    singleDieCheckBox.setSelected(false);
                    doubleSixCheckBox.setEnabled(false);
                    doubleSixCheckBox.setSelected(false);
                } else {
                    singleDieCheckBox.setEnabled(true);
                    doubleSixCheckBox.setEnabled(true);
                }
            });
            addCardsCheckBox.addActionListener(e -> {
                denyStopCardCheckBox.setEnabled( addCardsCheckBox.isSelected() );
                if (!addCardsCheckBox.isSelected()) {
                    denyStopCardCheckBox.setSelected(false);
                }
            });
            // Add fields to panel
            panel2.add(autoAdvanceCheckBox);
            panel2.add(singleDieCheckBox);
            panel2.add(doubleSixCheckBox);
            panel2.add(stopTilesCheckBox);
            panel2.add(moveAgainCheckBox);
            panel2.add(rollAgainCheckBox);
            panel2.add(addCardsCheckBox);
            panel2.add(denyStopCardCheckBox);
            // Buttons panel
            JPanel panel3 = new JPanel(new GridLayout(1, 2, 10, 10));
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> goBack());
            JButton nextButton = new JButton("Next");
            nextButton.addActionListener(e -> goNext());

            // add buttons to panel
            panel3.add(backButton);
            panel3.add(nextButton);
            // Add panel to frame
            add(panel1);
            add(panel2);
            add(panel3);
        }

        private void goBack() {
            new ConfigurationCareTaker();
            this.dispose();
        }

        private void goNext() {
            try {
                rules.nPlayers = Integer.parseInt(playersField.getText());
                rules.nRows = Integer.parseInt(rowsField.getText());
                rules.nCols = Integer.parseInt(columnsField.getText());
                rules.nLadders = Integer.parseInt(laddersField.getText());
                rules.nSnakes = Integer.parseInt(snakesField.getText());
                rules.nDice = diceComboBox.getSelectedIndex() + 1;
                if (rules.nDice < 1)
                    rules.nDice = 1;
                if (rules.nPlayers <= 0)
                    JOptionPane.showMessageDialog(this, "The number of players cannot be 0.",
                            "Invalid Number of Players", JOptionPane.ERROR_MESSAGE);
                else if (rules.nRows < 3 || rules.nCols < 3)
                    JOptionPane.showMessageDialog(this, "Rows and Columns values must be at least 3.",
                            "Invalid Rows and Columns", JOptionPane.ERROR_MESSAGE);
                else if ( (rules.nLadders + rules.nSnakes) > ((rules.nCols-2) * (rules.nRows/2)) )
                    JOptionPane.showMessageDialog(this,
                            "A "+rules.nRows+"x"+rules.nCols+" board can contain a maximum of "+( (rules.nCols-2) * (rules.nRows/2) )+" elements, summing both snakes and ladders",
                            "Too many snakes and ladders", JOptionPane.ERROR_MESSAGE);
                else {
                    rules.autoAdvance=autoAdvanceCheckBox.isSelected();
                    rules.singleDice=singleDieCheckBox.isSelected();
                    rules.doubleSix=doubleSixCheckBox.isSelected();
                    rules.stopTiles=stopTilesCheckBox.isSelected();
                    rules.moveAgainTiles=moveAgainCheckBox.isSelected();
                    rules.rollAgainTiles=rollAgainCheckBox.isSelected();
                    rules.addCards=addCardsCheckBox.isSelected();
                    rules.denyStopCard=denyStopCardCheckBox.isSelected();
                    this.dispose();
                    careTaker.configurationDone();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The typed values aren't valid numbers.", "Values Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ConcRules implements Rules {
        int nPlayers = 2;
        int nRows = 10;
        int nCols = 10;
        int nDice = 2;
        int nLadders = 7;
        int nSnakes = 7;

        boolean autoAdvance = false;
        boolean singleDice = false;
        boolean doubleSix = false;
        boolean stopTiles = false;
        boolean moveAgainTiles = false;
        boolean rollAgainTiles = false;
        boolean addCards = false;
        boolean denyStopCard = false;
    }

    public Rules getRules() {
        return rules;
    }

    public void startGame() {

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
            playersField = new JTextField(""+ rules.nPlayers());
            rowsField = new JTextField(""+ rules.nRows());
            columnsField = new JTextField(""+ rules.nCols());
            laddersField = new JTextField(""+ rules.nLadders());
            snakesField = new JTextField(""+ rules.nSnakes());
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            diceComboBox.setSelectedIndex(rules.nDice() - 1);
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
                    // Save the values in the rules record
                    rules = new PrimaryRulesRecord(numPlayers, numRows, numCols, numDice, numLadders, numSnakes);
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
            if( rules.nDice() < 2 ) { // rules not compatible with the use of a single dice
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
                    outputStream.writeObject(rules);
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
            new Game(rules, specialRules); // creates the board
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
