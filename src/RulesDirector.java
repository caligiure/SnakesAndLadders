public class RulesDirector {
    private final RulesBuilder builder;

    public RulesDirector(RulesBuilder builder) {
        this.builder = builder;
    }

    public void construct() {
        builder.buildRules();
    }
}
