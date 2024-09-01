import java.awt.event.ActionEvent;
import java.util.concurrent.TimeUnit;

class GameManagerAutoAdvance extends GameManager {
    private boolean end;

    public GameManagerAutoAdvance(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules, GameFrame gameFrame) {
        super(primaryRules, specialRules, gameFrame);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // begin advancing automatically the game after the user presses the start button
        while(!end) {
            rollDice();
            try {
                TimeUnit.SECONDS.sleep(3000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            nextTurn();
        }
    }

    @Override
    void endGame(int currentPlayer) {
        end = true;
        super.endGame(currentPlayer);
    }

}
