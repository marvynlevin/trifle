package trifleGraphic.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.control.ControllerAction;
import trifleGraphic.boardifierGraphic.model.GameException;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.view.View;
import trifleGraphic.view.TrifleView;

public class GameActionController  extends ControllerAction implements EventHandler<ActionEvent> {
    private final TrifleView view;

    public GameActionController(Model model, View view, Controller controller) {
        super(model, view, controller);

        this.view = (TrifleView) view;

        this.setMenuHandlers();
    }

    private void setMenuHandlers(){
        view.getMenuStart().setOnAction(this::onMenuStartAction);

        view.getMenuIntro().setOnAction(e -> {
            control.stopGame();
            view.resetView();
            model.setCaptureEvents(false);
        });

        view.getMenuQuit().setOnAction(e -> {
            // TODO Fermer tout les streams dans le controller!
            System.exit(0);
        });
    }

    public void onMenuStartAction(ActionEvent actionEvent) {
        try {
            model.setCaptureEvents(true);
            control.startGame();
        } catch(GameException err) {
            System.err.println(err.getMessage());
            System.exit(1);
        }
    }

    public void handle(ActionEvent event) {
        // TODO on peut y ajouter les différents events handlers pour la game

//        event.getTarget()
    }
}
