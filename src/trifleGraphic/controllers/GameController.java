package trifleGraphic.controllers;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import rules.GameMode;
import rules.PlayerMode;
import trifleGraphic.boardifierGraphic.control.ActionFactory;
import trifleGraphic.boardifierGraphic.control.ActionPlayer;
import trifleGraphic.boardifierGraphic.control.Controller;
import trifleGraphic.boardifierGraphic.model.GameException;
import trifleGraphic.boardifierGraphic.model.Model;
import trifleGraphic.boardifierGraphic.model.Player;
import trifleGraphic.boardifierGraphic.model.action.ActionList;
import trifleGraphic.boardifierGraphic.view.View;
import trifleGraphic.model.Pawn;
import trifleGraphic.model.TrifleBoard;
import trifleGraphic.model.TrifleStageModel;
import trifleGraphic.view.TrifleRootPane;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static trifleGraphic.controllers.GameMouseController.ANIMATION_FACTOR;
import static trifleGraphic.controllers.GameMouseController.ANIMATION_TYPE;

public class GameController extends Controller {

    private int bluePlayerPoints = 0;
    private int cyanPlayerPoints = 0;

    // Store each computer if needed
    public BotDecider firstComputer;
    public BotDecider secondComputer;

    private int roundsCounter;

    public BotDecider getComputer(int id){
        if (id == 0) return firstComputer;
        else return secondComputer;
    }

    public GameController(Model model, View view) {
        super(model, view);

        setControlAction(new GameActionController(model, view, this));
        setControlMouse(new GameMouseController(model, view, this));
    }

    public static final int[] PLAYER_IDS =  new int[]{0, 1};

    public void registerMove(TrifleStageModel gameStage, Point moveCoordinates, String move, Pawn pawn){
        if (model.getIdPlayer() == 0) {
            gameStage.setLastBluePlayerMove(moveCoordinates);
        } else {
            gameStage.setLastCyanPlayerMove(moveCoordinates);
        }
    }

    /**
     * Detect a winning situation for any player.
     * <br>This method doesn't manage a draw situation
     * @return If this is a winning situation
     */
    public boolean detectWin(){
        TrifleStageModel gameStage = (TrifleStageModel) model.getGameStage();

        System.out.println("bluePlayerBlocked: " + gameStage.isBluePlayerBlocked());
        System.out.println("cyanPlayerBlocked: " + gameStage.isCyanPlayerBlocked());
        if (gameStage.isPlayerBlocked(0) && gameStage.isPlayerBlocked(1)) {
            model.setIdWinner((model.getIdWinner() + 1) % 2);
            return true;
        }

        for (int playerID: PLAYER_IDS){
            List<Pawn> pawns = gameStage.getPlayerPawns(playerID);
            boolean isWinning = pawns.stream()
                    .anyMatch(pawn -> pawn.getCoords().y == getBaseRowForPlayer((playerID + 1) % 2));

            if (isWinning){
                model.setIdWinner(playerID);
                this.endGame();
                return true;
            }
        }
        return false;
    }

    public GameMode gameMode     = GameMode.Fast;
    public PlayerMode playerMode = PlayerMode.HumanVsHuman;

    public void configureFromRootPane(){
        this.model.getPlayers().clear();

        this.gameMode = TrifleRootPane.selectedGameMode;
        this.playerMode = TrifleRootPane.selectedPlayerMode;

        if (TrifleRootPane.isFirstPlayerBot) {
            this.model.addComputerPlayer(TrifleRootPane.firstPlayerName);

            firstComputer = TrifleRootPane.firstBotStrategy.initComputerGraphic(model, this);
        } else this.model.addHumanPlayer(TrifleRootPane.firstPlayerName);

        if (TrifleRootPane.isSecondPlayerBot) {
            this.model.addComputerPlayer(TrifleRootPane.secondPlayerName);
            secondComputer = TrifleRootPane.secondBotStrategy.initComputerGraphic(model, this);
        }
        else this.model.addHumanPlayer(TrifleRootPane.secondPlayerName);

    }

    private int newSumoLevelForPawn(Pawn pawn){
        pawn.increaseSumoLevel(this);
        System.out.println("Pawn with received a sumo: " + pawn);
        return pawn.getSumoLevel() <= 1 ? (pawn.getSumoLevel() - 1) * 2 : 0;
    }

