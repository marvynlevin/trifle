package trifleConsole.boardifier.model.action;

import trifleConsole.boardifier.model.GameElement;
import trifleConsole.boardifier.model.Model;
import trifleConsole.boardifier.model.animation.AnimationTypes;
import trifleConsole.boardifier.model.animation.WaitAnimation;


public class RemoveFromContainerAction extends GameAction {

    // construct an action with an animation
    public RemoveFromContainerAction(Model model, GameElement element) {
        super(model, element, AnimationTypes.WAIT_FRAMES);
        animateBeforeExecute = false;
    }

    public void execute() {
        // if the element is not within a container, do nothing
        if (element.getContainer() == null) return;

        element.getContainer().removeElement(element);
        onEndCallback.execute();
    }


    public void createAnimation() {
        animation = new WaitAnimation(model, 1);
    }
}
