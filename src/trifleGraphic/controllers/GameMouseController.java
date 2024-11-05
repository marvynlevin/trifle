package trifleGraphic.controllers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import trifleGraphic.boardifierGraphic.control.ActionFactory;
import trifleGraphic.boardifierGraphic.control.ActionPlayer;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.control.ControllerMouse;
import trifleGraphic.boardifierGraphic.model.Coord2D;
import trifleGraphic.boardifierGraphic.model.GameElement;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.action.ActionList;
import trifleGraphic.boardifierGraphic.model.animation.AnimationTypes;
import trifleGraphic.boardifierGraphic.view.View;
import trifleGraphic.model.Pawn;
import trifleGraphic.model.TrifleBoard;
import trifleGraphic.model.TrifleStageModel;
import trifleGraphic.view.TrifleBoardLook;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class  GameMouseController extends ControllerMouse implements EventHandler<MouseEvent> {
    public GameMouseController(Model model, View view, Controller controller) {
        super(model, view, controller);
    }

    public static final int ANIMATION_FACTOR = 12;
    public static final String ANIMATION_TYPE = AnimationTypes.MOVE_LINEARPROP;

    public void handle(MouseEvent event) {
        Coord2D clic = new Coord2D(event.getSceneX(), event.getSceneY());

        if (model.getElements() == null) return;
        List<GameElement> elementList = control.elementsAt(clic);

        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();

        System.out.println("state: " + stageModel.getState());

        if (stageModel.getState() == TrifleStageModel.SELECT_PAWN_STATE) {
            handlePawnSelectionState(clic, stageModel);
        } else if (stageModel.getState() == TrifleStageModel.SELECT_DEST_STATE) {
            processPawnDestination(clic, stageModel, elementList);
        }
    }

    /**
     * Manage the clic of the user to move a pawn at a specific destination. Implements all rules of the Kamisado
     * @param clic The clic coordinates
     * @param stageModel The stage model
     * @param elementList All elements that have been clicked since last frame.
     */
    public void processPawnDestination(Coord2D clic, TrifleStageModel stageModel, List<GameElement> elementList){
        handlePawnSelectionState(clic, stageModel);

        boolean boardClicked = false;
        for (GameElement element : elementList) {
            if (element == stageModel.getBoard()) {
                boardClicked = true; break;
            }
        }

        if (!boardClicked || model.getSelected().isEmpty()) return;

        Pawn pawn = (Pawn) model.getSelected().get(0);

        TrifleBoard board = (TrifleBoard) stageModel.getBoard();
        board.setValidCells(pawn.getCoords(), model.getIdPlayer(), pawn.getSumoLevel());

        TrifleBoardLook boardLook = (TrifleBoardLook) control.getElementLook(stageModel.getBoard());
        int[] dest = boardLook.getCellFromSceneLocation(clic);
        System.out.println("Mouse pawn destination: " + dest[0] + " " + dest[1]);

        if (board.canReachCell(dest[0], dest[1])) {
            System.out.println("The selected pawn can reach the cell at (" + dest[0] + ", " + dest[1] + ")");

            boolean isOshi = board.getElement(dest[0], dest[1]) != null && board.getElement(dest[0], dest[1]).getType() == Pawn.PAWN_ELEMENT_ID;

            ActionList actionList = ActionFactory.generatePutInContainer(
                    control,
                    model,
                    pawn,
                    TrifleBoard.BOARD_ID,
                    dest[0],
                    dest[1],
                    ANIMATION_TYPE,
                    ANIMATION_FACTOR
            );

            if (isOshi) {
                Point lastOpponentPawnOshied = null;
                for (int rowdest = 0; rowdest < pawn.getSumoLevel() + 1; rowdest++) {
                    int rowDest = dest[0] + (model.getIdPlayer() == 0 ? rowdest : -rowdest);
                    System.out.println("modifications [" + rowDest + "]["+ dest[1] + "] : " + rowDest);
                    if (board.getElement(rowDest, dest[1]) == null || board.getElement(rowDest, dest[1]).getType() != Pawn.PAWN_ELEMENT_ID) {
                        System.out.println("no YEET");
                        break;
                    }

                    System.out.println("YEET");

                    Pawn p = (Pawn) board.getElement(rowDest, dest[1]);

                    int newRow = rowDest + (model.getIdPlayer() == 0 ? 1 : -1);
                    p.setCoords(new Point(dest[1], newRow));
                    lastOpponentPawnOshied = (Point) p.getCoords().clone();

                    ActionList actionList2 = ActionFactory.generatePutInContainer(
                            control,
                            model,
                            p,
                            TrifleBoard.BOARD_ID,
                            newRow,
                            dest[1],
                            ANIMATION_TYPE,
                            ANIMATION_FACTOR
                    );

                    actionList.addAll(actionList2);
                }

                if (lastOpponentPawnOshied != null){
                    System.out.println("\n\n" + lastOpponentPawnOshied);
                    System.out.println(TrifleBoard.BOARD[lastOpponentPawnOshied.y][lastOpponentPawnOshied.x]);
                    System.out.println(Pawn.COLORS[TrifleBoard.BOARD[lastOpponentPawnOshied.y][lastOpponentPawnOshied.x]] + "\n\n");
                    if (model.getIdPlayer() == 0) {
                        stageModel.setLastCyanPlayerMove(lastOpponentPawnOshied);
                    } else {
                        stageModel.setLastBluePlayerMove(lastOpponentPawnOshied);
                    }
                }
            } else {
                actionList.setDoEndOfTurn(true);
                if (model.getIdPlayer() == 0) {
                    stageModel.setLastBluePlayerMove(new Point(dest[1], dest[0]));
                } else {
                    stageModel.setLastCyanPlayerMove(new Point(dest[1], dest[0]));
                }
            }

            stageModel.unselectAll();
            stageModel.setState(TrifleStageModel.SELECT_PAWN_STATE);
            board.resetReachableCells(false);


            pawn.setCoords(new Point(dest[1], dest[0]));
            if (((GameController) control).detectWin())
                return;

            System.out.println("1bluePlayerBlocked: " + stageModel.isBluePlayerBlocked());
            System.out.println("1cyanPlayerBlocked: " + stageModel.isCyanPlayerBlocked());

            stageModel.setPlayerBlocked(model.getIdPlayer(), false);

            ActionPlayer play = new ActionPlayer(model, control, actionList);
            play.start();

            GameController controller = (GameController) control;
            controller.registerMove(stageModel, new Point(dest[1], dest[0]), "", pawn);

            return;
        }

        // At the end, we remove the selected tag from the pawn and we reset the state
        stageModel.unselectAll();
        stageModel.setState(TrifleStageModel.SELECT_PAWN_STATE);

        board.resetReachableCells(false);
    }

    /**
     * Select a pawn at a specific coordinate
     * @param clic The clic coordinates
     * @param stageModel The stage Model
     * @return A pawn model of the current player if any
     */
    public Pawn getPawnFrom(Coord2D clic, TrifleStageModel stageModel) {
        System.out.println();
        System.out.println("Player ID: " + model.getIdPlayer());
        TrifleBoardLook boardLook = (TrifleBoardLook) control.getElementLook(stageModel.getBoard());

        int[] dest = boardLook.getCellFromSceneLocation(clic);

        System.out.println("Mouse destination: " + Arrays.toString(dest));
        for (Pawn p: stageModel.getPlayerPawns(model.getIdPlayer())) {
            System.out.println(p.getCoords());
            if (dest[0] == p.getCoords().getY() && dest[1] == p.getCoords().getX()) {
                System.out.println("Pawn: " + p.getColorIndex());
                return p;
            }
        }

        System.out.println();

        return null;
    }

    /**
     * Select the pawn at the given clic if there is any. Also implements the rules about the pawn selection.
     * @param clic The clic coordinates
     * @param stageModel The stage model
     */
    public void handlePawnSelectionState(Coord2D clic, TrifleStageModel stageModel) {
        Pawn pawn = getPawnFrom(clic, stageModel);
        if (pawn == null) return;

        TrifleBoard board = (TrifleBoard) stageModel.getBoard();

        // the pawn must play the same color cell
        Point lastOpponentMove;
        if (model.getIdPlayer() == 0) {
            lastOpponentMove = stageModel.getLastCyanPlayerMove();
        } else {
            lastOpponentMove = stageModel.getLastBluePlayerMove();
        }

        if (lastOpponentMove != null) {
            int colorIndex = TrifleBoard.BOARD[lastOpponentMove.y][lastOpponentMove.x];
            if (colorIndex != pawn.getColorIndex()){
                System.out.println("Invalid color: cannot move the wanted color");
                return;
            }
        }

        pawn.toggleSelected();
        stageModel.setState(TrifleStageModel.SELECT_DEST_STATE);

        board.setValidCells(pawn.getCoords(), model.getIdPlayer(), pawn.getSumoLevel());
    }
}
