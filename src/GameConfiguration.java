import javax.swing.*;
import java.awt.*;

public class GameConfiguration {
    private ConcRules rules;
    private final ConfigurationCareTaker careTaker;

    public GameConfiguration(ConfigurationCareTaker careTaker) {
        this.careTaker = careTaker;
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
            JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
            playersField = new JTextField(""+ rules.nPlayers);
            rowsField = new JTextField(""+ rules.nRows);
            columnsField = new JTextField(""+ rules.nCols);
            laddersField = new JTextField(""+ rules.nLadders);
            snakesField = new JTextField(""+ rules.nSnakes);
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            diceComboBox.setSelectedIndex(rules.nDice - 1);
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
            // Secondary fields
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
            panel.add(autoAdvanceCheckBox);
            panel.add(singleDieCheckBox);
            panel.add(doubleSixCheckBox);
            panel.add(stopTilesCheckBox);
            panel.add(moveAgainCheckBox);
            panel.add(rollAgainCheckBox);
            panel.add(addCardsCheckBox);
            panel.add(denyStopCardCheckBox);
            // Buttons panel
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> goBack());
            JButton confirmButton = new JButton("Confirm");
            confirmButton.addActionListener(e -> confirm());
            // add buttons to panel
            panel.add(backButton);
            panel.add(confirmButton);
            // Add panel to frame
            add(panel);
        }

        private void goBack() {
            new ConfigurationCareTaker();
            this.dispose();
        }

        private void confirm() {
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

    private static class ConcRules extends Rules {
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

    public void setRules(Rules r) {
        if(r == null)
            rules = new ConcRules();
        else
            rules = (ConcRules) r;
        AbsFrame configFrame = new ConfigFrame();
        configFrame.setVisible(true);
    }

    public void startGame() {
        //new Game(rules);
    }

}
