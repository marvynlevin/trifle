package trifleGraphic.view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import rules.BotStrategy;
import rules.GameMode;
import rules.PlayerMode;
import trifleGraphic.Trifle;
import trifleGraphic.boardifierGraphic.model.GameException;
import trifleGraphic.boardifierGraphic.view.RootPane;
import trifleGraphic.controllers.GameController;

import java.util.ArrayList;
import java.util.List;

public class TrifleRootPane extends RootPane {
    public TrifleRootPane() {
        super();
    }

    @Override
    public void createDefaultGroup(){
        Rectangle frame = new Rectangle(600, 400, Color.TRANSPARENT);
        Text text = new Text("Playing to the Kamisado");
        text.setFont(new Font(15));
        text.setFill(Color.BLACK);
        text.setX(20);
        text.setY(22);
        // put shapes in the group
        group.getChildren().clear();
        group.getChildren().addAll(frame, text);
        group.getChildren().add(createConfigPanel());
    }

    public static String firstPlayerName  = "joueur1";
    public static String secondPlayerName = "joueur2";

    public static BotStrategy firstBotStrategy = BotStrategy.DEFAULT;
    public static BotStrategy secondBotStrategy = BotStrategy.DEFAULT;

    public static boolean isFirstPlayerBot = false;
    public static boolean isSecondPlayerBot = false;

    public static GameMode selectedGameMode = GameMode.Fast;
    /**
     * This property is automatically determined by the bot checkbox
     */
    public static PlayerMode selectedPlayerMode = PlayerMode.HumanVsHuman;

    private Text playerModeText;
    private Text upToText;
    private Text sumoAndOshiText;

    private ComboBox<GameMode> gameModeComboBox;

    private CheckBox firstPlayerCheckBox;
    private CheckBox secondPlayerCheckBox;

    private TextField firstPlayerTextField;
    private TextField secondPlayerTextField;

    private ComboBox<BotStrategy> firstBotStrategyComboBox;
    private ComboBox<BotStrategy> secondBotStrategyComboBox;

    private void updatePlayerModeText(){
        System.out.println("Update the player mode selected");

        if (isFirstPlayerBot && isSecondPlayerBot){
            selectedPlayerMode = PlayerMode.ComputerVsComputer;
        } else if (!isFirstPlayerBot && !isSecondPlayerBot){
            selectedPlayerMode = PlayerMode.HumanVsHuman;
        } else {
            selectedPlayerMode = PlayerMode.HumanVsComputer;
        }

        playerModeText.setText(selectedPlayerMode.toString());
    }

    private void updateGamemodeInfos(){
        System.out.println("Update the gamemode information");
        // Define how much points are needed
        upToText.setText(selectedGameMode.requiredPoints() + " points");

        // Update the "Sumo & Oshi" text
        if (selectedGameMode != GameMode.Fast) sumoAndOshiText.setText("Yes");
        else sumoAndOshiText.setText("No");
    }

    private void resetConfig(){
        firstBotStrategyComboBox.valueProperty().setValue(null);
        secondBotStrategyComboBox.valueProperty().setValue(null);

        firstPlayerCheckBox.selectedProperty().set(false);
        firstPlayerTextField.textProperty().set("joueur1");

        secondPlayerCheckBox.selectedProperty().set(false);
        secondPlayerTextField.textProperty().set("joueur2");

        gameModeComboBox.valueProperty().set(GameMode.Fast);
        selectedPlayerMode = PlayerMode.HumanVsHuman;
    }

    private void startGameAction(ActionEvent event){
        List<String> missingRequirements = getMissingRequirementsInConfig();

        if (!missingRequirements.isEmpty()) {
            System.out.println("Missing things in the configuration:");

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.BASELINE_LEFT);
            vBox.setSpacing(10);
            vBox.setPadding(new Insets(10, 10, 10, 10));


            Label titleText = new Label("Some values are invalid or doesn't correspond to the wanted setup of a game.");
            titleText.setStyle("-fx-padding: 10px; -fx-font-weight: bold;");
            vBox.getChildren().add(titleText);

            // Create the list
            VBox missingRequirementsList  = new VBox();
            missingRequirementsList.setAlignment(Pos.BASELINE_LEFT);
            missingRequirementsList.setSpacing(10);

            for (String missingRequirement : missingRequirements) {
                Text text = new Text("- " + missingRequirement);

                missingRequirementsList.getChildren().add(text);
            }

            vBox.getChildren().add(missingRequirementsList);

            // add to the alert
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Bad configuration");
            alert.getDialogPane().setContent(vBox);


            ButtonType okBtn = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(okBtn);

            alert.showAndWait();

            return;
        }

        System.out.println("User can start the game");

