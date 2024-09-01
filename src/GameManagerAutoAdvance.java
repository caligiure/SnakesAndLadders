import java.awt.event.ActionEvent;

class GameManagerAutoAdvance extends GameManager {
    private boolean end;

    public GameManagerAutoAdvance(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules, GameFrame gameFrame) {
        super(primaryRules, specialRules, gameFrame);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // begin advancing automatically the game after the user presses the start button
        AdvancerThread thread = new AdvancerThread();
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    void endGame(int currentPlayer) {
        end = true;
        super.endGame(currentPlayer);
    }

    private class AdvancerThread extends Thread {
        public void run() {
            while(!end) {
                rollDice();
                try {
                    //noinspection BusyWait
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                if(!end)
                    nextTurn();
            }
        }
    }

}
