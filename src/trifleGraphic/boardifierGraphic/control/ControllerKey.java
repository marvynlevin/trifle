package trifleGraphic.boardifierGraphic.control;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.view.View;

public class ControllerKey implements EventHandler<KeyEvent> {
    protected Model model;
    protected View view;
    protected Controller control;

    public ControllerKey(Model model, View view, Controller control) {
        this.model = model;
        this.view = view;
        this.control = control;
        // Attach KeyEvent listening to a Node that has focus.
        view.getRootPane().setOnKeyPressed(this);
        view.getRootPane().setOnKeyReleased(this);
    }

    // by default, do nothing. Must be overridden in subclasses
    public void handle(KeyEvent event) {}
}
