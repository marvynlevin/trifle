package trifleGraphic.boardifierGraphic.model.action;

import trifleGraphic.boardifierGraphic.model.GameElement;
import trifleGraphic.boardifierGraphic.model.Model;

public class RemoveFromStageAction extends GameAction {

    public RemoveFromStageAction(Model model, GameElement element) {
        super(model, element, "none");
    }

    public void execute() {
        element.removeFromStage();
        onEndCallback.execute();
    }

    public void createAnimation() {
    }
}
