import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ConfigurationCareTaker {
    private GameConfiguration gameConfiguration;

    public ConfigurationCareTaker() {
        InitialFrame init = new InitialFrame(this);
        init.setVisible(true);
    }

    private class InitialFrame extends AbsFrame {
        private final ConfigurationCareTaker careTaker;

        public InitialFrame(ConfigurationCareTaker careTaker) {
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
            this.careTaker = careTaker;
        }

        private void newConfig() {
            gameConfiguration = new GameConfiguration(null, careTaker);
            this.dispose();
        }

        private void loadConfig() {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                    Rules rules = (Rules) inputStream.readObject();
                    JOptionPane.showMessageDialog(this, "Configuration loaded successfully.", "Configuration loaded", JOptionPane.INFORMATION_MESSAGE);
                    gameConfiguration = new GameConfiguration(rules, careTaker);
                    this.dispose(); // dispose later da controllare
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

    public void configurationDone() {
        ConfigurationSummary configurationSummary = new ConfigurationSummary();
        configurationSummary.setVisible(true);
    }

    private class ConfigurationSummary extends AbsFrame {
        public ConfigurationSummary() {
            JPanel panel = new JPanel(new GridLayout(1, 1, 10, 10));
            JButton startButton = new JButton("Start the game");
            startButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                        this, "Do you want to save this game configuration before starting?",
                        "Save Game Configuration", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    Rules rules = gameConfiguration.getRules();
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                            outputStream.writeObject(rules);
                            JOptionPane.showMessageDialog(this, "Configuration saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                            gameConfiguration.startGame();
                            this.dispose();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this, "The configuration couldn't be saved: "+ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    gameConfiguration.startGame();
                    this.dispose();
                }
            });
            panel.add(startButton);
            add(panel);
        }
    }
}