        try {
            // reconfigure the controller
            GameController controller = (GameController) Trifle.controller;
            controller.configureFromRootPane();

            // start the game
            controller.startGame();
        } catch (GameException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private List<String> getMissingRequirementsInConfig(){
        List<String> missingRequirements = new ArrayList<>();

        if (firstPlayerName.isEmpty())
            missingRequirements.add("First player name cannot be empty");

        if (secondPlayerName.isEmpty())
            missingRequirements.add("Second player name cannot be empty");

        if (isFirstPlayerBot && firstBotStrategy == null)
            missingRequirements.add("First bot strategy must be specified");

        if (isSecondPlayerBot && secondBotStrategy == null)
            missingRequirements.add("First bot strategy must be specified");

        if (selectedGameMode == null)
            missingRequirements.add("Game mode must be specified");

        if (selectedPlayerMode == null)
            missingRequirements.add("Player mode must be specified");

        return missingRequirements;
    }

    private ScrollPane createConfigPanel(){
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPadding(new Insets(30, 10, 10, 10));
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox();
        content.getChildren().add(createPlayersConfigPanel());

        Separator separator = new Separator();
        separator.setStyle("-fx-height: 2px; -fx-color: #000; -fx-margin: 10px 0");
        content.getChildren().addAll(separator, createGameConfigurationPanel());

        // Add the two buttons at the end
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.BOTTOM_LEFT);
        buttons.setPadding(new Insets(30, 0, 10, 0));
        buttons.setSpacing(20);

        Button startGameButton = new Button("Start the game");
        startGameButton.setOnAction(this::startGameAction);

        Button resetConfigButton = new Button("Reset the configuration");
        resetConfigButton.setOnAction(_action -> {
            this.resetConfig();
        });

        buttons.getChildren().addAll(startGameButton, resetConfigButton);
        content.getChildren().addAll(buttons);

        scrollPane.setContent(content);
        return scrollPane;
    }

    private VBox createPlayersConfigPanel(){
        VBox playerNames = new VBox();
        playerNames.setSpacing(10);

        Text title = new Text("Players");
        title.setStyle("-fx-font-weight: bold");

        playerNames.getChildren().add(title);

        // create player cards
        HBox playerCards = new HBox();
        playerCards.setSpacing(50);
        playerCards.setPadding(new Insets(0, 25, 0, 25));
        playerCards.setAlignment(Pos.CENTER);
        playerCards.getChildren().addAll(createPlayerConfigCard(0), createPlayerConfigCard(1));

        playerNames.getChildren().add(playerCards);

        return playerNames;
    }

