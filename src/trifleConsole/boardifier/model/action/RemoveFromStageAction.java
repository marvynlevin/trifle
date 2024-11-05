package trifleConsole.boardifier.model.action;

import trifleConsole.boardifier.model.GameElement;
import trifleConsole.boardifier.model.Model;

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
