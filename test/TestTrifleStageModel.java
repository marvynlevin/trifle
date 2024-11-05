import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import rules.GameMode;
import rules.PlayerMode;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.TextElement;
import trifleGraphic.model.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestTrifleStageModel {

    private TrifleStageModel model;
    private TrifleBoard mockBoard;
    private TextElement mockTextElement;
    private OldMove mockOldMove;
    private Model mockModel;
    private Pawn mockPawn;
    private BackgroundCell mockBackgroundCell;

    @BeforeEach
    public void setUp() {
        mockModel = mock(Model.class);
        model = new TrifleStageModel("TestStage", mockModel);

        mockBoard = mock(TrifleBoard.class);
        mockTextElement = mock(TextElement.class);
        mockOldMove = mock(OldMove.class);
        mockPawn = mock(Pawn.class);
        mockBackgroundCell = mock(BackgroundCell.class);
    }

    @Test
    public void testConstructor() {
        assertNotNull(model.getBluePlayer());
        assertNotNull(model.getCyanPlayer());
        assertNotNull(model.getBackgroundCells());
        assertNotNull(model.getOldMovesList());
        assertNotNull(model.getMovesHistory());
        assertEquals(TrifleStageModel.SELECT_PAWN_STATE, model.getState());
    }

    @Test
    public void testSetAndGetBoard() {
        model.setBoard(mockBoard);
        assertEquals(mockBoard, model.getBoard());
    }

    @Test
    public void testSetAndGetState() {
        model.setState(TrifleStageModel.SELECT_DEST_STATE);
        assertEquals(TrifleStageModel.SELECT_DEST_STATE, model.getState());
    }

    @Test
    public void testAddAndRetrieveOldMove() {
        model.addOldMove(mockOldMove);
        List<OldMove> oldMovesList = model.getOldMovesList();
        assertEquals(1, oldMovesList.size());
        assertEquals(mockOldMove, oldMovesList.get(0));
    }

    @Test
    public void testUpdatePlayerPoints() {
        when(mockTextElement.getText()).thenReturn("Blue: 10   Cyan: 8");
        model.setPlayerPoints(mockTextElement);
        model.updatePlayerPoints(10, 8);
        verify(mockTextElement).setText("Blue: 10   Cyan: 8");
    }

    @Test
    public void testGetAndSetPlayerName() {
        model.setPlayerName(mockTextElement);
        assertEquals(mockTextElement, model.getPlayerName());
    }

    @Test
    public void testGetAndSetRoundCounter() {
        model.setRoundCounter(mockTextElement);
        assertEquals(mockTextElement, model.getRoundCounter());
    }

    @Test
    public void testGetAndSetPlayerPoints() {
        model.setPlayerPoints(mockTextElement);
        assertEquals(mockTextElement, model.getPlayerPoints());
    }


    @Test
    public void testGetAndSetGameMode() {
        GameMode gameMode = GameMode.defaultValue();
        model.setGameMode(gameMode);
        assertEquals(gameMode, model.getGameMode());
    }

    @Test
    public void testGetAndSetPlayerMode() {
        PlayerMode playerMode = PlayerMode.defaultValue();
        model.setPlayerMode(playerMode);
        assertEquals(playerMode, model.getPlayerMode());
    }

    @Test
    public void testUpdateHistory() {
        for (int i = 0; i < TrifleStageModel.MAX_HISTORY_SIZE; i++) {
            TextElement textElement = mock(TextElement.class);
            model.getMovesHistory().add(textElement);
        }
        model.addOldMove(mockOldMove);
        model.updateHistory();
        verify(model.getMovesHistory().get(TrifleStageModel.MAX_HISTORY_SIZE - 1)).setText(mockOldMove.toString());
    }

    @Test
    public void testBluePlayerBlocked() {
        model.setBluePlayerBlocked(true);
        assertTrue(model.isBluePlayerBlocked());
        model.setBluePlayerBlocked(false);
        assertFalse(model.isBluePlayerBlocked());
    }

    @Test
    public void testCyanPlayerBlocked() {
        model.setCyanPlayerBlocked(true);
        assertTrue(model.isCyanPlayerBlocked());
        model.setCyanPlayerBlocked(false);
        assertFalse(model.isCyanPlayerBlocked());
    }
}
