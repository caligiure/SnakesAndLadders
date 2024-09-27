import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

abstract class GameManager implements ActionListener {
    private final PrimaryRulesRecord primaryRules;
    private final SpecialRulesRecord specialRules;
    private final GameFrame gameFrame;
    int nextPlayer = 0; // indicates the number of the player who must roll the dices
    private final int[] playersPosition;
    private final LinkedList<Integer> stoppedPlayers = new LinkedList<>();
    private final LinkedList<Integer> hasDenyStopPlayers = new LinkedList<>();

    public GameManager(PrimaryRulesRecord primaryRules, SpecialRulesRecord specialRules, GameFrame gameFrame) {
        this.primaryRules = primaryRules;
        this.specialRules = specialRules;
        this.gameFrame = gameFrame;
        playersPosition = new int[primaryRules.nPlayers()];
        for(int i = 0; i< primaryRules.nPlayers(); i++) // put every player on the board
            movePlayer(i, 1, 0);
        gameFrame.appendGameLog("Next Player: " + gameFrame.getPlayerTag(0) + ".\n");
    }

    void rollDice() {
        int currentPlayer = nextPlayer;
        int currentPosition = playersPosition[currentPlayer];
        int diceSum;
        boolean doubleSix = false;
        if(stoppedPlayers.contains(currentPlayer)) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " is stopped and must wait ⏳.\n");
            stoppedPlayers.removeFirstOccurrence(currentPlayer);
        } else {
            if (specialRules.singleDice() && currentPosition >= primaryRules.nRows() * primaryRules.nCols() - 6) // if the player is on one of the last 6 cells and the rule singleDice is active
                diceSum = sumDices(currentPlayer, 1);
            else
                diceSum = sumDices(currentPlayer, primaryRules.nDice());
            if (specialRules.doubleSix() && diceSum == 12) { // if the rule doubleSix is active
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " got a double six!⚅⚅\n");
                doubleSix = true;
            }
            int newPosition = calculateNewPosition(currentPosition, diceSum);
            movePlayer(currentPlayer, newPosition, diceSum);
            if (doubleSix) {
                rollDice(); // if the player got a double six, he must roll again
            }
        }
    } // rolls the dice and manages the consequences

    private int sumDices(int currentPlayer, int nDices) {
        Random rand = new Random();
        int diceSum = 0; // sums the total of nDice
        StringBuilder visualResult = new StringBuilder();
        for (int i = 0; i < nDices; i++) {
            int res = rand.nextInt(1,7);
            diceSum += res;
            switch (res) {
                case 1:
                    visualResult.append("⚀");
                    break;
                case 2:
                    visualResult.append("⚁");
                    break;
                case 3:
                    visualResult.append("⚂");
                    break;
                case 4:
                    visualResult.append("⚃");
                    break;
                case 5:
                    visualResult.append("⚄");
                    break;
                default:
                    visualResult.append("⚅");
                    break;
            }
        }
        gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " got a " + diceSum + visualResult + ".\n");
        return diceSum;
    } //calculates the sum of the dices and visualizes the results in the gameLog

    private int calculateNewPosition(int currentPosition, int diceSum) {
        int newPosition = currentPosition + diceSum;
        int finalCell = primaryRules.nRows()*primaryRules.nCols();
        if (newPosition > finalCell) { // the last cell must be reached with an exact shot
            newPosition = finalCell - (newPosition - finalCell);
        }
        return newPosition;
    } // calculates the new position of a player, based on its currentPosition and the sum of the dice

    private void movePlayer(int playerIndex, int newPosition, int diceSum) {
        // remove player from the old cell
        int oldPosition = playersPosition[playerIndex];
        if(oldPosition >= 1) {
            JLabel cell = gameFrame.getCellLabel(oldPosition);
            StringBuilder newText = new StringBuilder("<html>"+oldPosition + ")");
            for (int otherPlayer = 0; otherPlayer < primaryRules.nPlayers(); otherPlayer++) {
                if (otherPlayer != playerIndex && playersPosition[otherPlayer] == oldPosition)
                    newText.append(" ").append(gameFrame.getPlayerTag(otherPlayer)); // the other players must be re-added if they are still in the old position
            }
            newText.append("</html>");
            cell.setText(newText.toString());
        }
        // add the player to the new position
        playersPosition[playerIndex] = newPosition;
        JLabel cell = gameFrame.getCellLabel(newPosition);
        StringBuilder newText = new StringBuilder("<html>"+newPosition + ")");
        for(int player = 0; player < primaryRules.nPlayers(); player++) {
            if(playersPosition[player] == newPosition)
                newText.append(" ").append(gameFrame.getPlayerTag(player)); // the other players must be re-added if they were in this cell
        }
        newText.append("</html>");
        cell.setText(newText.toString());
        gameFrame.repaint(); // to avoid graphic errors
        gameFrame.setPositionOnPlayersTable(newPosition, playerIndex); // sets the new position on the players table
        gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(playerIndex) + " moves to cell " + newPosition + ".\n");
        checkTile(playerIndex, newPosition, diceSum); // check for snakes, ladders, special tiles or final cell
    } // removes a player from the old cell and adds it to the new one

    private void checkTile(int currentPlayer, int position, int diceSum) {
        int finalCell = primaryRules.nRows()*primaryRules.nCols();
        int[] coords = GameFrame.findCoordinates(position, primaryRules.nRows(), primaryRules.nCols());
        Content content = gameFrame.getcellContent(coords);
        if(position == finalCell) {
            endGame(currentPlayer);
        } else if(content.equals(Content.ladderBottom)) {
            int ladderTop = gameFrame.checkLadder(position); // checks if the specified position contains a ladder bottom and returns the position of the top of the ladder
            if(ladderTop != -1) {
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " stepped on a ladder!⏫\n");
                movePlayer(currentPlayer, ladderTop, diceSum);
            }
        } else if(content.equals(Content.snakeHead)) {
            int snakeTail = gameFrame.checkSnake(position); // checks if the specified position contains a snake head and returns the position of the tail of the snake
            if(snakeTail != -1) {
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " stepped on a snake!⏬\n");
                movePlayer(currentPlayer, snakeTail, diceSum);
            }
        } else if(content.equals(Content.rollAgain)) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " stepped on a special tile!✨\n");
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " must roll again!↺⚀⚅\n");
            rollDice();
        } else if(content.equals(Content.moveAgain)) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " stepped on a special tile!✨\n");
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " must move again!⏩\n");
            int newPosition = calculateNewPosition(playersPosition[currentPlayer], diceSum);
            movePlayer(currentPlayer, newPosition, diceSum);
        } else if(content.equals(Content.bench)) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " stepped on a bench tile!❌\n");
            if(hasDenyStopPlayers.contains(currentPlayer)){
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " uses a deny stop card to avoid getting stopped!✋\n");
                hasDenyStopPlayers.removeFirstOccurrence(currentPlayer);
            } else {
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " is stopped for 1 turn!⏳\n");
                stoppedPlayers.add(currentPlayer);
            }
        } else if (content.equals(Content.tavern)) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " stepped on a tavern tile!❌\n");
            if (hasDenyStopPlayers.contains(currentPlayer)) {
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " uses a deny stop card to avoid getting stopped!✋\n");
                hasDenyStopPlayers.removeFirstOccurrence(currentPlayer);
            } else {
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " is stopped for 3 turns!⏳\n");
                stoppedPlayers.add(currentPlayer);
                stoppedPlayers.add(currentPlayer);
                stoppedPlayers.add(currentPlayer);
            }
        } else if(content.equals(Content.drawCard)) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " stepped on a card tile!♠♣♥♦\n");
            drawCard(currentPlayer, diceSum);
        }
    } // checks the content of the cell reached by the player

    private void drawCard(int currentPlayer, int diceSum) {
        Random rand = new Random();
        int card; // there are 4 types of card: stop(0), moveAgain(1), rollAgain(2), denyStop(3)
        if(specialRules.denyStopCard()) // if the rule isn't active the card denyStop can't be drawn
            card = rand.nextInt(0, 4);
        else
            card = rand.nextInt(0, 3);
        if(card == 0) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " draws a stop card!❌\n");
            if(hasDenyStopPlayers.contains(currentPlayer)){
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " uses a deny stop card to avoid getting stopped!✋\n");
                hasDenyStopPlayers.removeFirstOccurrence(currentPlayer);
            } else {
                gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " is stopped for 1 turn!⏳\n");
                stoppedPlayers.add(currentPlayer);
            }
        } else if(card == 1) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " draws a move again card!⏩\n");
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " must move again!⏩\n");
            int newPosition = calculateNewPosition(playersPosition[currentPlayer], diceSum);
            movePlayer(currentPlayer, newPosition, diceSum);
        } else if(card == 2) {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " draws a roll again card!↺⚀⚅\n");
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " must roll again!↺⚀⚅\n");
            rollDice();
        } else {
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " draws a deny stop card!✋\n");
            gameFrame.appendGameLog("Player " + gameFrame.getPlayerTag(currentPlayer) + " will hold this card until needed!✋\n");
            hasDenyStopPlayers.add(currentPlayer);
        }
    } // draws a card randomly

    void endGame(int currentPlayer) {
        String winnerMessage = "Player " + gameFrame.getPlayerTag(currentPlayer) + " wins the game!";
        JOptionPane.showMessageDialog(gameFrame, winnerMessage, "Winner!", JOptionPane.INFORMATION_MESSAGE);
        // exit or restart the game
        int response = JOptionPane.showConfirmDialog(gameFrame, "Do you want to start a new match?", "Restart?", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            new Game(primaryRules, specialRules);  // reset this game with the same configuration
            gameFrame.dispose();
        } else {
            new GameConfiguration(rules); // goes back to main menu
            gameFrame.dispose();
        }
    } // ends the game if a player reaches the last cells

    void nextTurn() {
        nextPlayer = (nextPlayer +1) % primaryRules.nPlayers();  // select the next player
        gameFrame.appendGameLog("--------------\nNext Player: " + gameFrame.getPlayerTag(nextPlayer) + ".\n");
    } // updates the turn
}
