package trifleConsole.control;

import trifleConsole.boardifier.control.ActionFactory;
import trifleConsole.boardifier.control.ActionPlayer;
import trifleConsole.boardifier.control.Controller;
import trifleConsole.boardifier.model.GameElement;
import trifleConsole.boardifier.model.Model;
import trifleConsole.boardifier.model.Player;
import trifleConsole.boardifier.model.action.ActionList;
import trifleConsole.boardifier.view.ConsoleColor;
import trifleConsole.boardifier.view.View;
import trifleConsole.model.OldMove;
import trifleConsole.model.Pawn;
import trifleConsole.model.TrifleBoard;
import trifleConsole.model.TrifleStageModel;
import rules.BotStrategy;
import rules.GameMode;
import rules.PlayerMode;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

public class TrifleController extends Controller {
    // Store the gameMode. Useful to check how many rounds are left.
    private GameMode gameMode     = GameMode.defaultValue();
    private PlayerMode playerMode = PlayerMode.defaultValue();
    private final List<String> playerNames;
    private final long startTime;

    // Store each computer, yet it can be null
    public TrifleDecider firstComputer;
    public TrifleDecider secondComputer;

    /**
     * Count the number of rounds currently played
     */
    private int currentRound = 0;

    private int bluePlayerPoints = 0;
    private int cyanPlayerPoints = 0;

    public FileWriter outputMovesFileWriter;
    private final InputStreamReader inputStreamReader;



    /**
     * The Buffer used by the game
     */
    BufferedReader consoleSysIn;

    public TrifleController(Model model, View view, GameMode gameMode, PlayerMode playerMode, List<String> playerNames) {
        super(model, view);
        this.gameMode = gameMode;
        this.playerMode = playerMode;
        this.playerNames = playerNames;

        this.inputStreamReader = new InputStreamReader(System.in);
        this.consoleSysIn = new BufferedReader(inputStreamReader);

        this.startTime = System.currentTimeMillis();
    }

    public void defineBots(List<BotStrategy> botStrategies){
        switch (this.playerMode) {
            case HumanVsHuman: { break; }
            case HumanVsComputer: {
                this.secondComputer = botStrategies.get(0).initComputer(model, this);
                break;
            }
            case ComputerVsComputer: {
                this.firstComputer = botStrategies.get(0).initComputer(model, this);
                this.secondComputer = botStrategies.get(1).initComputer(model, this);
                break;
            }
            default:
                System.out.println(ConsoleColor.RED + "Unknown bot strategy: " + this.gameMode + ConsoleColor.RESET);
        }
    }

    public void addOutputMovesFileWriter(String path) throws IOException {
        if (this.outputMovesFileWriter != null)
            throw new IllegalArgumentException("The output file writer have already been defined.");

        this.outputMovesFileWriter = new FileWriter(path);
        this.outputMovesFileWriter.write("");
    }

    /**
     * This method is used to transmit information to the Model, such as the game mode
     */
    private void shareInformations() {
        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();

        stageModel.setGameMode(gameMode);
        stageModel.setPlayerNames(playerNames);
    }

    /**
     * The game loop
     */
    @Override
    public void stageLoop() {
        this.shareInformations();

        this.updateRoundCounter();
        this.updatePlayersCounter();

        this.update();
        int waitBeforeEnd = 0; // 2500
        if (System.getenv().containsKey("WAIT_BEFORE_END")) {
            waitBeforeEnd = Integer.parseInt(System.getenv().get("WAIT_BEFORE_END"));
        }

        while(!this.model.isEndStage() && this.model.getIdWinner() == -1) {
            long before = System.currentTimeMillis();

            this.playTurn();
            this.endOfTurn();
            this.update();

            long after = System.currentTimeMillis();
            if (waitBeforeEnd > 0 && after - before < waitBeforeEnd) {
                // Sleep
                try { Thread.sleep(waitBeforeEnd - (after-before)); }
                catch (InterruptedException e) { System.out.println(e.getMessage()); e.printStackTrace(); }
            }
        }
    }

