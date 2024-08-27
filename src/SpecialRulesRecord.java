import java.io.Serializable;

record SpecialRulesRecord(
        boolean autoAdvance,
        boolean singleDice,
        boolean doubleSix,
        boolean stopTiles,
        boolean moveAgainTiles,
        boolean rollAgainTiles,
        boolean addCards,
        boolean dontStopCard
) implements Serializable {}
