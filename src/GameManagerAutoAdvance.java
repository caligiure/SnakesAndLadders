import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.TimeUnit;

class GameManagerAutoAdvance extends GameManager {
    private boolean running = false;
    private final JButton actionButton;
    private final AdvancerThread advancerThread;

    public GameManagerAutoAdvance(PrimaryRulesRecord primaryRules, SecondaryRulesRecord specialRules, GameFrame gameFrame) {
        super(primaryRules, specialRules, gameFrame);
        actionButton = gameFrame.getActionButton();
        advancerThread = new AdvancerThread();
        advancerThread.setDaemon(true);
    }

    private synchronized boolean getRunning() {
        return running;
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void actionPerformed(ActionEvent e) { // begin advancing automatically the game after the user presses the start button
        actionButton.setEnabled(false);
        setRunning(true);
        try {
            advancerThread.start();
        } catch (IllegalThreadStateException ignored) {}
    }

    @Override
    void endGame(int currentPlayer) {
        setRunning(false);
        super.endGame(currentPlayer);
    }

    private class AdvancerThread extends Thread {
        public void run() {
            while(getRunning()) {
                rollDice();
                try {
                    TimeUnit.SECONDS.sleep(3);
                    //Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                if(getRunning())
                    nextTurn();
            }
        }
    }

}
