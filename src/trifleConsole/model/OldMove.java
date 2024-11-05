package trifleConsole.model;

import trifleConsole.boardifier.view.ConsoleColor;

/**
 * The purpose of this class is to register what moves have been done
 */
public class OldMove {
    private final int playerId;
    private final String playerName;
    // Normalized to `color (position)`
    private final String pawn;
    // Normalized to A1
    private final String move;

    public OldMove(int playerId, String playerName, String pawn, String move){
        this.playerId = playerId;
        this.playerName = playerName;
        this.pawn = pawn;
        this.move = move;
    }

    private String getPlayerColor(){
        return switch (playerId) {
            case 0  -> ConsoleColor.BLUE;
            case 1  -> ConsoleColor.CYAN;
            default -> "";
        };
    }

    @Override
    public String toString() {
        return getPlayerColor() + playerName + ConsoleColor.RESET + " played " + pawn + " on " + move;
    }
}