    @Override
    public void endGame() {
        System.out.println();

        TrifleStageModel gameStage = (TrifleStageModel) model.getGameStage();
        System.out.println("selected gamemode: " + this.gameMode.toString());
        int requiredPoints = this.gameMode.requiredPoints();

        int givenPoints = 1;
        if (model.getIdWinner() == 0 || model.getIdWinner() == 1) {
            List<Pawn> pawns = gameStage.getPlayerPawns(model.getIdWinner());
            int losingPlayerId = model.getIdWinner() == 0 ? 1 : 0;

            for (Pawn pawn: pawns) {
                if (pawn.getCoords().y == getBaseRowForPlayer(losingPlayerId)) {
                    givenPoints += newSumoLevelForPawn(pawn);
                    break;
                }
            }
        }

        switch (model.getIdWinner()){
            case 0: bluePlayerPoints += givenPoints; break;
            case 1: cyanPlayerPoints += givenPoints; break;
        }

        gameStage.updatePlayerPoints(bluePlayerPoints, cyanPlayerPoints);
        roundsCounter++;
        gameStage.getRoundCounter().setText("Round " + (roundsCounter + 1));

        int winningPlayerPoints;
        if (model.getIdWinner() == 0) winningPlayerPoints = bluePlayerPoints;
        else winningPlayerPoints = cyanPlayerPoints;

        // détect une victoire
        if (winningPlayerPoints >= requiredPoints) {
            partyEnd();
            return;
        }

        int rightOrLeft = 0;
        if (model.getIdWinner() == 0 || model.getIdWinner() == 1)
            rightOrLeft = this.askRightOrLeft();

        System.out.println("rightOrLeft: " + rightOrLeft);

        ActionList actionList = new ActionList();

        // reset the game
        for (int i = 0; i <= 7; i++) {
            Pawn bluePawn = gameStage.getPlayerPawns(0).get(i);
            bluePawn.setCoords(new Point((rightOrLeft == 1 ? 7 - i : i), 0));

            ActionList actionList2 = ActionFactory.generatePutInContainer(
                    this,
                    model,
                    bluePawn,
                    TrifleBoard.BOARD_ID,
                    0,
                    rightOrLeft == 1 ? 7 - i : i,
                    ANIMATION_TYPE,
                    ANIMATION_FACTOR
            );
            actionList.addAll(actionList2);


            Pawn cyanPawn = gameStage.getPlayerPawns(1).get(i);
            cyanPawn.setCoords(new Point((rightOrLeft == 1 ? 7 - i : i), 7));

            ActionList actionList3 = ActionFactory.generatePutInContainer(
                    this,
                    model,
                    cyanPawn,
                    TrifleBoard.BOARD_ID,
                    7,
                    rightOrLeft == 1 ? 7 - i : i,
                    ANIMATION_TYPE,
                    ANIMATION_FACTOR
            );
            actionList.addAll(actionList3);

        }

        gameStage.setLastBluePlayerMove(null);
        gameStage.setLastCyanPlayerMove(null);

        gameStage.setBluePlayerBlocked(false);
        gameStage.setCyanPlayerBlocked(false);

        ActionPlayer play = new ActionPlayer(model, this, actionList);
        play.start();

        System.out.println("\n\n\nNew round!\n\n");
    }


    /**
     * Ask the winner if he wants to place every pawn from right to left or from left to right.
     * @return Zero for the left to right option, or 1 for the right to left option
     */
    private int askRightOrLeft(){
        Alert box = new Alert(Alert.AlertType.NONE);
        box.setTitle("Winner decision");

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        String winnerName = model.getPlayers().get(model.getIdWinner()).getName();
        Text text = new Text(winnerName + ", do you want to place your pawns from left to right or from right to left ?");
        vBox.getChildren().add(text);

        box.getDialogPane().setContent(vBox);

        ButtonType leftToRight = new ButtonType("Left to right",    ButtonBar.ButtonData.OTHER);
        ButtonType rightToLeft = new ButtonType("Right to left",    ButtonBar.ButtonData.OTHER);
        ButtonType centerQuit  = new ButtonType("Quit the game",    ButtonBar.ButtonData.FINISH);

        box.getButtonTypes().clear();
        box.getButtonTypes().addAll(leftToRight, rightToLeft, centerQuit);

        Optional<ButtonType> result = box.showAndWait();

        if (result.isPresent() && result.get() == centerQuit) {
            System.exit(0);
        }

        return result.filter(buttonType -> buttonType != leftToRight)
                .map(buttonType -> 1)
                .orElse(0);
    }

