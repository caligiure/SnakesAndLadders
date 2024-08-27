import java.io.Serializable;

record PrimaryRulesRecord(
        int nPlayers,
        int nRows,
        int nCols,
        int nDices,
        int nLadders,
        int nSnakes
) implements Serializable {}
