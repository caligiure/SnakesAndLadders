public class RulesConcBuilder implements RulesBuilder {
    private final MutableFields fields;
    private RulesConc rules;

    public RulesConcBuilder (MutableFields fields) {
        this.fields = fields;
    }

    @Override
    public void buildRules() {
        rules = new RulesConc(fields.nPlayers, fields.nRows, fields.nCols, fields.nDice, fields.nLadders, fields.nSnakes, fields.autoAdvance, fields.singleDice, fields.doubleSix, fields.stopTiles, fields.moveAgainTiles, fields.rollAgainTiles, fields.addCards, fields.denyStopCard);
    }

    public RulesConc getResult() {
        return rules;
    }

}