    private VBox createPlayerConfigCard(int playerID){
        VBox playerConfig = new VBox();
        playerConfig.setPrefWidth(255);
        playerConfig.setMaxWidth(255);
        playerConfig.setSpacing(5);

        // Create the body
        HBox nameBody = new HBox();
        nameBody.setAlignment(Pos.CENTER);
        nameBody.setSpacing(10);

        // for a "Human" player
        Label nameLabel = new Label("Name");
        nameLabel.setPrefWidth(150);

        TextField nameField = new TextField();
        nameField.setPrefWidth(210);

        nameField.setPromptText("The name");
        nameField.textProperty().set("joueur" + (playerID + 1));
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (playerID == 0) firstPlayerName = newValue;
            else secondPlayerName = newValue;
        });

        if (playerID == 0) firstPlayerTextField = nameField;
        else secondPlayerTextField = nameField;

        nameBody.getChildren().addAll(nameLabel, nameField);

        // for a "Bot"
        HBox strategyBody = new HBox();
        nameBody.setAlignment(Pos.CENTER);
        nameBody.setSpacing(10);

        Label strategyLabel = new Label("Strategy");
        strategyLabel.setPrefWidth(150);

        ComboBox<BotStrategy> botStrategy = new ComboBox<>();
        botStrategy.getItems().addAll(BotStrategy.values());
        botStrategy.setDisable(true);

        if (playerID == 0) firstBotStrategyComboBox = botStrategy;
        else secondBotStrategyComboBox = botStrategy;

        strategyBody.getChildren().addAll(strategyLabel, botStrategy);

        // Create player text & checkbox
        HBox topBar = new HBox();
        topBar.setSpacing(30);

        Text text = new Text("Player no." + (playerID + 1));
        text.setWrappingWidth(100);
        topBar.getChildren().add(text);

        CheckBox bot = new CheckBox("Bot");
        bot.setSelected(false);
        bot.setId("bot_config_player");
        bot.selectedProperty().addListener((ov, old_val, new_val) -> {
            // Define whether the first or second player is a bot
            if (playerID == 0) isFirstPlayerBot = new_val;
            else isSecondPlayerBot = new_val;

            // Change the Name of the player/bot if this hasn't been changed
            if (new_val){
                text.setText("Bot no." + (playerID + 1));
                botStrategy.setDisable(false);

                if (nameField.textProperty().get().equals("joueur" + (playerID + 1))) {
                    nameField.textProperty().set("bot" + (playerID + 1));
                }
            }
            else {
                text.setText("Player no." + (playerID + 1));
                botStrategy.setDisable(true);

                if (nameField.textProperty().get().equals("bot" + (playerID + 1))) {
                    nameField.textProperty().set("joueur" + (playerID + 1));
                }
            }

            this.updatePlayerModeText();
        });

        if (playerID == 0) firstPlayerCheckBox = bot;
        else secondPlayerCheckBox = bot;

        topBar.getChildren().add(bot);

        playerConfig.getChildren().add(topBar);
        playerConfig.getChildren().addAll(nameBody, strategyBody);

        return playerConfig;
    }

    private VBox createGameConfigurationPanel(){
        VBox gameConfiguration = new VBox();
        gameConfiguration.setSpacing(20);

        Text title = new Text("Game Configuration");
        title.setStyle("-fx-font-weight: bold");
        gameConfiguration.getChildren().add(title);

        // Create the gamemode box

        // ComboBox
        HBox gamemodeGroup = new HBox();
        gamemodeGroup.setSpacing(10);

        HBox gamemode = new HBox();
        gamemode.setPrefWidth(250);
        gamemode.setMaxWidth(250);
        gamemode.setSpacing(20);
        gamemode.setAlignment(Pos.BASELINE_CENTER);

        Label gameModeLabel = new Label("Game mode");
        gameModeLabel.setStyle("-fx-font-weight: bold");

        this.gameModeComboBox = new ComboBox<>();
        gameModeComboBox.getItems().addAll(GameMode.values());
        gameModeComboBox.valueProperty().set(GameMode.Fast);

        gameModeComboBox.valueProperty().addListener((ov, old_val, new_val) -> {
            TrifleRootPane.selectedGameMode = new_val;
            updateGamemodeInfos();
        });

        gamemode.getChildren().addAll(gameModeLabel, gameModeComboBox);
        gamemodeGroup.getChildren().add(gamemode);

        // Gamemode infos
        VBox gamemodeInfos = new VBox();
        gamemodeInfos.setSpacing(10);
        gamemodeInfos.setPrefWidth(250);

        // Up to
        HBox upToGroup  = new HBox();

        Text upTo = new Text("Up to:");
        upTo.setStyle("-fx-font-weight: bold");
        upTo.setWrappingWidth(200);

        this.upToText = new Text("");
        upToText.setWrappingWidth(100);
        upToText.setTextAlignment(TextAlignment.RIGHT);
        upToGroup.getChildren().addAll(upTo, upToText);
        gamemodeInfos.getChildren().add(upToGroup);

        // separator
        Separator sep = new Separator();
        gamemodeInfos.getChildren().add(sep);

        // Sumo & Oshi
        HBox sumoAnOshiGroup  = new HBox();

        Text sumoAndOshi = new Text("Sumo & Oshi:");
        sumoAndOshi.setStyle("-fx-font-weight: bold");
        sumoAndOshi.setWrappingWidth(200);

        this.sumoAndOshiText = new Text("");
        sumoAndOshiText.setWrappingWidth(100);
        sumoAndOshiText.setTextAlignment(TextAlignment.RIGHT);
        sumoAnOshiGroup.getChildren().addAll(sumoAndOshi, sumoAndOshiText);
        gamemodeInfos.getChildren().add(sumoAnOshiGroup);

        gamemodeGroup.getChildren().add(gamemodeInfos);
        gameConfiguration.getChildren().add(gamemodeGroup);

        // The player mode :)

        HBox playerModeGroup = new HBox();
        playerModeGroup.setAlignment(Pos.BASELINE_CENTER);
        playerModeGroup.setPrefWidth(250);
        playerModeGroup.setMaxWidth(250);
        playerModeGroup.setPadding(new Insets(0, 0, 0, 20));


        Text playerModeLabel = new Text("Player mode:");
        playerModeLabel.setStyle("-fx-font-weight: bold");
        playerModeLabel.setWrappingWidth(100);
        playerModeLabel.setTextAlignment(TextAlignment.LEFT);

        this.playerModeText = new Text("");
        playerModeText.setWrappingWidth(250);

        playerModeGroup.getChildren().addAll(playerModeLabel, this.playerModeText);
        gameConfiguration.getChildren().add(playerModeGroup);

        // Lastly, update all texts
        this.updatePlayerModeText();
        this.updateGamemodeInfos();

        return gameConfiguration;
    }
}
