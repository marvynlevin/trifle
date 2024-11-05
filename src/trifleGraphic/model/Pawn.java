package trifleGraphic.model;

import trifleGraphic.boardifierGraphic.control.Logger;
import trifleGraphic.boardifierGraphic.model.ElementTypes;
import trifleGraphic.boardifierGraphic.model.GameElement;
import trifleGraphic.boardifierGraphic.model.GameStageModel;
import trifleGraphic.boardifierGraphic.model.animation.Animation;
import trifleGraphic.boardifierGraphic.model.animation.AnimationStep;
import trifleGraphic.controllers.GameController;
import trifleGraphic.view.PawnLook;

import java.awt.*;

import static trifleGraphic.view.TrifleBoardLook.PAWN_SIZE;

/**
 * This is a pawn in the game which store what color he is.
 */
public class Pawn extends GameElement {
    public static final int PAWN_ELEMENT_ID = 50;
    public static final int BLUE_PLAYER = 1;
    public static final int CYAN_PLAYER = 2;

    public static final Color[] COLORS = new Color[]{
            Color.getHSBColor(
                    19f / 360f,
                    0.737f,
                    0.878f
            ),
            Color.getHSBColor(
                    213f / 360f,
                    0.682f,
                    0.678f
            ),
            Color.getHSBColor(
                    314f / 360f,
                    0.694f,
                    0.475f
            ),
            Color.getHSBColor(
                    341f / 360f,
                    0.541f,
                    0.898f
            ),
            Color.getHSBColor(
                    53f / 360f,
                    0.672f,
                    0.898f
            ),
            Color.getHSBColor(
                    1f,
                    0.742f,
                    0.867f
            ),
            Color.getHSBColor(
                    149f / 360f,
                    0.724f,
                    0.612f
            ),
            Color.getHSBColor(
                    349f / 360f,
                    0.582f,
                    0.216f
            )
    };

    private final int colorIndex;
    private final int playerID;

    private Point coords;

    private int sumoLevel;
    private int numberCasesPlayable;

    public Pawn(int colorIndex, int playerID, GameStageModel gameStageModel, int x, int y) {
        super(gameStageModel);

        ElementTypes.register("pawn", PAWN_ELEMENT_ID);

        this.type = ElementTypes.getType("pawn");

        this.colorIndex = colorIndex;
        this.playerID = playerID;
        this.sumoLevel = 0;

        this.numberCasesPlayable = 7;

        this.coords = new Point(x, y);
        this.setLocation((x + 1) * PAWN_SIZE - 2, (y + 1) * PAWN_SIZE + 7);

    }

    public Point getCoords(){
        return coords;
    }
    public void setCoords(Point coords){
        System.out.println("Coordinates defined to " + coords);
        this.coords = coords;
    }

    public int getColorIndex(){
        return colorIndex;
    }

    public int getSumoLevel(){
        return sumoLevel;
    }
    public void increaseSumoLevel(GameController controller){
        sumoLevel++;

        this.numberCasesPlayable = 7 - 2 * this.sumoLevel;

        PawnLook pawnLook = (PawnLook) controller.getElementLook(this);
        pawnLook.sumoLevelText.setText(sumoLevel + "");
    }
    public void resetSumoLevel(){
        sumoLevel = 0;
    }

    public int getNumberCasesPlayable(){
        return numberCasesPlayable;
    }

    /**
     * Return the chinese
     * @return The corresponding pin-yang
     */
    public String getChinesePawnName(){
        return switch (this.getColorIndex()) {
            case 0 -> "褐";
            case 1 -> "青";
            case 2 -> "赤";
            case 3 -> "黄";
            case 4 -> "红";
            case 5 -> "紫";
            case 6 -> "蓝";
            case 7 -> "橙";
            default -> "?";
        };
    }

    public int getPlayerID(){
        return playerID;
    }

    @Override
    public void update() {
        if (animation != null) {
            AnimationStep step = animation.next();
            if (step == null) {
                animation = null;
            }
            else if (step == Animation.NOPStep) {
                Logger.debug("nothing to do", this);
            }
            else {
                Logger.debug("move animation", this);
                setLocation(step.getInt(0), step.getInt(1));
            }
        }
    }

    @Override
    public String toString(){
        return "Pawn { colorIndex: " + colorIndex
                + ", playerNumber: " + playerID
                + ", coords: (" + coords.x + ", " + coords.y  + ")"
                + ", sumoLevel: " + sumoLevel + " }";
    }
}
