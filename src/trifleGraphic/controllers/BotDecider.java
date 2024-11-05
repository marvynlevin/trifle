package trifleGraphic.controllers;

import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.control.Decider;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.action.ActionList;

/**
 * A default implementation of a Decider, aka a computer player.
 */
public class BotDecider extends Decider {
    public BotDecider(Model model, Controller controller){
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