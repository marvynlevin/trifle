package trifleGraphic.view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.view.RootPane;
import trifleGraphic.boardifierGraphic.view.View;


public class TrifleView extends View {
    private MenuItem menuStart;
    private MenuItem menuIntro;
    private MenuItem menuQuit;

    public TrifleView(Model model, Stage stage, RootPane rootPane){
        super(model, stage, rootPane);
    }

    @Override
    protected void createMenuBar(){
        // TODO on peut mettre ici des actions supplémentaires
        menuBar = new MenuBar();
        Menu menu1 = new Menu("Game");
        menuStart = new MenuItem("New game");
        menuIntro = new MenuItem("Configuration");
        menuQuit = new MenuItem("Quit");
        menu1.getItems().add(menuStart);
        menu1.getItems().add(menuIntro);
        menu1.getItems().add(menuQuit);
        menuBar.getMenus().add(menu1);
    }

    public MenuItem getMenuStart() {
        return menuStart;
    }

    public MenuItem getMenuIntro() {
        return menuIntro;
    }

    public MenuItem getMenuQuit() {
        return menuQuit;
    }
}
