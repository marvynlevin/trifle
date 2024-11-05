package trifleGraphic.boardifierGraphic.model.action;

import trifleGraphic.boardifierGraphic.model.GameElement;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.animation.AnimationTypes;


public class RemoveFromContainerAction extends GameAction {

    // construct an action with an animation
    public RemoveFromContainerAction(Model model, GameElement element) {
        super(model, element, AnimationTypes.WAIT_FRAMES);
        animateBeforeExecute = false;
    }

    public void execute() {
        // if the element is not within a container, do nothing
        if (element.getContainer() == null) return;
        element.waitForContainerOpEnd();
        element.getContainer().removeElement(element);
        onEndCallback.execute();
    }


    public void createAnimation() {
    }
}
