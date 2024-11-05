import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.action.ActionList;
import trifleGraphic.controllers.BotDecider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestBotDecider {

    private BotDecider BotDecider;
    private Model mockModel;
    private Controller mockController;

    @BeforeEach
    public void setUp() {
        mockModel = mock(Model.class);
        mockController = mock(Controller.class);

        BotDecider = new BotDecider(mockModel, mockController);
    }

    @Test
    public void testDecide() {
        ActionList actions = BotDecider.decide();

        assertNotNull(actions);
        System.out.println("Bot called");
    }
}
