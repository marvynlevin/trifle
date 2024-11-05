import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import trifleGraphic.controllers.GameMouseController;
import trifleGraphic.model.Pawn;
import trifleGraphic.model.TrifleBoard;
import trifleGraphic.model.TrifleStageModel;
import trifleGraphic.view.TrifleBoardLook;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestGameMouseController {

    private GameMouseController gameMouseController;
    private Model mockModel;
    private View mockView;
    private Controller mockController;
    private MouseEvent mockMouseEvent;
    private TrifleStageModel mockStageModel;
    private TrifleBoard mockBoard;
    private Pawn mockPawn;
    private GameElement mockGameElement;
    private TrifleBoardLook mockBoardLook;

    @BeforeEach
    public void setUp() {
        mockModel = mock(Model.class);
        mockView = mock(View.class);
        mockController = mock(Controller.class);
        mockMouseEvent = mock(MouseEvent.class);
        mockStageModel = mock(TrifleStageModel.class);
        mockBoard = mock(TrifleBoard.class);
        mockPawn = mock(Pawn.class);
        mockGameElement = mock(GameElement.class);
        mockBoardLook = mock(TrifleBoardLook.class);

        gameMouseController = new GameMouseController(mockModel, mockView, mockController);

        when(mockModel.getGameStage()).thenReturn(mockStageModel);
        when(mockStageModel.getBoard()).thenReturn(mockBoard);
    }

    @Test
    public void testHandle_PawnSelectionState() {
        when(mockMouseEvent.getSceneX()).thenReturn(100.0);
        when(mockMouseEvent.getSceneY()).thenReturn(100.0);
        when(mockStageModel.getState()).thenReturn(TrifleStageModel.SELECT_PAWN_STATE);

        gameMouseController.handle(mockMouseEvent);

        verify(mockStageModel).getState();
    }

    @Test
    public void testHandle_PawnDestinationState() {
        when(mockMouseEvent.getSceneX()).thenReturn(100.0);
        when(mockMouseEvent.getSceneY()).thenReturn(100.0);
        when(mockStageModel.getState()).thenReturn(TrifleStageModel.SELECT_DEST_STATE);
        when(mockController.elementsAt(any(Coord2D.class))).thenReturn(Arrays.asList(mockGameElement));
        when(mockGameElement.equals(mockBoard)).thenReturn(true);

        gameMouseController.handle(mockMouseEvent);

        verify(mockStageModel).getState();
    }

    @Test
    public void testProcessPawnDestination_CanReachCell() {
        Coord2D clic = new Coord2D(100, 100);
        List<GameElement> elementList = Arrays.asList(mockGameElement);
        when(mockGameElement.equals(mockBoard)).thenReturn(true);
        when(mockBoardLook.getCellFromSceneLocation(any(Coord2D.class))).thenReturn(new int[]{1, 1});
        when(mockBoard.canReachCell(1, 1)).thenReturn(true);
        when(mockModel.getSelected()).thenReturn(Arrays.asList(mockPawn));

        gameMouseController.processPawnDestination(clic, mockStageModel, elementList);

        verify(mockBoard).resetReachableCells(false);
        verify(mockStageModel).setState(TrifleStageModel.SELECT_PAWN_STATE);
    }

    @Test
    public void testGetPawnFrom() {
        Coord2D clic = new Coord2D(100, 100);
        when(mockBoardLook.getCellFromSceneLocation(any(Coord2D.class))).thenReturn(new int[]{1, 1});
        when(mockStageModel.getPlayerPawns(anyInt())).thenReturn(Arrays.asList(mockPawn));
        when(mockPawn.getCoords()).thenReturn(new Point(1, 1));

        Pawn pawn = gameMouseController.getPawnFrom(clic, mockStageModel);

        assertNotNull(pawn);
    }

    @Test
    public void testHandlePawnSelectionState_PawnSelected() {
        Coord2D clic = new Coord2D(100, 100);
        when(mockStageModel.getPlayerPawns(anyInt())).thenReturn(Arrays.asList(mockPawn));
        when(mockBoardLook.getCellFromSceneLocation(any(Coord2D.class))).thenReturn(new int[]{1, 1});
        when(mockPawn.getCoords()).thenReturn(new Point(1, 1));
        when(mockBoard.canPawnMove(mockPawn, anyInt())).thenReturn(true);

        gameMouseController.handlePawnSelectionState(clic, mockStageModel);

        verify(mockPawn).toggleSelected();
        verify(mockStageModel).setState(TrifleStageModel.SELECT_DEST_STATE);
        verify(mockBoard).setValidCells(mockPawn.getCoords(), mockModel.getIdPlayer(), 0);
    }
}
