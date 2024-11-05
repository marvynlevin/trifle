package trifleGraphic.boardifierGraphic.model.action;

import trifleGraphic.boardifierGraphic.control.Logger;
import trifleGraphic.boardifierGraphic.model.ContainerElement;
import trifleGraphic.boardifierGraphic.model.Coord2D;
import trifleGraphic.boardifierGraphic.model.GameElement;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.animation.AnimationTypes;
import trifleGraphic.boardifierGraphic.model.animation.LinearMoveAnimation;
import trifleGraphic.boardifierGraphic.model.animation.MoveAnimation;


public class PutInContainerAction extends GameAction {

    protected ContainerElement containerDest;
    protected int rowDest;
    protected int colDest;
    protected double xDest;
    protected double yDest;
    protected double factor; // a speed in pixel/ms or the whole duration, see LinearMoveAnimation

    // construct an action with an animation
    public PutInContainerAction(Model model, GameElement element, String containerDest, int rowDest, int colDest, String animationName, double xDest, double yDest, double factor) {
        super(model, element, animationName);

        this.containerDest = model.getContainer(containerDest);
        this.rowDest = rowDest;
        this.colDest = colDest;
        this.xDest = xDest;
        this.yDest = yDest;
        this.factor = factor;

    }

    public PutInContainerAction(Model model, GameElement element, String containerDest, int rowDest, int colDest) {
        this(model, element, containerDest, rowDest, colDest, AnimationTypes.NONE, 0,0, 0);
    }

    public ContainerElement getContainerDest() {
        return containerDest;
    }

    public int getRowDest() {
        return rowDest;
    }

    public int getColDest() {
        return colDest;
    }

    public void execute() {
        // if the element is already within a container, do nothing
        if (element.getContainer() != null) return;
        // if dsestination container exists, puts element within
        if (containerDest != null) {
            element.waitForContainerOpEnd();
            containerDest.addElement(element, rowDest, colDest);
        }
        onEndCallback.execute();
    }

    protected void createAnimation() {
        animation = null;
        // only create an animation of type move/xxx
        if (animationName.startsWith("move")) {

            if (element.getContainer() != null) {
                Logger.trace("cannot create a move animation for an element that is already in a container");
                return;
            }
            Coord2D endLoc = new Coord2D(xDest, yDest);
            // create animation to visualize this movement
            if (animationType == AnimationTypes.MOVETELEPORT_VALUE) {
                animation = new MoveAnimation(model, element.getLocation(), endLoc);
            } else if ((animationType == AnimationTypes.MOVELINEARPROP_VALUE) ||
                    (animationType == AnimationTypes.MOVELINEARCST_VALUE)) {
                animation = new LinearMoveAnimation(model, element.getLocation(), endLoc, animationType, factor);
            }
        }
    }
}
