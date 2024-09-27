import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Main {

    public static void main(String[] args) {
        InitialFrame init = new InitialFrame();
        init.setVisible(true);
    }

    private static class InitialFrame extends AbsFrame {
        public InitialFrame() {
            JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
            // Set buttons
            JButton setButton = new JButton("Start a new configuration");
            setButton.addActionListener(e -> newConfig()); // Add action listener to button
            JButton loadButton = new JButton("Load an old configuration");
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
            new GameConfiguration(null);
            this.setVisible(false); // dispose later (da controllare)
        }

        private void loadConfig() {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                    // Load the rules from the file
                    Rules rules = (Rules) inputStream.readObject();
                    JOptionPane.showMessageDialog(this, "Configuration loaded successfully.", "Configuration loaded", JOptionPane.INFORMATION_MESSAGE);
                    new GameConfiguration(rules);
                    this.setVisible(false); // dispose later da controllare
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

}