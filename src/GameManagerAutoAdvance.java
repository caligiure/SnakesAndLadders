import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.Semaphore;

class GameManagerAutoAdvance extends GameManager {
    private boolean running = false;
    private final JButton actionButton;
    private final AdvancerThread advancerThread;
    private final Semaphore run = new Semaphore(1);

    public GameManagerAutoAdvance(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules, GameFrame gameFrame) {
        super(primaryRules, specialRules, gameFrame);
        actionButton = gameFrame.getActionButton();
        advancerThread = new AdvancerThread();
    }

    private synchronized boolean getRunning() {
        return running;
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void actionPerformed(ActionEvent e) { // begin advancing automatically the game after the user presses the start button
        if(!getRunning()) { // if the game is not running
            actionButton.setEnabled(false);
            setRunning(true);
            advancerThread.setDaemon(true);
            advancerThread.start();
            actionButton.setText("Pause");
            actionButton.setEnabled(true);
        } else {
            actionButton.setEnabled(false);
            setRunning(false);
            actionButton.setText("Resume");
            actionButton.setEnabled(true);
        }
    }

    @Override
    void endGame(int currentPlayer) {
        actionButton.setEnabled(false);
        setRunning(false);
        super.endGame(currentPlayer);
    }

    private class AdvancerThread extends Thread {
        public void run() {
            while(getRunning()) {
                try {
                    run.acquire();
                    rollDice();
                    //noinspection BusyWait
                    Thread.sleep(3000);
                    nextTurn();
                    run.release();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}
