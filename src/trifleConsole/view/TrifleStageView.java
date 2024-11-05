package trifleConsole.view;

import trifleConsole.boardifier.control.Logger;
import trifleConsole.boardifier.model.GameElement;
import trifleConsole.boardifier.model.GameStageModel;
import trifleConsole.boardifier.model.TextElement;
import trifleConsole.boardifier.view.*;
import trifleConsole.model.BackgroundCell;
import trifleConsole.model.TrifleStageModel;

public class TrifleStageView extends GameStageView {
    public TrifleStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    public static final int BOARD_WIDTH = 82;

    @Override
    public void createLooks() {
        TrifleStageModel model = (TrifleStageModel) this.gameStageModel;

        // add the look for all texts
        addLook(new TextLook(model.getPlayerName()));
        addLook(new TextLook(model.getRoundCounter()));
        addLook(new TextLook(model.getPlayerPoints()));

        for (TextElement historyText: model.getMovesHistory())
            addLook(new TextLook(historyText));

        // Create the main board (8x8)
        ClassicBoardLook boardLook = new ClassicBoardLook(PawnLook.HEIGHT, PawnLook.WIDTH, model.getBoard(), 1, 1, true);

        boardLook.setPadding(0);
        boardLook.setPaddingLeft(-4);
        boardLook.setPaddingRight(1);
        boardLook.setColWidth(PawnLook.WIDTH + 3);
        boardLook.setRowHeight(PawnLook.HEIGHT + 1);
        addLook(boardLook);

        // add look for all pawns
        for (int col = 0; col < 8; col++) { // col = `x` axis
            // Add blue
            GameElement bluePawn = model.getBluePlayer().get(col);
            ElementLook bluePawnLook = new PawnLook(bluePawn);
            addLook(bluePawnLook);
            model.getBoard().addElement(bluePawn, 0, col);

            // Add cyan
            GameElement cyanPawn = model.getCyanPlayer().get(col);
            ElementLook cyanPawnLook = new PawnLook(cyanPawn);
            addLook(cyanPawnLook);
            model.getBoard().addElement(cyanPawn, 7, col);
        }

        // add look for inners cases (the cell colors)
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int index = x + y * 8;
                BackgroundCell backgroundCell = model.getBackgroundCells().get(index);

                ElementLook look = new BackgroundCellLook(backgroundCell);

                addLook(look);
                boardLook.addInnerLook(look, x, y);
            }
        }

        boardLook.updateInners();

        Logger.debug("Finished creating game stage looks", this);
    }
}
