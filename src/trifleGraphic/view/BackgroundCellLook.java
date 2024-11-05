package trifleGraphic.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import trifleGraphic.boardifierGraphic.model.GameElement;
import trifleGraphic.boardifierGraphic.view.ElementLook;
import trifleGraphic.model.BackgroundCell;
import trifleGraphic.model.Pawn;

import static trifleGraphic.view.TrifleBoardLook.PAWN_SIZE;


public class BackgroundCellLook extends ElementLook {
    public BackgroundCellLook(GameElement element) {
        super(element);
        render();
    }

    @Override
    public void onSelectionChange(){
        System.out.println("Selection change");
    }

    @Override
    public void onFaceChange(){
        System.out.println("Face change");
    }

    @Override
    public void onVisibilityChange(){
        System.out.println("Visibility change");
    }

    protected void render(){
        Rectangle rectangle = getRectangle();
        addShape(rectangle);

    }

    private Rectangle getRectangle() {
        BackgroundCell bc = (BackgroundCell) element;

        Rectangle rectangle = new Rectangle(PAWN_SIZE, PAWN_SIZE);
        rectangle.setX(rectangle.getX() - ((double) PAWN_SIZE / 2));
        rectangle.setY(rectangle.getY() - ((double) PAWN_SIZE / 2));

        java.awt.Color bcColor = Pawn.COLORS[bc.getColorIndex()];

        Color convertedColor = Color.color(
                bcColor.getRed() / 255.0,
                bcColor.getGreen() / 255.0,
                bcColor.getBlue() / 255.0
        );

        rectangle.setFill(convertedColor);
        return rectangle;
    }
}
