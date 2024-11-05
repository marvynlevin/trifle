import minmax.BoardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trifleConsole.model.Pawn;
import trifleConsole.model.TrifleBoard;
import trifleConsole.model.TrifleStageModel;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestMinMax {

    private TrifleBoard board;
    private TrifleStageModel stageModel;
    private List<Pawn> bluePawns;
    private List<Pawn> cyanPawns;
    private BoardStatus boardStatus;

    @BeforeEach
    public void setUp() {
        // Initialize the pawns and stage model for testing
        bluePawns = Arrays.asList(
                new Pawn(1, Pawn.BLUE_PLAYER, stageModel, 0, 0),
                new Pawn(2, Pawn.BLUE_PLAYER, stageModel, 1, 0),
                new Pawn(3, Pawn.BLUE_PLAYER, stageModel, 2, 0)
        );

        cyanPawns = Arrays.asList(
                new Pawn(4, Pawn.CYAN_PLAYER, stageModel, 0, 7),
                new Pawn(5, Pawn.CYAN_PLAYER, stageModel, 1, 7),
                new Pawn(6, Pawn.CYAN_PLAYER, stageModel, 2, 7)
        );

        board = Mockito.mock(TrifleBoard.class); // Assuming a default constructor is available
        stageModel = Mockito.mock(TrifleStageModel.class);
        boardStatus = new BoardStatus(bluePawns, cyanPawns, board, stageModel);
    }

    @Test
    public void testInitialization() {
        // Verify initial matrix setup
        int[][] matrix = boardStatus.getMatrix();

        for (int[] r: matrix)
            System.out.println(Arrays.toString(r));

        assertEquals(1, matrix[0][0]);
        assertEquals(1, matrix[0][1]);
        assertEquals(1, matrix[0][2]);
        assertEquals(2, matrix[7][0]);
        assertEquals(2, matrix[7][1]);
        assertEquals(2, matrix[7][2]);

        // Verify initial moves
        assertNull(boardStatus.getLastBlueMove());
        assertNull(boardStatus.getLastCyanMove());
    }

    @Test
    public void testSetLastMove() {
        Point move = new Point(1, 1);
        boardStatus.setLastMove(0, move);
        assertEquals(move, boardStatus.getLastBlueMove());

        move = new Point(6, 6);
        boardStatus.setLastMove(1, move);
        assertEquals(move, boardStatus.getLastCyanMove());
    }

    @Test
    public void testMovePawn() {
        Point newMove = new Point(1, 1);
        boardStatus.movePawn(0, 1, newMove);
        assertEquals(newMove, boardStatus.getPawns(0).get(0).getCoords());

        newMove = new Point(6, 6);
        boardStatus.movePawn(1, 4, newMove);
        assertEquals(newMove, boardStatus.getPawns(1).get(0).getCoords());
    }

    @Test
    public void testIsPointOccupied() {
        assertTrue(boardStatus.isPointOccupied(new Point(0, 0)));
        assertFalse(boardStatus.isPointOccupied(new Point(3, 3)));
    }

    @Test
    public void testIsStartOfGame() {
        assert(boardStatus.isStartOfGame());

        boardStatus.movePawn(0, 1, new Point(1, 1));
        assertFalse(boardStatus.isStartOfGame());
    }

    @Test
    public void testIsWin() {
        assertFalse(boardStatus.isWin());

        boardStatus.movePawn(0, 1, new Point(7, 0));
        assertTrue(boardStatus.isWin());
    }

    @Test
    public void testGetPossibleMoves() {
        List<Point> moves = boardStatus.getPossibleMoves(0, new Point(0, 0));
        assertFalse(moves.isEmpty());

        moves = boardStatus.getPossibleMoves(1, new Point(7, 0));
        assertFalse(moves.isEmpty());
    }
}
