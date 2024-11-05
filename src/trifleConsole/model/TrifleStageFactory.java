package trifleConsole.model;

import trifleConsole.boardifier.model.GameStageModel;
import trifleConsole.boardifier.model.StageElementsFactory;
import trifleConsole.boardifier.model.TextElement;

import java.util.ArrayList;
import java.util.List;

import static trifleConsole.model.TrifleStageModel.MAX_HISTORY_SIZE;
import static trifleConsole.view.TrifleStageView.BOARD_WIDTH;

public class TrifleStageFactory extends StageElementsFactory {
    private final TrifleStageModel stageModel;

    public TrifleStageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (TrifleStageModel) gameStageModel;
    }

    @Override
    public void setup() {
        // Create the text that will be used to display the player name
        TextElement playerNameText = new TextElement(stageModel.getCurrentPlayerName() + " is playing. You start, so you can choose which pawn your moving.", stageModel);
        playerNameText.setLocation(BOARD_WIDTH + 2, 6);
        stageModel.setPlayerName(playerNameText);

        // Create the round counter
        TextElement roundCounterText = new TextElement("Round 1/" + stageModel.getGameMode().requiredPoints(), stageModel);
        roundCounterText.setLocation(BOARD_WIDTH + 2, 3);
        stageModel.setRoundCounter(roundCounterText);

        // create the player counter
        stageModel.updatePlayerPoints(0, 0);

        // add all texts history
        for (int i = 0; i < MAX_HISTORY_SIZE; i++) {
            TextElement text = new TextElement("", stageModel);
            text.setLocation(BOARD_WIDTH + 2 + 2, 7 + 2 + i);

            stageModel.getMovesHistory().add(text);
            stageModel.addElement(text);
        }

        // Create the board
        TrifleBoard board = new TrifleBoard(0, 0, this.stageModel);
        stageModel.setBoard(board);

        // Create the blue pawns
        List<Pawn> bluePawns = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            int colorIndex = TrifleBoard.BOARD[0][x];
            bluePawns.add(new Pawn(colorIndex, Pawn.BLUE_PLAYER, stageModel, x, 0));
        }
        stageModel.setBluePawns(bluePawns);

        // Create the cyan pawns
        List<Pawn> cyanPawns = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            int colorIndex = TrifleBoard.BOARD[7][x];
            cyanPawns.add(new Pawn(colorIndex,Pawn.CYAN_PLAYER,  stageModel, x, 7));
        }
        stageModel.setCyanPawns(cyanPawns);

        // Create all background cells
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int colorIndex = TrifleBoard.BOARD[y][x];

                BackgroundCell backgroundCell = new BackgroundCell(colorIndex, stageModel);
                stageModel.addBackgroundCell(backgroundCell);
            }
        }
    }
}
