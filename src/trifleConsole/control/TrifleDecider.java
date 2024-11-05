package trifleConsole.control;

import trifleConsole.boardifier.control.Controller;
import trifleConsole.boardifier.control.Decider;
import trifleConsole.boardifier.model.Model;
import trifleConsole.boardifier.model.action.ActionList;

/**
 * A default implementation of a Decider, aka a computer player.
 */
public class TrifleDecider extends Decider {
    public TrifleDecider(Model model, Controller controller){
        super(model, controller);
    }

    /**
     * Decide which move can be done
     * @return The move calculated
     */
    @Override
    public ActionList decide() {
        ActionList actions = new ActionList();
        actions.setDoEndOfTurn(true);

//        TrifleController controller = (TrifleController) this.control;
//
//        controller.registerMove(
//                (TrifleStageModel) controller.getStageModel(),
//                new Point(0, 0),
//                "a1a2",
//                // Pawn
//        );

        System.out.println("Bot called");

        return actions;
    }
}