import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trifleConsole.model.Pawn;
import trifleConsole.model.TrifleBoard;
import trifleConsole.model.TrifleStageModel;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestTrifleBoard {

    private TrifleBoard board;
    private TrifleStageModel stageModel;

    @BeforeEach
    public void setUp() {
        stageModel = Mockito.mock(TrifleStageModel.class);
        board = new TrifleBoard(0, 0, stageModel);
    }

    @Test
    public void testInitialization() {
        assertEquals(8, board.getWidth());
        assertEquals(8, board.getHeight());
    }

    @Test
    public void testSetValidCells() {
        Point coordinates = new Point(3, 3);
        board.setValidCells(coordinates, 0);
        boolean[][] reachableCells = board.getReachableCells();
        assertTrue(reachableCells[3][4]);
        assertTrue(reachableCells[3][5]);
        assertTrue(reachableCells[3][6]);
        assertTrue(reachableCells[3][7]);
        assertTrue(reachableCells[4][4]);
        assertTrue(reachableCells[5][5]);
        assertTrue(reachableCells[6][6]);
        assertTrue(reachableCells[2][4]);
        assertTrue(reachableCells[1][5]);
        assertTrue(reachableCells[0][6]);
    }

    @Test
    public void testCanPawnMove() {
        Pawn pawn = new Pawn(0, 0, stageModel, 3, 3);
        assertTrue(board.canPawnMove(pawn, 0));
        pawn.setCoords(new Point(7, 7));
        assertFalse(board.canPawnMove(pawn, 0));
    }

    @Test
    public void testComputeValidCells() {
        Point coordinates = new Point(3, 3);
        List<Point> validCells = board.computeValidCells(coordinates, 0);

        assertTrue(validCells.contains(new Point(3, 4)));
        assertTrue(validCells.contains(new Point(3, 5)));
        assertTrue(validCells.contains(new Point(3, 6)));
        assertTrue(validCells.contains(new Point(3, 7)));
        assertTrue(validCells.contains(new Point(4, 4)));
        assertTrue(validCells.contains(new Point(5, 5)));
        assertTrue(validCells.contains(new Point(6, 6)));
        assertTrue(validCells.contains(new Point(2, 4)));
        assertTrue(validCells.contains(new Point(1, 5)));
        assertTrue(validCells.contains(new Point(0, 6)));
    }
}
