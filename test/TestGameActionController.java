import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.model.GameException;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.view.View;
import trifleGraphic.controllers.GameActionController;
import trifleGraphic.view.TrifleView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TestGameActionController {

    private GameActionController gameActionController;
    private Model mockModel;
    private View mockView;
    private Controller mockController;
    private TrifleView mockTrifleView;

    @BeforeEach
    public void setUp() {
        mockModel = mock(Model.class);
        mockView = mock(View.class);
        mockController = mock(Controller.class);
        mockTrifleView = mock(TrifleView.class);

        gameActionController = new GameActionController(mockModel, mockTrifleView, mockController);

        when(mockTrifleView.getMenuStart()).thenReturn(mock(MenuItem.class));
        when(mockTrifleView.getMenuIntro()).thenReturn(mock(MenuItem.class));
        when(mockTrifleView.getMenuQuit()).thenReturn(mock(MenuItem.class));
    }

    @Test
    public void testConstructor_SetsMenuHandlers() {
        verify(mockTrifleView.getMenuStart()).setOnAction(any(EventHandler.class));
        verify(mockTrifleView.getMenuIntro()).setOnAction(any(EventHandler.class));
        verify(mockTrifleView.getMenuQuit()).setOnAction(any(EventHandler.class));
    }

    @Test
    public void testOnMenuStartAction_StartsGame() throws GameException {
        ActionEvent mockActionEvent = mock(ActionEvent.class);

        gameActionController.onMenuStartAction(mockActionEvent);

        verify(mockModel).setCaptureEvents(true);
        verify(mockController).startGame();
    }

    @Test
    public void testOnMenuStartAction_ThrowsGameException() throws GameException {
        ActionEvent mockActionEvent = mock(ActionEvent.class);
        doThrow(new GameException("Test Exception")).when(mockController).startGame();

        GameException exception = assertThrows(GameException.class, () -> {
            gameActionController.onMenuStartAction(mockActionEvent);
        });

        assertEquals("Test Exception", exception.getMessage());
    }

    @Test
    public void testHandle_MenuIntroAction() {
        ActionEvent mockActionEvent = mock(ActionEvent.class);
        ArgumentCaptor<EventHandler<ActionEvent>> captor = ArgumentCaptor.forClass(EventHandler.class);

        verify(mockTrifleView.getMenuIntro()).setOnAction(captor.capture());
        captor.getValue().handle(mockActionEvent);

        verify(mockController).stopGame();
        verify(mockTrifleView).resetView();
        verify(mockModel).setCaptureEvents(false);
    }

    @Test
    public void testHandle_MenuQuitAction() {
        ActionEvent mockActionEvent = mock(ActionEvent.class);
        ArgumentCaptor<EventHandler<ActionEvent>> captor = ArgumentCaptor.forClass(EventHandler.class);

        verify(mockTrifleView.getMenuQuit()).setOnAction(captor.capture());
        assertThrows(SecurityException.class, () -> {
            captor.getValue().handle(mockActionEvent);
        });
    }

    @Test
    public void testHandle() {
        ActionEvent mockActionEvent = mock(ActionEvent.class);

        gameActionController.handle(mockActionEvent);

        // Assuming the handle method will be further implemented, you can add verifications here
    }
}
