package bots;

import minmax.BoardStatus;
import minmax.MinMax;
import minmax.Node;
import trifleConsole.boardifier.model.animation.Animation;
import trifleConsole.boardifier.model.animation.AnimationTypes;
import trifleGraphic.boardifierGraphic.control.ActionFactory;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.action.ActionList;
import trifleGraphic.controllers.GameController;
import trifleGraphic.controllers.BotDecider;
import trifleGraphic.model.Pawn;
import trifleGraphic.model.TrifleBoard;
import trifleGraphic.model.TrifleStageModel;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;

import static trifleGraphic.controllers.GameMouseController.ANIMATION_FACTOR;
import static trifleGraphic.controllers.GameMouseController.ANIMATION_TYPE;

public class DeterministicMinMaxBotGraphic extends BotDecider {
    private final MinMax minMax;

    public DeterministicMinMaxBotGraphic(Model model, Controller controller) {
        super(model, controller);

        this.minMax = new MinMax();
    }

    @Override
    public ActionList decide() {
        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();
        GameController controller = (GameController) this.control;

        BoardStatus boardStatus = new BoardStatus(
                stageModel.getBluePlayer(),
                stageModel.getCyanPlayer(),
                (TrifleBoard) stageModel.getBoard(),
                stageModel
        );

        for (int[] row: boardStatus.getMatrix())
            System.out.println(Arrays.toString(row));

        this.minMax.reset();
        this.minMax.buildTree(boardStatus, model.getIdPlayer());

        Node nextMove = this.minMax.minimax(model.getIdPlayer());
        if (nextMove == null) {
            System.out.println("The bot " + model.getIdPlayer() + " cannot move his pawn.");
            stageModel.setPlayerBlocked(model.getIdPlayer(), true);

            Point lastOpponentMove = boardStatus.getLastMove((model.getIdPlayer() + 1) % 2);
            if (lastOpponentMove != null) {

                int bgColorIndex = TrifleBoard.BOARD[lastOpponentMove.x][lastOpponentMove.y];

                Pawn pawn = stageModel.getPlayerPawn(model.getIdPlayer(), bgColorIndex);

                if (model.getIdPlayer() == 0) {
                    stageModel.setLastBluePlayerMove(pawn.getCoords());
                } else {
                    stageModel.setLastCyanPlayerMove(pawn.getCoords());
                }
            }

            return new ActionList();
        }
        stageModel.setPlayerBlocked(model.getIdPlayer(), false);
        System.out.println("Choice of the MinMax:\n" + nextMove);

        this.minMax.getTracker().displayStatistics();
        this.minMax.getTracker().sendStatisticsToApi();

        Pawn pawnInvolved = stageModel.getPlayerPawn(
                model.getIdPlayer(),
                nextMove.getPawnInvolved().getColorIndex()
        );

        assert pawnInvolved != null;

        ActionList actions = ActionFactory.generatePutInContainer(
                controller,
                model,
                pawnInvolved,
                TrifleBoard.BOARD_ID,
                nextMove.getMoveDone().y,
                nextMove.getMoveDone().x,
                ANIMATION_TYPE,
                ANIMATION_FACTOR / 4.0
        );
        actions.setDoEndOfTurn(true);

        controller.registerMove(
                stageModel,
                nextMove.getMoveDone(),
                GameController.normalizeCoordinate(pawnInvolved.getCoords(), false)
                    + GameController.normalizeCoordinate(nextMove.getMoveDone(), false),
                pawnInvolved
        );
        pawnInvolved.setCoords(new Point(nextMove.getMoveDone().x, nextMove.getMoveDone().y));

        return actions;
    }
}
