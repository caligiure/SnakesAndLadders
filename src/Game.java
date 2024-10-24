import javax.swing.*;
import java.awt.*;

public class Game {
    private final PrimaryRulesRecord primaryRules;
    private final SecondaryRulesRecord specialRules;
    private final String[] playerName;

    public Game(PrimaryRulesRecord primaryRules, SecondaryRulesRecord specialRules) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;
        playerName = new String[primaryRules.nPlayers()];
        InsertPlayerNameFrame firstPlayerFrame = new InsertPlayerNameFrame(0); // frame to insert the first player's name
        firstPlayerFrame.setVisible(true);
    }

    private class InsertPlayerNameFrame extends JFrame {

        public InsertPlayerNameFrame(int i) {
            // Set the JFrame
            setTitle("Insert Player Name");
            setSize(500, 100);
            setLocationRelativeTo(null); // sets the location of this frame at the center of the screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10)); // this panel contains the other elements
            JTextField nameField = new JTextField("Player "+(i+1)); // field for the player's name
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> saveAndClose(i, nameField.getText())); // saves the name and closes the frame
            panel.add(new JLabel("Insert name for Player "+(i+1)+":"));
            panel.add(nameField);
            panel.add(okButton);
            add(panel);
        }

        private void saveAndClose(int i, String name) {
            playerName[i] = name;
            if(i < primaryRules.nPlayers()-1){
                InsertPlayerNameFrame nextPlayerFrame = new InsertPlayerNameFrame(i+1);
                nextPlayerFrame.setVisible(true);
            } else {
                GameFrame gameFrame = new GameFrame(primaryRules, specialRules, playerName);
                gameFrame.setVisible(true);
            }
            this.dispose();
        } // saves the name, opens the next frame and closes itself
    } // frame to insert a player's name

}
