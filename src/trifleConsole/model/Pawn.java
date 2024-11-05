package trifleConsole.model;

import trifleConsole.boardifier.model.ElementTypes;
import trifleConsole.boardifier.model.GameElement;
import trifleConsole.boardifier.model.GameStageModel;
import trifleConsole.boardifier.view.ConsoleColor;

import java.awt.*;

/**
 * This is a pawn in the game which store what color he is.
 */
public class Pawn extends GameElement {
    public static final int PAWN_ELEMENT_ID = 50;
    public static final int BLUE_PLAYER = 1;
    public static final int CYAN_PLAYER = 2;

    public static final String[] COLORS = new String[]{
            ConsoleColor.CYAN,
            ConsoleColor.BLUE,
            ConsoleColor.PURPLE,
            ConsoleColor.WHITE,
            ConsoleColor.YELLOW,
            ConsoleColor.RED,
            ConsoleColor.GREEN,
            ConsoleColor.BLACK_BRIGHT
    };

    public static final String[] BG_COLORS = new String[]{
            ConsoleColor.CYAN_BACKGROUND,
            ConsoleColor.BLUE_BACKGROUND,
            ConsoleColor.PURPLE_BACKGROUND,
            ConsoleColor.WHITE_BACKGROUND,
            ConsoleColor.YELLOW_BACKGROUND,
            ConsoleColor.RED_BACKGROUND,
            ConsoleColor.GREEN_BACKGROUND,
            ConsoleColor.BLACK_BACKGROUND
    };

    private final int colorIndex;
    private final int playerNumber;

    private Point coords;

    public Pawn(int colorIndex, int playerNumber, GameStageModel gameStageModel, int x, int y) {
        super(gameStageModel);

        ElementTypes.register("pawn", PAWN_ELEMENT_ID);

        this.type = ElementTypes.getType("pawn");

        this.colorIndex = colorIndex;
        this.playerNumber = playerNumber;

        this.coords = new Point(x, y);
    }

    public Point getCoords(){
        return coords;
    }
    public void setCoords(Point coords){
        this.coords = coords;
    }

    public int getColorIndex(){
        return colorIndex;
    }

    public int getPlayerNumber(){
        return playerNumber;
    }

    /**
     * Format the pawn information
     * @return Return a string in the form of "Cyan (A1)
     */
    public String getFormattedPawnId() {

        String sb = COLORS[colorIndex];
        sb += switch (this.getColorIndex()) {
            case 0 -> "Cyan  ";
            case 1 -> "Blue  ";
            case 2 -> "Purple";
            case 3 -> "White ";
            case 4 -> "Yellow";
            case 5 -> "Red   ";
            case 6 -> "Green ";
            case 7 -> "Black ";
            default -> "Unknown";
        };
        sb += ConsoleColor.RESET;

        sb +=" (";
        sb += (char) (this.getCoords().x + 65);
        sb += (this.getCoords().y + 1);
        sb += ')';

        return sb;
    }

    @Override
    public String toString(){
        return "Pawn { colorIndex: " + colorIndex
                + ", playerNumber: " + playerNumber
                + ", coords: (" + coords.x + ", " + coords.y  + ") }";
    }
}
