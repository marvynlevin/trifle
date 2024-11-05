package trifleGraphic.view;

import javafx.scene.paint.Color;
import trifleGraphic.boardifierGraphic.model.ContainerElement;
import trifleGraphic.boardifierGraphic.view.ClassicBoardLook;

public class TrifleBoardLook extends ClassicBoardLook {
    public static final int PAWN_SIZE = 60;

    public TrifleBoardLook(ContainerElement element) {
        super(
                PAWN_SIZE,
                element,
                -1,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                0,
                Color.TRANSPARENT,
                5,
                Color.TRANSPARENT,
                true
        );
    }
}
