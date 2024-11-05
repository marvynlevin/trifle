package trifleConsole.boardifier.control;

import trifleConsole.boardifier.model.action.ActionList;
import trifleConsole.boardifier.model.Model;

public abstract class Decider {
    protected Model model;
    protected Controller control;

    public Decider(Model model, Controller control) {
        this.model = model;
        this.control = control;
    }

    public abstract ActionList decide();
}
