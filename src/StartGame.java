import javax.swing.*;
import java.awt.*;

public class StartGame extends JFrame {

    // Components for game configuration
    private final JTextField playersField;
    private final JTextField rowsField;
    private final JTextField columnsField;
    private final JTextField laddersField;
    private final JTextField snakesField;
    private final JComboBox<String> diceComboBox;
    private final JCheckBox specialTilesCheckBox;
    private final JCheckBox autoAdvanceCheckBox;
    private final JButton startButton;

    public StartGame() {
        // Set the JFrame
        setTitle("Configure the Snakes And Ladders game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        // components panel
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        // Initialize components
        playersField = new JTextField();
        rowsField = new JTextField();
        columnsField = new JTextField();
        laddersField = new JTextField();
        snakesField = new JTextField();
        specialTilesCheckBox = new JCheckBox("Add special tiles");
        diceComboBox = new JComboBox<>(new String[] {"1", "2"});
        autoAdvanceCheckBox = new JCheckBox("Automatically roll the dices and advance");
        startButton = new JButton("Start game");

        // Add components to panel
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
        panel.add(autoAdvanceCheckBox);
        panel.add(specialTilesCheckBox);
        panel.add(startButton);

        add(panel);

        // Add action listener to the start button
        startButton.addActionListener(_ -> startGame());

    }

    private void startGame() {
        try {
            int numPlayers = Integer.parseInt(playersField.getText());
            int numRows = Integer.parseInt(rowsField.getText());
            int numCols = Integer.parseInt(columnsField.getText());
            int numLadders = Integer.parseInt(laddersField.getText());
            int numSnakes = Integer.parseInt(snakesField.getText());
            boolean specialTiles = specialTilesCheckBox.isSelected();
            int numDice = diceComboBox.getSelectedIndex() + 1;
            boolean autoAdvance = autoAdvanceCheckBox.isSelected();

            if(numDice < 0)
                numDice = 0;
            if(numPlayers <= 0)
                JOptionPane.showMessageDialog(this, "Please insert a number of players greater than 0.", "Invalid Number of Players", JOptionPane.ERROR_MESSAGE);
            if(numRows <= 0 && numCols <= 0)
                JOptionPane.showMessageDialog(this, "Please insert values greater than 0 for rows or columns.", "Invalid Rows or Columns", JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "The typed values aren't valid numbers.", "Values Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
