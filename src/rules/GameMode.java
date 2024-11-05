package rules;

/**
 * The different game modes available
 */
public enum GameMode {
    /**
     * A single round
     */
    Fast,
    /**
     * A standard game with multiple rounds
     */
    Standard,
    Long,
    /**
     * A game with a large number of rounds
     */
    Marathon;

    /**
     * @return The name
     */
    public String toString() {
        return switch (this) {
            case Fast     -> "Fast";
            case Standard -> "Standard";
            case Long     -> "Long";
            case Marathon -> "Marathon";
        };
    }

    public String getDescription() {
        return "";
    }

    public int requiredPoints(){
        return switch (this) {
            case Fast -> 1;
            case Standard -> 3;
            case Long -> 7;
            case Marathon -> 15;
        };
    }

    public static GameMode defaultValue() {
        return Fast;
    }
}
