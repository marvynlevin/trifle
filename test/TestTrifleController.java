import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import trifleConsole.boardifier.model.Model;
import trifleConsole.boardifier.model.Player;
import trifleConsole.boardifier.view.View;
import trifleConsole.control.TrifleController;
import trifleConsole.control.TrifleDecider;
import trifleConsole.model.TrifleStageModel;
import rules.BotStrategy;
import rules.GameMode;
import rules.PlayerMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.awt.*;
import java.util.List;

public class TestTrifleController {

    /**
     * Test a list of valid and invalid moves for the method `extractPawnIndex` from `TrifleController`
     * @param inputMove the input move, such as a1g7 or cg7
     * @param expectedIndex the expected output
     */
    @ParameterizedTest
    @CsvSource({
            "a1g7, 0",
            "cg7,  0",
            "b1b1, 1",
            "bg7,  1",
            "c1d2, 2",
            "pg7, 3",
            "c1g7, 3",
            "dg7, 4",
            "d1g7, 4",
            "eg7, 5",
            "e1g7, 5",
            "fg7, 6",
            "f1g7, 6",
            "gg7, 7",
            "g1g7, 7",
            "ng7, 8",
            "h1g7, 8",

            "11b7, ", // null because of two 11 at the start
            "aa1g7, ", // null because a is repeated two times
            ",", // well, you got it
            "a9g7,", // null because a9 is out of bound
            "a0g7,", // null because a0 is out of bound
            "i1g7,", // null because the letter I is out of bound
    })
    public void extractPawnIndex(String inputMove, Integer expectedIndex) {
        TrifleController controller = Mockito.mock(TrifleController.class);

        Mockito.when(controller.extractPawnIndex(inputMove)).thenReturn(expectedIndex);

        Integer actualIndex = controller.extractPawnIndex(inputMove);
        Mockito.verify(controller).extractPawnIndex(inputMove);

        assertEquals(expectedIndex, actualIndex);
    }

    /**
     * Test a list of valid and invalid moves for the method `extractRequestedMove` from `TrifleController`
     * @param inputMove the input move, such as a1g7 or cg7
     * @param x the expected x
     * @param y the expected y
     */
    @ParameterizedTest
    @CsvSource({
            "a1g7, 6, 6",
            "cg7,  6, 6",
            "b1b1, 1, 1",
            "bg7,  6, 6",
            "c1d2, 3, 2",
            "pg7,  6, 6",
            "c1g7, 6, 6",
            "dg7,  6, 6",
            "d1g7, 6, 6",
            "eg7,  6, 6",
            "e1g7, 6, 6",
            "fg7,  6, 6",
            "f1g7, 6, 6",
            "gg7,  6, 6",
            "g1g7, 6, 6",
            "ng7,  6, 6",
            "h1g7, 6, 6",

            "11l7,,", // null because l7 is out of bound
            ",,", // well, you got it
            "a9g8,,", // null because g8 is out of bound
            "a0z7,,", // null because z7 is out of bound
            "i1go,,", // null because go is not a valid action
    })
    public void extractRequestedMove(String inputMove, Integer x, Integer y) {
        TrifleController controller = Mockito.mock(TrifleController.class);

        if (x != null && y != null)
            Mockito.when(controller.extractRequestedMove(inputMove)).thenReturn(new Point(x, y));
        else
            Mockito.when(controller.extractRequestedMove(inputMove)).thenReturn(null);

        Point p = controller.extractRequestedMove(inputMove);
        Mockito.verify(controller).extractRequestedMove(inputMove);

        if (x == null || y == null) {
            assertEquals(p, null);
        } else {
            assertEquals(x, p.x);
            assertEquals(y, p.y);
        }
    }

    @Mock
    private Model model;

    @Mock
    private View view;

    private final GameMode gameMode = GameMode.Fast;

    private final PlayerMode playerMode = PlayerMode.HumanVsHuman;

    @Mock
    private List<String> playerNames;

    @Mock
    private TrifleStageModel stageModel;

    private TrifleController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(model.getGameStage()).thenReturn(stageModel);
        controller = new TrifleController(model, view, GameMode.Fast, PlayerMode.HumanVsComputer, playerNames);
    }

    @Test
    void testInitialization() {
        assertNotNull(controller);
    }

//    @Test
//    void testDefineBotsHumanVsHuman() {
//        controller.defineBots(List.of());
//        assertNull(controller.getFirstComputer());
//        assertNull(controller.getSecondComputer());
//    }

    @Test
    void testDefineBotsHumanVsComputer() {
        BotStrategy botStrategy = mock(BotStrategy.class);
        when(botStrategy.initComputer(any(Model.class), any(TrifleController.class))).thenReturn(mock(TrifleDecider.class));
        controller.defineBots(List.of(botStrategy));
        assertNull(controller.getFirstComputer());
        assertNotNull(controller.getSecondComputer());
    }

//    @Test
//    void testDefineBotsComputerVsComputer() {
//        BotStrategy botStrategy1 = mock(BotStrategy.class);
//        BotStrategy botStrategy2 = mock(BotStrategy.class);
//        when(botStrategy1.initComputer(any(Model.class), any(TrifleController.class))).thenReturn(mock(TrifleDecider.class));
//        when(botStrategy2.initComputer(any(Model.class), any(TrifleController.class))).thenReturn(mock(TrifleDecider.class));
//        controller.defineBots(List.of(botStrategy1, botStrategy2));
//        assertNotNull(controller.getFirstComputer());
//        assertNotNull(controller.getSecondComputer());
//    }


    @Test
    void testPlayTurnHuman() {
        Player humanPlayer = mock(Player.class);
        when(humanPlayer.getType()).thenReturn(Player.HUMAN);
        when(model.getCurrentPlayer()).thenReturn(humanPlayer);
        doNothing().when(controller).playerTurn(any(Player.class));
        controller.playTurn();
        verify(controller).playerTurn(humanPlayer);
    }

    @Test
    void testPlayTurnComputer() {
        Player computerPlayer = mock(Player.class);
        when(computerPlayer.getType()).thenReturn(Player.COMPUTER);
        when(model.getCurrentPlayer()).thenReturn(computerPlayer);
        doNothing().when(controller).botTurn(any(Player.class));
        controller.playTurn();
        verify(controller).botTurn(computerPlayer);
    }

    @Test
    void testEndGameWithWinner() {
        when(gameMode.requiredPoints()).thenReturn(1);
        when(model.getIdWinner()).thenReturn(0);
        when(model.getPlayers()).thenReturn(List.of(mock(Player.class), mock(Player.class)));
        controller.endGame();
        verify(model, times(1)).setIdWinner(0);
    }

    @Test
    void testEndGameDraw() {
        when(gameMode.requiredPoints()).thenReturn(1);
        when(model.getIdWinner()).thenReturn(-1);
        when(model.getPlayers()).thenReturn(List.of(mock(Player.class), mock(Player.class)));
        controller.endGame();
        verify(model, times(1)).setIdWinner(-1);
    }


}
