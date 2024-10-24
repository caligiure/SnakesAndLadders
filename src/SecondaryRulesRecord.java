import java.io.Serializable;

public record SecondaryRulesRecord(
        boolean autoAdvance,
        boolean singleDice,
        boolean doubleSix,
        boolean stopTiles,
        boolean moveAgainTiles,
        boolean rollAgainTiles,
        boolean addCards,
        boolean denyStopCard
) implements Serializable {}
