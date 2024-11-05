package trifleGraphic.boardifierGraphic.model.animation;

import trifleGraphic.boardifierGraphic.model.Model;

public class WaitAnimation extends Animation {
    protected int nbFrames;

    public WaitAnimation(Model model, int nbFrames) {
        super(model, AnimationTypes.getType("wait/frames"));
        this.nbFrames = nbFrames;
    }

    public void computeSteps() {
        for(int i=0;i<nbFrames;i++) steps.add(NOPStep);
    }
}