    public void closeStreams(){
        try {
            this.consoleSysIn.close();
            inputStreamReader.close();

            // Close the file writer if any
            if (this.outputMovesFileWriter != null)
                this.outputMovesFileWriter.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void playTurn() {
        Player p = model.getCurrentPlayer();

        TrifleStageModel stage = (TrifleStageModel) model.getGameStage();
        if (stage.isBluePlayerBlocked() && stage.isCyanPlayerBlocked()) {
            System.out.println("All players cannot move, it's a draw.");
            this.endGame();
        }

        switch (p.getType()) {
            case Player.HUMAN: {
                playerTurn(p);
                break;
            }
            case Player.COMPUTER: {
                botTurn(p);
                break;
            }
            default: {
                System.out.println(ConsoleColor.RED + "Player type is nether a player or a human, what?\nThis error is critical, the game will exit." + ConsoleColor.RESET);
                System.exit(1);
            }
        }
    }

    private static void printMoveHelp(){
        System.out.println("\nHow to move my pawn?");
        System.out.println("We will take an example. If you are Player 1 and want to move your cyan pawn at A1 to G7, you input will be:");
        System.out.println("  CG7 or A1G7 (where A1 is the pawn position)");
        System.out.println("*`C` is for `cyan` and where you want to move it.*");
        System.out.println("Here is each color code:");
        System.out.println("- Cyan:   `c`");
        System.out.println("- Blue:   `b`");
        System.out.println("- Purple: `p`");
        System.out.println("- White:  `d`");
        System.out.println("- Orange: `e`");
        System.out.println("- Red:    `f`");
        System.out.println("- Green:  `g`");
        System.out.println("- Black:  `n`");
        System.out.println("You can view again this message by typing `?` in your input.\n");
    }

    private static final Pattern MOVE_PATTERN = Pattern.compile("^([a-hA-H][1-8]|[cbpdefgnCBPDEFGN])([a-hA-H][1-8])$");

    public void playerTurn(Player p) {
        boolean ok = false;

        while (!ok) {
            System.out.print(p.getName() + " > ");
            try {
                String potentialMove = consoleSysIn.readLine();
                if (potentialMove == null || potentialMove.isEmpty()) {
                    System.out.println("\n" + ConsoleColor.RED + "The program received nothing as action. The stdin channel probably ended." + ConsoleColor.RESET);
                    System.exit(1);
                }

                String move = potentialMove.toLowerCase().trim();

                if (move.contains("stop")) {
                    System.out.println(ConsoleColor.YELLOW + "You used the keyword 'stop'. End of the game." + ConsoleColor.RESET);
                    endGame();
                    System.exit(0);
                }

                if (move.equals("?")) {
                    printMoveHelp();
                    continue;
                }

                if ((move.length() != 3 && move.length() != 4) || !MOVE_PATTERN.matcher(move).find()) {
                    System.out.println(ConsoleColor.RED + "Your input must be like `A1G7` or `cG7`, where the first is whether the color code or the coordinate of the move, and after, where you want to move it." + ConsoleColor.RESET);
                    System.out.println(ConsoleColor.YELLOW + "The match is insensitive and doesn't differentiate upper and lower case" + ConsoleColor.RESET);
                    continue;
                }

                ok = this.analyseAndPlay(move);

            } catch (IOException e) {
                System.out.println(ConsoleColor.RED + e.getMessage());
                e.printStackTrace();
                System.out.println(ConsoleColor.RESET);
                System.exit(1);
            }
        }
    }

    private void updateRoundCounter(){
        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();

        String text = "Round " + (this.currentRound + 1) + "/" + gameMode.requiredPoints();
        stageModel.getRoundCounter().setText(text);
    }

    private void updatePlayersCounter(){
        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();

        stageModel.updatePlayerPoints(bluePlayerPoints, cyanPlayerPoints);
    }

    @Override
    public void endGame(){
        // Winner logic
        if (this.gameMode.requiredPoints() > this.currentRound) {
            // A round ended, so we give the winner of this round or tell that it's a draw otherwise
            if (model.getIdWinner() != -1) {
                System.out.println(model.getPlayers().get(model.getIdWinner()).getName() + " wins this round (no." + (this.currentRound + 1) + ")");

                // increase the winner's points
                if (model.getIdPlayer() == 0) {
                    this.bluePlayerPoints++;
                } else {
                    this.cyanPlayerPoints++;
                }
            } else {
                System.out.println("Draw for the round " + (this.currentRound + 1));
            }

            // Update the round counter
            this.updateRoundCounter();
        } else {
            // All rounds have been played, we gave the number of points for each player and who win
            System.out.println(model.getPlayers().get(0).getName() + ": " + bluePlayerPoints);
            System.out.println(model.getPlayers().get(1).getName() + ": " + cyanPlayerPoints);
            System.out.println();
            System.out.println("Rounds: " + gameMode.requiredPoints());
            System.out.println("Time:   " + formatTime(System.currentTimeMillis() - startTime));

            int winnerID;

            if (bluePlayerPoints > cyanPlayerPoints) {
                System.out.println("    " + model.getPlayers().get(0).getName() + " wins this game.");
                winnerID = 0;
            }
            else if (bluePlayerPoints < cyanPlayerPoints){
                System.out.println("    " + model.getPlayers().get(1).getName() + " wins this game.");
                winnerID = 1;
            }  else {
                System.out.println("    Draw.");
                winnerID = -1;
            }

            // Write the result to a specific file
            try {
                FileWriter fileWriter = new FileWriter(".trifle000001");
                fileWriter.write("");
                fileWriter.append(Integer.toString(winnerID)).append('\n');
                fileWriter.append(Integer.toString(bluePlayerPoints)).append('\n');
                fileWriter.append(Integer.toString(cyanPlayerPoints));
                fileWriter.close();
            } catch(IOException e) {
                System.out.println(ConsoleColor.RED + "Cannot write the result's file: " + e.getMessage());
            }
        }
        super.endGame();
        System.exit(0);
    }

    private String formatTime(long time){
        String t = "";

        long hours = time / 1000 / 60 / 60 % 24;
        if (hours > 0)
            t += hours+"h ";

        long minutes = time / 1000 / 60 % 60;
        if (minutes > 0)
            t += minutes+"m ";

        long seconds = time / 1000 % 60;
        if (seconds > 0)
            t += seconds+"s";

        return t.trim();
    }

    private void addOldMoveToFile(String move){
        if (this.outputMovesFileWriter == null)
            return;

        try {
            this.outputMovesFileWriter.append(move).append("\n");
            this.outputMovesFileWriter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMoveToOldMoves(Player p, String normalizedPawn, String normalizedMove){
        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();

        OldMove oldMove = new OldMove(
                p.getName().equals(playerNames.get(0)) ? 0 : 1,
                p.getName(),
                normalizedPawn,
                normalizedMove
        );

        stageModel.addOldMove(oldMove);
        stageModel.updateHistory();
    }

    public TrifleStageModel getStageModel(){
        return (TrifleStageModel) model.getGameStage();
    }

    public void botTurn(Player _p) {
        ActionList actions;
        if (model.getIdPlayer() == 0) actions = this.firstComputer.decide();
        else actions = this.secondComputer.decide();

        // Play the move
        actions.setDoEndOfTurn(true);
        ActionPlayer play = new ActionPlayer(model, this, actions);

        play.start();
    }

    /**
     * Analyze the movement and play if possible
     * @param move The move requested by the user. Must be lowercase
     * @return true if the move was approved, false elsewhere
     */
    private boolean analyseAndPlay(String move) {
        TrifleStageModel gameStage = (TrifleStageModel) model.getGameStage();

        Integer pawnIndex = extractPawnIndex(move);
        if (pawnIndex == null) {
            System.out.println(ConsoleColor.RED + "The pawn you gave wasn't found." + ConsoleColor.RESET);
            return false;
        }

        Point moveCoordinates = extractRequestedMove(move);
        if (moveCoordinates == null) {
            System.out.println(ConsoleColor.RED + "The movement you gave is invalid." + ConsoleColor.RESET);
            return false;
        }

        // now we know that the user input is correctly formed, we can check if the movement is legal
        List<Pawn> pawns = model.getIdPlayer() == 0 ? gameStage.getBluePlayer() : gameStage.getCyanPlayer();
        Pawn pawn = model.getIdPlayer() == 0 ? pawns.get(pawnIndex) : pawns.get(7 - pawnIndex);

        // Check if the player can move his pawn
        // If not display a message and wait 2s
        if (!((TrifleBoard) gameStage.getBoard()).canPawnMove(pawn, model.getIdPlayer())) {
            System.out.println(ConsoleColor.YELLOW + "The pawn that you must move cannot move in the current situation. Your turn will be skipped." + ConsoleColor.RESET);

            gameStage.setPlayerBlocked(model.getIdPlayer(), true);

            try { Thread.sleep(2000); } catch(InterruptedException ignored) {}
            return true;
        }
        gameStage.setPlayerBlocked(model.getIdPlayer(), false);

        Point lastEnemyMovement = model.getIdPlayer() == 0
                ? gameStage.getLastCyanPlayerMove()
                    : gameStage.getLastBluePlayerMove();

        // Check the pawn color based on the last movement of the enemy.
        if (lastEnemyMovement != null) {
            // get this cell color
            int colorIndex = TrifleBoard.BOARD[lastEnemyMovement.y][lastEnemyMovement.x];
            if (colorIndex != pawn.getColorIndex()) {
                System.out.println(ConsoleColor.RED + "You can only play the pawn on which your opponent's pawn case color was moved.\nThis means that, if he move his blue pawn onto the red case, you must play the red pawn.\n" + ConsoleColor.RESET);
                return false;
            }
        }

        // Update the allowed cells
        ((TrifleBoard) gameStage.getBoard()).setValidCells(pawn.getCoords(), model.getIdPlayer());

        // check it
        if (!gameStage.getBoard().canReachCell(moveCoordinates.x, moveCoordinates.y)) {
            System.out.println(ConsoleColor.RED + "You can't play this move. Try again." + ConsoleColor.RESET);
            return false;
        }

        ActionList actions = ActionFactory.generatePutInContainer(
                model,
                pawn,
                TrifleBoard.BOARD_ID,
                moveCoordinates.y,
                moveCoordinates.x
        );

        actions.setDoEndOfTurn(true);
        ActionPlayer play = new ActionPlayer(model, this, actions);

        play.start();


        this.registerMove(
                gameStage,
                moveCoordinates,
                move,
                pawn
        );

        return true;
    }

    public void registerMove(TrifleStageModel gameStage, Point moveCoordinates, String move, Pawn pawn){
        this.addOldMoveToFile(move);
        this.addMoveToOldMoves(model.getCurrentPlayer(), pawn.getFormattedPawnId(), normalizeCoordinate(moveCoordinates, true));
        pawn.setCoords(moveCoordinates);

        // !! Last one !!
        this.detectWin();
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
            int colorIndex = TrifleBoard.BOARD[coordinates.y][coordinates.x];
            sb += Pawn.COLORS[colorIndex];
        }

        sb += ((char) (coordinates.x + 65)) + "";
        sb += (coordinates.y + 1);

        if (colored)
            sb += ConsoleColor.RESET;

        return sb;
    }

    /**
     * Detect if there is a win-win situation
     */
    private void detectWin(){
        TrifleStageModel gameStage = (TrifleStageModel) model.getGameStage();

        // check blue pawns
        for (Pawn pawn: gameStage.getBluePlayer()) {
            if (pawn.getCoords().y == 7) {
                model.setIdWinner(0);
                this.endGame();
            }
        }

        // check cyan pawns
        for (Pawn pawn: gameStage.getCyanPlayer()) {
            if (pawn.getCoords().y == 0) {
                model.setIdWinner(1);
                this.endGame();
            }
        }
    }

    /**
     * Return the pawn index from the user's input
     * @param move The movement in lower case, it can be CG7 or A1G7, for example
     * @return The pawn index or null if it doesn't exist/is invalid
     */
    public Integer extractPawnIndex(String move) {
        TrifleStageModel gameStage = (TrifleStageModel) model.getGameStage();

        if (move.length() == 3) {
            // a color code
            return switch (move.charAt(0)) {
                case 'c' -> 0;
                case 'b' -> 1;
                case 'p' -> 2;
                case 'd' -> 3;
                case 'e' -> 4;
                case 'f' -> 5;
                case 'g' -> 6;
                case 'n' -> 7;
                default -> null;
            };
        }
        else if (move.length() == 4) {
            // the first two characters are the pawn coordinates
            int x = ((int) move.charAt(0)) - 97; // we get the first char,
            // and as it should be between a and h (included),
            // we use `pos - 97` (ascii code of 'a')

            if (x < 0 || x > 7) {
                System.out.println(ConsoleColor.RED + "The position of the pawn you gave is invalid on the X axis at least." + ConsoleColor.RESET);
                return null;
            }

            int y = ((int) move.charAt(1)) - 49; // we get the second char,
            // and as it should be between 1 and 8 (included),
            // we use `pos - 48` (ascii code of '0')

            if (y < 0 || y > 7) {
                System.out.println(ConsoleColor.RED + "The position of the pawn you gave is invalid on the Y axis." + ConsoleColor.RESET);
                return null;
            }

            TrifleBoard board = (TrifleBoard) gameStage.getBoard();

            GameElement pawn = board.getElement(y, x);
            return pawn != null ? ((Pawn) pawn).getColorIndex() : null;
        }
        return null;
    }

    /**
     * Return the coordinates of the wanted movement from the user's input
     * @param move The movement in lower case, it can be CG7 or A1G7, for example
     * @return The coordinates on (x, y) of the movement, or null if the movement is invalid/out of bound
     */
    public Point extractRequestedMove(String move) {
        int offset;
        if (move.length() == 3) {
            offset = 1;
        } else if (move.length() == 4) {
            offset = 2;
        } else throw new IllegalArgumentException("The input length is not 3 or 4, the input form wasn't checked before being passed to this function");

        int x = ((int) move.charAt(offset) - 97); // same method as `extractPawnPosition`

        if (x < 0 || x > 7) {
            System.out.println(ConsoleColor.RED + "The movement you gave is invalid on the X axis at least." + ConsoleColor.RESET);
            return null;
        }

        int y = ((int) move.charAt(offset + 1) - 49); // same method as `extractPawnPosition`

        if (y < 0 || y > 8) {
            System.out.println(ConsoleColor.RED + "The movement you gave is invalid on the Y axis." + ConsoleColor.RESET);
            return null;
        }

        return new Point(x, y);
    }

    public void increaseRoundCounter(){
        this.currentRound++;
    }
    public int getCurrentRound(){
        return this.currentRound;
    }

    @Override
    public void endOfTurn(){
        System.out.println();
        model.setNextPlayer();

        Player p = model.getCurrentPlayer();
        TrifleStageModel stageModel = (TrifleStageModel) model.getGameStage();

        String text = p.getName() + " is playing.";

        Point lastOpponentMove = model.getIdPlayer() == 0 ? stageModel.getLastCyanPlayerMove() : stageModel.getLastBluePlayerMove();
        if (lastOpponentMove == null) {
            text += " You start, so you can choose which pawn your moving.";
        } else {
            // Tell which color must be moved
            int colorIndex = TrifleBoard.BOARD[lastOpponentMove.y][lastOpponentMove.x];

            text += " You must play the ";
            text += Pawn.COLORS[colorIndex];

            text += switch (colorIndex) {
                case 0 -> "Cyan";
                case 1 -> "Blue";
                case 2 -> "Purple";
                case 3 -> "White";
                case 4 -> "Yellow";
                case 5 -> "Red";
                case 6 -> "Green";
                case 7 -> "Black";
                default -> "Unknown";
            };

            text += ConsoleColor.RESET;

            text += " at ";
            text += normalizeCoordinate(
                    stageModel.getPlayerPawn(model.getIdPlayer(), colorIndex).getCoords(),
                    true
            );
        }

        stageModel.getPlayerName().setText(text);
    }

    public TrifleDecider getFirstComputer() {
        return firstComputer;
    }


    public TrifleDecider getSecondComputer() {
        return secondComputer;
    }
}
