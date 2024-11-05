import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.model.GameException;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.Player;
import trifleGraphic.boardifierGraphic.view.View;
import trifleGraphic.controllers.GameController;
import trifleGraphic.model.Pawn;
import trifleGraphic.model.TrifleBoard;
import trifleGraphic.model.TrifleStageModel;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestGameController {

    private GameController gameController;
    private Model mockModel;
    private View mockView;
    private TrifleStageModel mockStageModel;
    private Pawn mockPawn;
    private Player mockPlayer;
    private TrifleBoard mockBoard;

    @BeforeEach
    public void setUp() {
        mockModel = mock(Model.class);
        mockView = mock(View.class);
        mockStageModel = mock(TrifleStageModel.class);
        mockPawn = mock(Pawn.class);
        mockPlayer = mock(Player.class);
        mockBoard = mock(TrifleBoard.class);

        when(mockModel.getGameStage()).thenReturn(mockStageModel);
        when(mockStageModel.getBoard()).thenReturn(mockBoard);
        when(mockModel.getCurrentPlayer()).thenReturn(mockPlayer);
        when(mockStageModel.getPlayerPawns(anyInt())).thenReturn(List.of(mockPawn));

        gameController = new GameController(mockModel, mockView);
    }

    @Test
    public void testRegisterMove() {
        Point moveCoordinates = new Point(1, 1);
        Pawn pawn = new Pawn(0, 0, mockStageModel, 0,0);

        TrifleStageModel gameStage = new TrifleStageModel("test", mockModel);

        gameController.registerMove(gameStage, moveCoordinates, "move", pawn);

        assertEquals(moveCoordinates, gameStage.getLastBluePlayerMove());
    }

    @Test
    public void testDetectWin_PlayerWins() {
        when(mockStageModel.getPlayerPawns(anyInt())).thenReturn(List.of(mockPawn));
        when(mockPawn.getCoords()).thenReturn(new Point(0, 7));

        assertTrue(gameController.detectWin());
        verify(mockModel).setIdWinner(anyInt());
    }

    @Test
    public void testDetectWin_NoPlayerWins() {
        when(mockStageModel.getPlayerPawns(anyInt())).thenReturn(List.of(mockPawn));
        when(mockPawn.getCoords()).thenReturn(new Point(0, 0));

        assertFalse(gameController.detectWin());
    }

    @Test
    public void testEndGame() {
        gameController.endGame();

        verify(mockModel, atLeastOnce()).getIdWinner();
    }

    @Test
    public void testEndOfTurn() {
        Point lastOpponentMove = new Point(0, 0);
        when(mockModel.getIdPlayer()).thenReturn(0);
        when(mockStageModel.getLastCyanPlayerMove()).thenReturn(lastOpponentMove);

        gameController.endOfTurn();

        verify(mockModel).setNextPlayer();
        verify(mockStageModel).setState(anyInt());
    }

    @Test
    public void testNormalizeCoordinate() {
        Point coordinates = new Point(0, 0);
        String result = GameController.normalizeCoordinate(coordinates, false);

        assertEquals("A1", result);
    }

    @Test
    public void testPartyEnd_Quit() {
        Alert mockAlert = mock(Alert.class);
        when(mockAlert.showAndWait()).thenReturn(Optional.of(ButtonType.CLOSE));
        doReturn(mockAlert).when(gameController).createEndGameBox();

        gameController.partyEnd();

        verify(mockModel).setCaptureEvents(false);
        verify(mockModel).setCaptureEvents(true);
    }

    @Test
    public void testGetBaseRowForPlayer() {
        assertEquals(0, gameController.getBaseRowForPlayer(0));
        assertEquals(7, gameController.getBaseRowForPlayer(1));
    }
}
