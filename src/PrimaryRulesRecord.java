import java.io.Serializable;

public record PrimaryRulesRecord(
        int nPlayers,
        int nRows,
        int nCols,
        int nDices,
        int nLadders,
        int nSnakes
) implements Serializable {}
