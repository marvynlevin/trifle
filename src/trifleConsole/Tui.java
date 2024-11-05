package trifleConsole;

import trifleConsole.boardifier.view.ConsoleColor;
import rules.BotStrategy;
import rules.GameMode;
import rules.PlayerMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tui {
    private List<String> playerNames = new ArrayList<>();
    private List<BotStrategy> botStrategies = new ArrayList<>();
    private GameMode gameMode;
    private PlayerMode playerMode;

    private Scanner scanner;

    public void reset() {
        this.gameMode      = null;
        this.playerMode    = null;
        this.playerNames   = new ArrayList<>();
        this.botStrategies = new ArrayList<>();
    }

    public void closeStream(){
        this.scanner = null;
    }

    /**
     * Draw the navigation
     */
    private void drawNavMenu(){
        System.out.println();
        System.out.println("You are in the interactive configuration menu.\nSelect an option");

        System.out.println("(1). Define game mode");
        System.out.println("(2). Define the player mode (human or computer)");
        System.out.println("(3). Define the name of the player(s)");
        System.out.println("(4). Define bot strategy(ies)");
        System.out.println("(5). Display current configuration");
        System.out.println("(6). Start the game");

        System.out.println("Enter 'q' to exit");
        System.out.print("> ");
    }

    /**
     * Check whether the configuration is ready
     * @return true if the config is ready to be played, false elsewhere
     */
    private boolean isConfigReady(){
        if (this.gameMode == null || this.playerMode == null)
            return false;

        return switch (this.playerMode) {
            case HumanVsHuman -> this.playerNames.size() == 2;
            case HumanVsComputer -> this.playerNames.size() == 1;
            case ComputerVsComputer -> this.playerNames.isEmpty();
        };
    }

    /**
     * The main loop
     */
    public void run() {
        this.reset();

        if (this.scanner == null)
            this.scanner = new Scanner(System.in);

        boolean run = true;

        while (run){
            this.drawNavMenu();
            String line = scanner.nextLine().toLowerCase().trim();

            switch (line) {
                case "q": {
                    System.out.println(ConsoleColor.GREEN + "Roger, see you soon!" + ConsoleColor.RESET);
                    System.exit(0);
                }
                // Définir le mode de jeu
                case "1": {
                    this.setGameMode();
                    break;
                }
                // Définir le mode de joueur.
                // Il faut noter qu'on va réinitialiser les joueurs enregistrés (sauf si on passe de 2 à 1, là, on va "crop" et prévenir l'utilisateur)
                case "2": {
                    this.setPlayerMode();
                    break;
                }
                // Définir les joueurs.
                // On va afficher la liste, la réinitialiser puis demander le nombre N de joueurs requis pour le mode de joueur
                case "3": {
                    this.definePlayers();
                    break;
                }
                case "4": {
                    this.defineBotStrategies();
                    break;
                }
                // On affiche la configuration actuelle
                case "5": {
                    this.showActualConfiguration();
                    break;
                }
                // Lancer la partie
                case "6": {
                    if (isConfigReady())
                        run = false;
                    else
                        this.notConfigured();

                    break;
                }
                // I'm blue dabedi dabeda
                case "trifleConsole": {
                    this.recette();
                    break;
                }
                default: {
                    System.out.println(ConsoleColor.RED + "I don't understand this action." + ConsoleColor.RESET);
                }
            }
        }
    }

    /**
     * Define the players
     */
    private void definePlayers(){
        if (this.playerMode == null) {
            System.out.println(ConsoleColor.RED + "You must select the player mode before configuring the players." + ConsoleColor.RESET);
            return;
        }

        int playersNb = switch (this.playerMode) {
            case HumanVsHuman -> 2;
            case HumanVsComputer -> 1;
            case ComputerVsComputer -> 0;
        };

        if (playersNb < 1) {
            System.out.println(ConsoleColor.YELLOW + "No human player is required for this player mode." + ConsoleColor.RESET);
            return;
        }

        System.out.println("If you want to exit, type 'exit'");

        // On définit les joueurs
        for (int i = 0; i < playersNb; i++) {
            boolean r = this.addPlayer(i + 1);

            if (!r) {
                System.out.println(ConsoleColor.YELLOW + "You exited this sub-menu. Registered players will not be cleared" + ConsoleColor.RESET);
                return;
            }
        }

        System.out.println(ConsoleColor.GREEN + "All the players have been registered." + ConsoleColor.RESET);
    }

    /**
     * Add a single player
     * @param nb The number of the player (to say "Player n°<nb>"), it's purely aesthetic
     * @return whether the player want to exit or not
     */
    private boolean addPlayer(int nb){
        System.out.println("\nPlayer configuration no." + nb);

        boolean r = true;
        String name = "";
        while (r) {
            System.out.print("What's his name? ");

            name = scanner.nextLine().trim();

            if (name.equals("exit")) {
                return true;
            }

            r = !this.yesOrNo("Player name no." + nb + " will be '" + name + "', are you sure? (Y/n) ");
        }
        this.playerNames.add(name);
        System.out.println("Player name no." + nb + " will be '" + name + "'");
        return false;
    }

    /**
     * A helper function to check if the user says yes or no
     * @param msg The message
     * @return true for yes, false for no
     */
    private boolean yesOrNo(String msg){
        while (true) {
            System.out.print(msg);
            String l = scanner.nextLine().trim().toLowerCase();

            if (l.isEmpty())
                return true;

            switch (l) {
                case "y": case "yes": case "o": case "oui":
                    return true;
                case "n": case "no": case "non":
                    return false;
                default:
                    System.out.println(ConsoleColor.RED + "You must answer Y or N." + ConsoleColor.RESET);
            }
        }
    }

    /**
     * Set the game mode for the game
     */
    private void setGameMode(){
        while (true){
            System.out.println();
            System.out.println("Game mode definition");
            System.out.println("MGame modes available:");
            System.out.println("(a) " + GameMode.Fast     + "\n      " + GameMode.Fast.getDescription());
            System.out.println("(b) " + GameMode.Standard + "\n      " + GameMode.Standard.getDescription());
            System.out.println("(c) " + GameMode.Marathon + "\n      " + GameMode.Marathon.getDescription());
            System.out.println("Type 'exit' to leave this menu");
            System.out.print(">> ");

            String selection = scanner.nextLine().toLowerCase().trim();
            switch (selection){
                case "a": {
                    this.gameMode = GameMode.Fast;
                    System.out.println("\nGame mode selected: " + ConsoleColor.WHITE_BOLD + GameMode.Fast + ConsoleColor.RESET);
                    return;
                }
                case "b": {
                    this.gameMode = GameMode.Standard;
                    System.out.println("\nGame mode selected: " + ConsoleColor.WHITE_BOLD + GameMode.Standard + ConsoleColor.RESET);
                    return;
                }
                case "c": {
                    this.gameMode = GameMode.Marathon;
                    System.out.println("\nGame mode selected: " + ConsoleColor.WHITE_BOLD + GameMode.Marathon + ConsoleColor.RESET);
                    return;
                }
                case "exit": {
                    System.out.println("Back to the main menu.");
                    return;
                }
                default: {
                    System.out.println(ConsoleColor.RED + "Your selection is invalid." + ConsoleColor.RESET);
                }
            }
        }
    }

    /**
     * Set the player mode for the game
     */
    private void setPlayerMode(){
        while (true) {
            System.out.println();
            System.out.println("Definition of player mode");
            System.out.println(ConsoleColor.RED + "Registered players and bots will be deleted." + ConsoleColor.RESET);
            System.out.println("Player modes available:");
            System.out.println("(a) " + PlayerMode.HumanVsHuman);
            System.out.println("(b) " + PlayerMode.HumanVsComputer);
            System.out.println("(c) " + PlayerMode.ComputerVsComputer);
            System.out.println("Type 'exit' to leave this menu");
            System.out.print(">> ");

            String selection = scanner.nextLine().toLowerCase().trim();
            switch (selection) {
                case "a": {
                    this.playerMode = PlayerMode.HumanVsHuman;
                    System.out.println("\nPlayer mode selected: " + ConsoleColor.WHITE_BOLD + PlayerMode.HumanVsHuman + ConsoleColor.RESET);
                    this.resetPlayers();
                    return;
                }
                case "b": {
                    this.playerMode = PlayerMode.HumanVsComputer;
                    System.out.println("\nPlayer mode selected: " + ConsoleColor.WHITE_BOLD + PlayerMode.HumanVsComputer + ConsoleColor.RESET);
                    System.out.println("By default, bots will use the strategy" + ConsoleColor.WHITE_BOLD + BotStrategy.DEFAULT + ConsoleColor.RESET);
                    this.resetPlayers();
                    return;
                }
                case "c": {
                    this.playerMode = PlayerMode.ComputerVsComputer;
                    System.out.println("\nPlayer mode selected: " + ConsoleColor.WHITE_BOLD + PlayerMode.ComputerVsComputer + ConsoleColor.RESET);
                    System.out.println("By default, bots will use the strategy" + ConsoleColor.WHITE_BOLD + BotStrategy.DEFAULT + ConsoleColor.RESET);
                    this.resetPlayers();
                    return;
                }
                default: {
                    System.out.println(ConsoleColor.RED + "Your selection is invalid." + ConsoleColor.RESET);
                }
            }
        }
    }

    /**
     * Set the bot strategy
     */
    private void defineBotStrategies(){
        System.out.println();
        if (this.playerMode == null) {
            System.out.println(ConsoleColor.RED + "You cannot configure the bots' strategies until you have chosen the player mode." + ConsoleColor.RESET);
            return;
        }
        if (this.playerMode == PlayerMode.HumanVsHuman) {
            System.out.println("Mode " + this.playerMode + " does not involve any bot, so there is no strategy to define.");
            return;
        }

        System.out.println(ConsoleColor.RED + "All bot strategies have been reset." + ConsoleColor.RESET);
        this.resetBotStrategies();

        int nb = switch (this.playerMode) {
            case HumanVsComputer    -> 1;
            case ComputerVsComputer -> 2;
            default                 -> 0;
        };

        for (int i = 0; i < nb; i++) {
            boolean r = this.setSingleBotStrategy(i + 1);

            if (r) {
                System.out.println(ConsoleColor.YELLOW + "You exited this sub-menu. Registered bot strategies will not be cleared" + ConsoleColor.RESET);
                return;
            }
        }

        if (nb > 1)
            System.out.println(ConsoleColor.GREEN + "All bot strategies have been defined" + ConsoleColor.RESET);
        else
            System.out.println(ConsoleColor.GREEN + "The bot strategy has been defined" + ConsoleColor.RESET);
    }

    /**
     *
     * @param n The ID of this bot
     * @return Whether the user want to exit or not
     */
    private boolean setSingleBotStrategy(int n){
        while (true) {
            System.out.println("The strategies are:");
            System.out.println("(a). " + BotStrategy.BotEurDeCul);
            System.out.println("    " + BotStrategy.BotEurDeCul.getDescription());
            System.out.println("(b). " + BotStrategy.MinMaxDeterministic);
            System.out.println("    " + BotStrategy.MinMaxDeterministic.getDescription());
            System.out.print("What will be bot n°" + n + "'s strategy? ");

            String l = scanner.nextLine().trim().toLowerCase();

            switch (l) {
                case "a": {
                    this.botStrategies.add(BotStrategy.BotEurDeCul);
                    System.out.println("Bot no." + n + " strategy will be " + ConsoleColor.WHITE_BOLD + BotStrategy.BotEurDeCul + ConsoleColor.RESET);
                    return true;
                }
                case "b": {
                    this.botStrategies.add(BotStrategy.MinMaxDeterministic);
                    System.out.println("Bot no." + n + " strategy will be " + ConsoleColor.WHITE_BOLD + BotStrategy.MinMaxDeterministic + ConsoleColor.RESET);
                    return false;
                }
                case "exit": {
                    return true;
                }
                default:
                    System.out.println(ConsoleColor.RED + "Your selection is invalid." + ConsoleColor.RESET);
            }
        }
    }

    /**
     * Reset the player list for this configuration
     */
    private void resetPlayers(){
        this.playerNames.clear();
    }

    /**
     * Reset the player list for this configuration
     */
    private void resetBotStrategies(){
        this.botStrategies.clear();
    }

    /**
     * Print the required information to start the game
     */
    private void notConfigured(){
        System.out.println(ConsoleColor.RED + "Some elements need to be configured:");
        if (this.gameMode == null)
            System.out.println("- Game mode (fast, standard, marathon)");
        if (this.playerMode == null)
            System.out.println("- Player mode (Human vs Human, Human vs Computer, Computer vs Computer)");
        else {
            if ((this.playerMode == PlayerMode.HumanVsHuman) && (this.playerNames.size() != 2))
                System.out.println("- The player mode specified requires 2 human players, but you have  " + this.playerNames.size());

            if ((this.playerMode == PlayerMode.HumanVsComputer) && (this.playerNames.size() != 1))
                System.out.println("- The specified player mode requires 1 human player, but you have  " + this.playerNames.size());
        }
        System.out.print(ConsoleColor.RESET);
    }

    /**
     * Show the actual configuration in a formatted-way
     */
    private void showActualConfiguration(){
        System.out.println("\nCurrent configuration:");
        System.out.println("  Game mode:          "
                + (this.gameMode == null ? ConsoleColor.WHITE_BOLD + "No game mode defined" + ConsoleColor.RESET : this.gameMode));
        System.out.println("  Mode de joueur:     "
                + (this.playerMode == null ? ConsoleColor.WHITE_BOLD + "No player mode defined" + ConsoleColor.RESET : this.playerMode));

        System.out.print  ("  Registered players: ");
        if (this.playerNames == null || this.playerNames.isEmpty()) {
            System.out.println(ConsoleColor.WHITE_BOLD + "No players registered" + ConsoleColor.RESET);
        } else {
            String playerList = String.join(", ", this.playerNames);
            System.out.println(playerList);
        }
        System.out.print  ("  Bots strategies:    ");
        if (this.botStrategies == null || this.botStrategies.isEmpty()) {
            System.out.println(ConsoleColor.WHITE_BOLD + "No bot strategy defined, the default strategy is " + ConsoleColor.RESET + BotStrategy.DEFAULT);
        } else {
            switch (this.playerMode) {
                case HumanVsHuman: {
                    System.out.println("\nSo, er... You're not supposed to be here...");
                    break;
                }
                case HumanVsComputer: {
                    System.out.println("Computer <" + this.botStrategies.get(0) + ">");
                    break;
                }
                case ComputerVsComputer: {
                    System.out.print("Computer n°1 <" + this.botStrategies.get(0) + ">, Computer n°2 <" + this.botStrategies.get(1) + ">");
                    break;
                }
            }
        }

        System.out.println();
    }

    /**
     * Et paf ça fait des chocapics
     */
    private void recette(){
        System.out.println("\n");
        System.out.println("\033[1mIngrédients:\033[0m");
        System.out.println("- 1 paquet de biscuits sablés (environ 200g)");
        System.out.println("- 500ml de crème fraîche liquide");
        System.out.println("- 3 cuillères à soupe de sucre");
        System.out.println("- 1 sachet de sucre vanillé");
        System.out.println("- 1 pot de confiture de framboises (environ 350g)");
        System.out.println("- 100g de chocolat noir");

        System.out.println();

        System.out.println("\033[1mPréparation:\033[0m");
        System.out.println("1. \033[1mPréparer la crème Chantilly:\033[0m");
        System.out.println("    * Fouetter la crème fraîche liquide avec le sucre et le sucre vanillé jusqu'à obtenir une chantilly ferme.");

        System.out.println();

        System.out.println("2. \033[1mMontage du trifle:\033[0m");
        System.out.println("    * Émietter les biscuits sablés dans le fond d'un plat transparent.");
        System.out.println("    * Recouvrir de confiture de framboises.");
        System.out.println("    * Étaler une couche de crème Chantilly.");
        System.out.println("    * Répéter les couches jusqu'à épuisement des ingrédients.");
        System.out.println("    * Terminer par une couche de crème Chantilly.");

        System.out.println();

        System.out.println("3. \033[1mDécoration:\033[0m");
        System.out.println("    * Râper le chocolat noir et le parsemer sur la crème Chantilly.");
        System.out.println("    * Placer le trifle au réfrigérateur pendant au moins 2 heures avant de servir.");
        System.out.println();
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public PlayerMode getPlayerMode() {
        return playerMode;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public List<BotStrategy> getBotStrategies(){
        if (this.botStrategies.size() > 1)
            return this.botStrategies;

        List<BotStrategy> botStrategies = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            if (i < this.botStrategies.size()) botStrategies.add(this.botStrategies.get(i));
            else botStrategies.add(BotStrategy.DEFAULT);
        }

        return botStrategies;
    }

    public Tui() {}
}
