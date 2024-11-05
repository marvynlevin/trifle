package trifleConsole.view;

import trifleConsole.boardifier.view.ConsoleColor;
import trifleConsole.boardifier.model.GameElement;
import trifleConsole.boardifier.view.ElementLook;
import trifleConsole.model.BackgroundCell;
import trifleConsole.model.Pawn;

public class BackgroundCellLook extends ElementLook {
    public BackgroundCellLook(GameElement element) {
        super(element, PawnLook.WIDTH, PawnLook.HEIGHT);
    }

    protected void render(){
        BackgroundCell cell = (BackgroundCell) this.element;
        int colorIndex = cell.getColorIndex();

        shape[0][0] = Pawn.BG_COLORS[colorIndex] + " ";
        shape[0][PawnLook.WIDTH - 1] = " " + ConsoleColor.RESET;

        shape[1][0] = Pawn.BG_COLORS[colorIndex] + " ";
        shape[1][PawnLook.WIDTH - 1] = " " + ConsoleColor.RESET;

        shape[2][0] = Pawn.BG_COLORS[colorIndex] + " ";
        shape[2][PawnLook.WIDTH - 1] = " " + ConsoleColor.RESET;
    }
}