    /**
     * At the end of the game, it asks if you want to quit,
     * configure the game or start a new game with the same settings
     */
    public void partyEnd(){
        Alert endGameBox = this.createEndGameBox();
        model.setCaptureEvents(false);
        Optional<ButtonType> res = endGameBox.showAndWait();
        model.setCaptureEvents(true);
        System.out.println("User response: " + res);

        if (res.isEmpty()){
            System.out.println("Unexpected result: No button has been clicked yet there wasn't any way to close the window?");
            System.exit(1);
        } else {
            switch (res.get().getText().toLowerCase()){
                case "quit": {
                    System.exit(0);
                    break;
                }
                case "configure game": {
                    this.stopGame();
                    view.resetView();
                    break;
                }
                case "new game": {
                    try {
                        this.startGame();
                    } catch (GameException e){
                        System.out.println("\u001b[31m" + e.getMessage());
                        e.printStackTrace();
                        System.out.print("\u001b[0m");
                        System.exit(1);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Create the alert box shown at the end of a game.
     * @return The alert box initiated
     */
    public Alert createEndGameBox(){
        String message = switch(model.getIdWinner()) {
            case 0 -> "Winner: " + model.getPlayers().get(0).getName();
            case 1 -> "Winner: " + model.getPlayers().get(1).getName();
            default -> "Draw";
        };

        Alert box = new Alert(Alert.AlertType.NONE);
        box.initStyle(StageStyle.UNDECORATED);

        box.initOwner(view.getStage());
        box.setHeaderText(message);

        ButtonType quitButton = new ButtonType("Quit");
        ButtonType configureGameButton = new ButtonType("Configure game");
        ButtonType newGameButton = new ButtonType("New Game");

        box.getButtonTypes().clear();
        box.getButtonTypes().addAll(quitButton, configureGameButton, newGameButton);

        return box;
    }

    /**
     * Get the base row ID of the given player.
     * <br>The base row is the row at which each player started.
     * @param playerID The wanted player
     * @return The row ID
     */
    public int getBaseRowForPlayer(int playerID){
        return playerID == 0 ? 0 : 7;
    }

    public static final String MOVE1 = new File("src/trifleGraphic/sounds/move1.mp3").toURI().toString();
    public static final String MOVE2 = new File("src/trifleGraphic/sounds/move2.mp3").toURI().toString();

    public static int move_sound_cursor = 0;

    @Override
    public void startGame() throws GameException {
        super.startGame();

        if (this.playerMode == PlayerMode.ComputerVsComputer){
            this.endOfTurn();
        }
    }

    @Override
    public void endOfTurn(){
        if (detectWin()) {
            this.endGame();
            return;
        }

        Media sound = new Media(move_sound_cursor == 0 ? MOVE1 : MOVE2);
        move_sound_cursor = move_sound_cursor == 0 ? 1 : 0;

        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setOnEndOfMedia(mediaPlayer::play);
        mediaPlayer.play();

        System.out.println("End of turn");
        System.out.println("PlayerID: " + model.getIdPlayer());
        model.setNextPlayer();
        System.out.println("PlayerID: " + model.getIdPlayer());

        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();
        stageModel.setState(TrifleStageModel.SELECT_PAWN_STATE);

        Player p = model.getCurrentPlayer();
        String text = p.getName() + " is playing.";

        Point lastOpponentMove = stageModel.getLastPlayerMove((model.getIdPlayer() + 1) % 2);
        if (lastOpponentMove == null) {
            text += " You start, so you can choose which pawn your moving.";
        }
        else {
            int colorIndex = TrifleBoard.BOARD[lastOpponentMove.y][lastOpponentMove.x];
            // Tell which color must be moved

            text += "\nYou must play the ";

            text += switch (colorIndex) {
                case 0 -> "Orange";
                case 1 -> "Blue";
                case 2 -> "Purple";
                case 3 -> "Pink";
                case 4 -> "Yellow";
                case 5 -> "Red";
                case 6 -> "Green";
                case 7 -> "Brown";
                default -> "Unknown";
            };

            text += " at ";
            text += normalizeCoordinate(
                    stageModel.getPlayerPawn(model.getIdPlayer(), colorIndex).getCoords(),
                    false
            );
        }

        stageModel.getPlayerName().setText(text);


        // check whether the next player can play?
        TrifleBoard board = (TrifleBoard) stageModel.getBoard();

        // lastOpponentMove
        if (lastOpponentMove != null) {
            int colorIndex = TrifleBoard.BOARD[lastOpponentMove.y][lastOpponentMove.x];
            Pawn pawn = stageModel.getPlayerPawn(model.getIdPlayer(), colorIndex);
            if (!board.canPawnMove(pawn, model.getIdPlayer())) {
                System.out.println("Player " + model.getIdPlayer() + " cannot move, therefor he'll skip his turn");
                stageModel.setPlayerBlocked(model.getIdPlayer(), true);

                if (model.getIdPlayer() == 0) stageModel.setLastBluePlayerMove(pawn.getCoords());
                else stageModel.setLastCyanPlayerMove(pawn.getCoords());

                this.endOfTurn();
            }

        }

        if ((model.getIdPlayer() == 0 && firstComputer != null) || (model.getIdPlayer() == 1 && secondComputer != null)) {
            playBot();
        }
    }

    public void playBot(){
        BotDecider bot = getComputer(model.getIdPlayer());

        ActionList actionList = bot.decide();

        actionList.setDoEndOfTurn(true);
        ActionPlayer play = new ActionPlayer(model, this, actionList);

        play.start();
    }

    /**
     * Normalize a coordinate from a `Point` to a String like `G7`
     * @param coordinates The coordinates to normalize
     * @param colored If the output string should have colors
     * @return The normalized coordinate
     */
    public static String normalizeCoordinate(Point coordinates, boolean colored) {
        String sb = "";

        System.out.println(coordinates);

        if (colored) {
            int colorIndex = trifleConsole.model.TrifleBoard.BOARD[coordinates.y][coordinates.x];
            sb += Pawn.COLORS[colorIndex];
        }

        sb += ((char) (coordinates.x + 65)) + "";
        sb += (coordinates.y + 1);

        return sb;
    }

}
