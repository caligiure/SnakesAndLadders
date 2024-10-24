public class RulesConc extends Rules {
    final int nPlayers;
    final int nRows;
    final int nCols;
    final int nDice;
    final int nLadders;
    final int nSnakes;

    final boolean autoAdvance;
    final boolean singleDice;
    final boolean doubleSix;
    final boolean stopTiles;
    final boolean moveAgainTiles;
    final boolean rollAgainTiles;
    final boolean addCards;
    final boolean denyStopCard;

    public RulesConc(int nPlayers, int nRows, int nCols, int nDice, int nLadders, int nSnakes, boolean autoAdvance, boolean singleDice, boolean doubleSix, boolean stopTiles, boolean moveAgainTiles, boolean rollAgainTiles, boolean addCards, boolean denyStopCard) {
        this.nPlayers = nPlayers;
        this.nRows = nRows;
        this.nCols = nCols;
        this.nDice = nDice;
        this.nLadders = nLadders;
        this.nSnakes = nSnakes;
        this.autoAdvance = autoAdvance;
        this.singleDice = singleDice;
        this.doubleSix = doubleSix;
        this.stopTiles = stopTiles;
        this.moveAgainTiles = moveAgainTiles;
        this.rollAgainTiles = rollAgainTiles;
        this.addCards = addCards;
        this.denyStopCard = denyStopCard;
    }

}
