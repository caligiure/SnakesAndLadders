import java.awt.event.ActionEvent;

class GameManagerManualAdvance extends GameManager {

    public GameManagerManualAdvance(PrimaryRulesRecord primaryRules, SecondaryRulesRecord specialRules, GameFrame gameFrame) {
        super(primaryRules, specialRules, gameFrame);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // advance the game by one turn every time the user presses the button to roll the dices
        rollDice();
        nextTurn();
    }

}
