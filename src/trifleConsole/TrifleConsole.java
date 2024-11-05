package trifleConsole;

import trifleConsole.boardifier.control.Logger;
import trifleConsole.boardifier.control.StageFactory;
import trifleConsole.boardifier.model.GameException;
import trifleConsole.boardifier.model.Model;
import static trifleConsole.boardifier.view.ConsoleColor.*;
import trifleConsole.boardifier.view.View;
import trifleConsole.control.TrifleController;
import rules.BotStrategy;
import rules.GameMode;
import rules.PlayerMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrifleConsole {
    /**
     * Say helllooooo<br>
     * Send the content of the file at "resources/kamisado.asset.text"
     */
    private static void hello(){
        try {
            String kamisadoAsset = Utils.readFile(new File("src/trifle/resources/kamisado.asset.text"));
            System.out.println("\n");
            System.out.println(kamisadoAsset);
        } catch (IOException e) {
            System.out.println(RED_BOLD + "Cannot load the asset `kamisado.asset.text`." + RESET);
        }
    }

    public static void main(String[] args) {
        hello();

        Logger.setLevel(Logger.LOGGER_INFO);
        Logger.setVerbosity(Logger.VERBOSE_BASIC);

        Optional<String> outputMovesDir = Optional.empty();

        // Parse the internal arguments, such as `--output-moves`
        List<String> externalArgs = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "": {break;}
                case "--output-moves": {
                    outputMovesDir = Optional.of(args[i + 1]);
                    // remove this arg
                    args[i] = "";
                    args[i + 1] = "";
                    break;
                }
                default:
                    externalArgs.add(arg);
            }
        }


        Tui tui = new Tui();
        GameMode gameMode = GameMode.defaultValue();

        // get the required data
        if (externalArgs.isEmpty()) {
            tui.run();
            gameMode = tui.getGameMode();
            tui.closeStream();
        }
        else {
            // TODO trouver une autre solution pour définir le mode de jeu dans ce cas, car l'argument à l'index 0 est réservé
            // au mode de joueur
//            switch (externalArgs.get(0)) {
//                case "0": break;
//                case "1": {
//                    gameMode = GameMode.Standard;
//                    break;
//                }
//                case "2": {
//                    gameMode = GameMode.Marathon;
//                    break;
//                }
//                default: {
//                    System.out.println("The gameMode you wish to play (" + externalArgs.get(0) + ") is not between 0 and 2 (inclusive).\nThe gameMode has been automatically changed to Human vs Human.");
//                }
//            }
        }

        Model model = new Model();

        // Add the players to the model
        List<String> playerNames = getPlayerNames(tui, gameMode);
        PlayerMode playerMode = getPlayerMode(tui, externalArgs.isEmpty() ? null : externalArgs.get(0), externalArgs.isEmpty());
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
                System.out.println(RED + "A problem has occurred in the gameMode configuration." + RESET);
                System.exit(1);
            }
        }

        // Initiate the required instances, such as the view, StageFactory and the controller
        StageFactory.registerModelAndView("trifleConsole", "trifleConsole.model.TrifleStageModel", "trifleConsole.view.TrifleStageView");
        View view = new View(model);
        TrifleController controller = new TrifleController(model, view, gameMode, playerMode, playerNames);
        controller.setFirstStageName("trifleConsole");

        model.setNextPlayer();

        if (playerMode != PlayerMode.HumanVsHuman) {
            List<BotStrategy> botStrategies = tui.getBotStrategies();
            controller.defineBots(botStrategies);
        }

        if (outputMovesDir.isPresent()) {
            try {
                controller.addOutputMovesFileWriter(outputMovesDir.get());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }

        try {
            int numberOfRounds = gameMode.requiredPoints();

            System.out.println("You'll play " + numberOfRounds + " rounds.");

            while (controller.getCurrentRound() < numberOfRounds) {
                System.out.println("Round no." + (controller.getCurrentRound() + 1));

                controller.startGame();
                controller.stageLoop();

                controller.increaseRoundCounter();

                if (controller.getCurrentRound() < numberOfRounds - 1) {
                    System.out.println("End of round. Next round start in 5s...");
                    Thread.sleep(5000);
                    System.out.println("\n\n");
                }
            }

            controller.endGame();
        } catch (GameException | InterruptedException e) {
            System.out.println(RED + e.getMessage());
            e.printStackTrace();
            System.out.println("Cannot start the game. Abort" + RESET);
            System.exit(1);
        } finally {
            Logger.trace("Closing InputReader stream...");
            controller.closeStreams();
            Logger.trace("Controller's streams closed successfully");
        }
    }

    /**
     * Get the player Mode
     * @param tui The TUI instance
     * @param mode The player-mode given as argument, if any
     * @param tuiEnabled If whether the tui was called
     * @return The player mode (between zero and two, included)
     */
    private static PlayerMode getPlayerMode(Tui tui, String mode, boolean tuiEnabled) {
        if (tuiEnabled)
            return tui.getPlayerMode();
        else return switch (mode) {
            case "0" -> PlayerMode.HumanVsHuman;
            case "1" -> PlayerMode.HumanVsComputer;
            case "2" -> PlayerMode.ComputerVsComputer;
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }

    /**
     * Get the player (or bot) names
     * <br>
     * Bot names will be generated automatically if there are less than two names registered in the TUI
     * @param tui The TUI instance
     * @param mode The player-mode given as argument, if any
     * @return The list of the names
     */
    private static List<String> getPlayerNames(Tui tui, GameMode mode) {
        List<String> tuiPlayerNames = tui.getPlayerNames();
        if (tuiPlayerNames.isEmpty()) {
            return switch (mode.ordinal()) {
                case 1 -> List.of("Player", "Computer");
                case 2 -> List.of("Computer1", "Computer2");
                default -> List.of("Player1", "Player2");
            };
        } else {
            if (2 - tuiPlayerNames.size() != tui.getPlayerMode().getBotNumber()) {
                for (int i = 0; i < 2 - tuiPlayerNames.size(); i++)
                    tuiPlayerNames.add("Computer" + (i + 1));
            }

            if (tui.getPlayerMode() == PlayerMode.HumanVsComputer && tuiPlayerNames.size() < 2) {
                tuiPlayerNames.add("Computer");
            }

            return tuiPlayerNames;
        }
    }
}
