package trifleGraphic.model;

/**
 * The purpose of this class is to register what moves have been done
 */
public class OldMove {
    private final String playerName;
    // Normalized to `color (position)`
    private final String pawn;
    // Normalized to A1
    private final String move;

    public OldMove(int playerId, String playerName, String pawn, String move){
        this.playerName = playerName;
        this.pawn = pawn;
        this.move = move;
    }

    private String getPlayerColor(){
        return "";
    }

    @Override
    public String toString() {
        return getPlayerColor() + playerName + "\u001b[0m" + " played " + pawn + " on " + move;
    }
}
