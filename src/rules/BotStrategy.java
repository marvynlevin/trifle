package rules;

import bots.DeterministicMinMaxBot;
import bots.DeterministicMinMaxBotGraphic;
import trifleConsole.boardifier.control.Controller;
import trifleConsole.boardifier.model.Model;
import trifleConsole.control.TrifleDecider;
import trifleGraphic.controllers.BotDecider;

public enum BotStrategy {
    BotEurDeCul,
    MinMaxDeterministic;

    public static final BotStrategy DEFAULT = BotStrategy.MinMaxDeterministic;

    /**
     * @return The name of the strategy
     */
    public String toString() {
        return switch (this) {
            case BotEurDeCul -> "BotEur de cul";
            case MinMaxDeterministic -> "Mellie (MinMax)";
        };
    }

    public String getDescription(){
        return switch (this) {
            case BotEurDeCul -> "A simple deterministic algorithm";
            case MinMaxDeterministic -> "MinMax with a deterministic algorithm";
        };
    }

    public TrifleDecider initComputer(Model model, Controller controller){
        return switch (this) {
            case MinMaxDeterministic -> new DeterministicMinMaxBot(model, controller);
            default -> new TrifleDecider(model, controller);
        };
    }


    public BotDecider initComputerGraphic(
            trifleGraphic.boardifierGraphic.model.Model model,
            trifleGraphic.boardifierGraphic.control.Controller controller
    ){
        return switch (this) {
            case MinMaxDeterministic -> new DeterministicMinMaxBotGraphic(model, controller);
            default -> new BotDecider(model, controller);
        };
    }
}
