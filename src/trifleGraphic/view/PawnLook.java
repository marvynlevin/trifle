package trifleGraphic.view;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import trifleGraphic.boardifierGraphic.model.GameElement;
import trifleGraphic.boardifierGraphic.view.ElementLook;
import trifleGraphic.model.Pawn;


import static trifleGraphic.view.TrifleBoardLook.PAWN_SIZE;

public class PawnLook extends ElementLook {
    public static int RADIUS = (PAWN_SIZE / 2) - 2;

    public static int STROKE_WIDTH = 2;

    public Text sumoLevelText;

    public PawnLook(GameElement element) {
        super(element);
        render();
    }
    protected void render() {
        Pawn pawn = (Pawn) element;
        // TODO remplacer le cercle par une forme de tour
        Circle circle = new Circle();
        circle.setRadius(RADIUS);

        // Define the color
        java.awt.Color pawnColor = Pawn.COLORS[pawn.getColorIndex()];
        Color convertedColor = javafx.scene.paint.Color.color(
                pawnColor.getRed() / 255.0,
                pawnColor.getGreen() / 255.0,
                pawnColor.getBlue() / 255.0
        );
        circle.setFill(convertedColor);

        circle.setStrokeWidth(STROKE_WIDTH);
        circle.setStrokeType(StrokeType.CENTERED);
        circle.setStroke(Color.valueOf(pawn.getPlayerID() == 1 ? "0xfefefe" : "0x000"));

        addShape(circle);

        Text pawnName = new Text(pawn.getChinesePawnName());
        pawnName.setFont(new Font(24));

        this.sumoLevelText = new Text(pawn.getSumoLevel() + "");
        sumoLevelText.setFont(new Font(18));

        // Define the text color, white or black, depending on the color of the pawn
        if (whiteOrBlack(convertedColor) == 0) {
            pawnName.setFill(Color.WHITE);
            sumoLevelText.setFill(Color.WHITE);
        } else {
            pawnName.setFill(Color.BLACK);
            sumoLevelText.setFill(Color.BLACK);
        }

        Bounds bt = pawnName.getBoundsInLocal();
        pawnName.setX(-bt.getWidth()/2 - 6);
        pawnName.setY(pawnName.getBaselineOffset()/2-4);

        sumoLevelText.setX(-bt.getWidth()/2 + 18);
        sumoLevelText.setY(sumoLevelText.getBaselineOffset()/2 - 2);

        addShape(pawnName);
        addShape(sumoLevelText);
    }

    private static ImageView getImageView(Image levelImage, Circle circle) {
        ImageView levelImageView = new ImageView(levelImage);
        levelImageView.setFitWidth(RADIUS * 0.4);
        levelImageView.setFitHeight(RADIUS * 0.4);

        double centerX = circle.getCenterX();
        double centerY = circle.getCenterY();
        levelImageView.setLayoutX(centerX - levelImageView.getFitWidth() / 2);
        levelImageView.setLayoutY(centerY - levelImageView.getFitHeight() / 2);
        return levelImageView;
    }

    /**
     *
     * @param color The color in question
     * @return Zero for white, one for black
     */
    public static int whiteOrBlack(Color color) {
        double seuil = 0.75;

        Color adjustedColor = color.brighter();
        double lum = adjustedColor.getBrightness();

        return lum > seuil ? 1 : 0;
    }
}
