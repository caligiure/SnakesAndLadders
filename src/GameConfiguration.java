import javax.swing.*;
import java.awt.*;

public class GameConfiguration {
    private MutableFields fields;
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
            playersField = new JTextField(""+ fields.nPlayers);
            rowsField = new JTextField(""+ fields.nRows);
            columnsField = new JTextField(""+ fields.nCols);
            laddersField = new JTextField(""+ fields.nLadders);
            snakesField = new JTextField(""+ fields.nSnakes);
            diceComboBox = new JComboBox<>(new String[] {"1", "2"});
            diceComboBox.setSelectedIndex(fields.nDice - 1);
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
            autoAdvanceCheckBox = new JCheckBox("Automatically roll the dice and advance", fields.autoAdvance);
            singleDieCheckBox = new JCheckBox("Use a single die in the last 6 tiles, to reduce the risk of overshooting", fields.singleDice);
            doubleSixCheckBox = new JCheckBox("If you got double six, you can roll the dice a second time and move again before ending your turn", fields.doubleSix);
            stopTilesCheckBox = new JCheckBox("Add stop tiles: bench stops you for 1 turn, tavern stops you for 3 turns", fields.stopTiles);
            moveAgainCheckBox = new JCheckBox("Add 'move again' tiles that let you move again without rolling the dice", fields.moveAgainTiles);
            rollAgainCheckBox = new JCheckBox("Add 'roll again' tiles that let you roll the dice a second time and move again", fields.rollAgainTiles);
            addCardsCheckBox = new JCheckBox("Add special tiles that let you draw a card", fields.addCards);
            denyStopCardCheckBox = new JCheckBox("Add a 'deny stop' card that you can use to avoid getting stopped", fields.denyStopCard);
            if(fields.nDice < 2) {
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
                fields.nPlayers = Integer.parseInt(playersField.getText());
                fields.nRows = Integer.parseInt(rowsField.getText());
                fields.nCols = Integer.parseInt(columnsField.getText());
                fields.nLadders = Integer.parseInt(laddersField.getText());
                fields.nSnakes = Integer.parseInt(snakesField.getText());
                fields.nDice = diceComboBox.getSelectedIndex() + 1;
                if (fields.nDice < 1)
                    fields.nDice = 1;
                if (fields.nPlayers <= 0)
                    JOptionPane.showMessageDialog(this, "The number of players cannot be 0.",
                            "Invalid Number of Players", JOptionPane.ERROR_MESSAGE);
                else if (fields.nRows < 3 || fields.nCols < 3)
                    JOptionPane.showMessageDialog(this, "Rows and Columns values must be at least 3.",
                            "Invalid Rows and Columns", JOptionPane.ERROR_MESSAGE);
                else if ( (fields.nLadders + fields.nSnakes) > ((fields.nCols-2) * (fields.nRows/2)) )
                    JOptionPane.showMessageDialog(this,
                            "A "+ fields.nRows+"x"+ fields.nCols+" board can contain a maximum of "+( (fields.nCols-2) * (fields.nRows/2) )+" elements, summing both snakes and ladders",
                            "Too many snakes and ladders", JOptionPane.ERROR_MESSAGE);
                else {
                    fields.autoAdvance=autoAdvanceCheckBox.isSelected();
                    fields.singleDice=singleDieCheckBox.isSelected();
                    fields.doubleSix=doubleSixCheckBox.isSelected();
                    fields.stopTiles=stopTilesCheckBox.isSelected();
                    fields.moveAgainTiles=moveAgainCheckBox.isSelected();
                    fields.rollAgainTiles=rollAgainCheckBox.isSelected();
                    fields.addCards=addCardsCheckBox.isSelected();
                    fields.denyStopCard=denyStopCardCheckBox.isSelected();
                    careTaker.configurationDone();
                    this.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "The typed values aren't valid numbers.", "Values Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Rules getRulesMemento() {
        RulesConcBuilder builder = new RulesConcBuilder(fields);
        RulesDirector director = new RulesDirector(builder);
        director.construct();
        return builder.getResult();
    }

    public void setRulesMemento(Rules r) {
        if(r == null)
            fields = new MutableFields();
        else {
            RulesConc rules = (RulesConc) r;
            fields = new MutableFields();
            fields.nPlayers=rules.nPlayers;
            fields.nRows=rules.nRows;
            fields.nCols=rules.nCols;
            fields.nLadders=rules.nLadders;
            fields.nSnakes=rules.nSnakes;
            fields.nDice=rules.nDice;
            fields.autoAdvance=rules.autoAdvance;
            fields.singleDice=rules.singleDice;
            fields.doubleSix=rules.doubleSix;
            fields.stopTiles=rules.stopTiles;
            fields.moveAgainTiles=rules.moveAgainTiles;
            fields.rollAgainTiles=rules.rollAgainTiles;
            fields.addCards=rules.addCards;
            fields.denyStopCard=rules.denyStopCard;
        }
        AbsFrame configFrame = new ConfigFrame();
        configFrame.setVisible(true);
    }

    public void startGame() {

        //new Game(rules);
    }

}
