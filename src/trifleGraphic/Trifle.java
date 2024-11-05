package trifleGraphic;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import rules.GameMode;
import rules.PlayerMode;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.control.Logger;
import trifleGraphic.boardifierGraphic.control.StageFactory;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.controllers.GameController;
import trifleGraphic.view.TrifleRootPane;
import trifleGraphic.view.TrifleView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Trifle extends Application {
    private static GameMode     gameMode;
    public  static List<String> playerNames  = new ArrayList<>();

    public static final String FIRST_STAGE_NAME = "trifleGraphic";

    public static Controller controller;

    public static void main(String[] args) {
        Logger.setLevel(Logger.LOGGER_INFO);
        Logger.setVerbosity(Logger.VERBOSE_BASIC);

        Trifle.gameMode = GameMode.defaultValue();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();

        // Add the players to the model
        playerNames = getPlayerNames(gameMode);
        PlayerMode playerMode = getPlayerMode("");
        switch (playerMode) {
            case HumanVsHuman: {
                model.addHumanPlayer(playerNames.get(0));
                model.addHumanPlayer(playerNames.get(1));
                break;
            }
            case HumanVsComputer: {
                model.addHumanPlayer(playerNames.get(0));
                model.addComputerPlayer("Computer");
                break;
            }
            case ComputerVsComputer: {
                model.addComputerPlayer("Computer1");
                model.addComputerPlayer("Computer2");
                break;
            }
            default: {
                System.out.println("\u001b[31mA problem has occurred in the gameMode configuration.\u001b[0m");
                System.exit(1);
            }
        }

        // Initiate the required instances, such as the view, StageFactory and the controller
        StageFactory.registerModelAndView(FIRST_STAGE_NAME, "trifleGraphic.model.TrifleStageModel", "trifleGraphic.view.TrifleStageView");

        TrifleRootPane rootPane = new TrifleRootPane();

        TrifleView view = new TrifleView(model, primaryStage, rootPane);

        controller = new GameController(model, view);

        controller.setFirstStageName(FIRST_STAGE_NAME);
        primaryStage.setTitle("Trifle - Kamisado");

        model.setCaptureEvents(false);

        Media sound = new Media(BACKGROUND_SOUND_PATH);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setOnEndOfMedia(mediaPlayer::play);
        mediaPlayer.setVolume(0.25);

        primaryStage.getIcons().add(new Image("trifleGraphic/icon.png"));

        Application.setUserAgentStylesheet("trifleGraphic/themes/nord-light.css");
        mediaPlayer.play();

        primaryStage.show();
    }

    public static final String BACKGROUND_SOUND_PATH = new File("src/trifleGraphic/sounds/background.mp3").toURI().toString();

    /**
     * Get the player Mode
     * @param mode The player-mode given as argument, if any
     * @return The player mode (between zero and two, included)
     */
    private static PlayerMode getPlayerMode(String mode) {
        return switch (mode) {
            case "", "0" -> PlayerMode.HumanVsHuman;
            case "1" -> PlayerMode.HumanVsComputer;
            case "2" -> PlayerMode.ComputerVsComputer;
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }

    /**
     * Get the player (or bot) names
     * <br>
     * Bot names will be generated automatically if there are less than two names registered in the TUI
     * @param mode The player-mode given as argument, if any
     * @return The list of the names
     */
    private static List<String> getPlayerNames(GameMode mode) {
        return switch (mode.ordinal()) {
            case 1 -> List.of("Player", "Computer");
            case 2 -> List.of("Computer1", "Computer2");
            default -> List.of("Player1", "Player2");
        };
    }
}
