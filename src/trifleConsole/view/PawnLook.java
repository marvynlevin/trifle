package trifleConsole.view;

import trifleConsole.boardifier.model.Coord2D;
import trifleConsole.boardifier.model.GameElement;
import trifleConsole.boardifier.view.ConsoleColor;
import trifleConsole.boardifier.view.ElementLook;
import trifleConsole.model.Pawn;
import trifleConsole.model.TrifleBoard;

public class PawnLook extends ElementLook {
    public static final int WIDTH = 7;
    public static final int HEIGHT = 3;

    public PawnLook(GameElement element){
        super(element, WIDTH, HEIGHT);
    }

    protected void render() {
        Pawn pawn = (Pawn) element;

        int colorIndex = pawn.getColorIndex();
        String color = Pawn.COLORS[colorIndex];

        char pawnChar = getCharacter(pawn.getPlayerNumber());

        Coord2D pos = pawn.getLocation();
        int x = (int) pos.getX() / (WIDTH + 3);
        int y = (int) pos.getY() / (HEIGHT + 1);

        // Don't ask me why the fuck x & y are inverted, because I have no clue what the fuck I'm doing.
        // The only thing I know is that this is working...
        int bgColorIndex = TrifleBoard.BOARD[y][x];

        shape[1][1] += ConsoleColor.RESET;
        shape[1][3] = color + pawnChar;
        shape[1][4] = " " + Pawn.BG_COLORS[bgColorIndex];
    }

    private static char getCharacter(int playerNb){
        return switch (playerNb) {
            case 1 -> 'o';
            case 2 -> 'x';
            default -> ' ';
        };
    }
}
