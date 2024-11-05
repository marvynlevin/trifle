package minmax;

import java.awt.*;

public class Pawn {
    private Point coords;
    private final int playerID;
    private final int colorIndex;

    public Pawn(Point coords, int playerID, int colorIndex) {
        this.coords = coords;
        this.playerID = playerID;
        this.colorIndex = colorIndex;
    }

    public Pawn(trifleConsole.model.Pawn pawn) {
        this(
                new Point(pawn.getCoords().y, pawn.getCoords().x),
                pawn.getPlayerNumber(),
                pawn.getColorIndex()
        );
    }

    public Pawn(trifleGraphic.model.Pawn pawn) {
        this(
                new Point(pawn.getCoords().y, pawn.getCoords().x),
                pawn.getPlayerID(),
                pawn.getColorIndex()
        );
    }

    public Point getCoords() {
        return coords;
    }

    public void setCoords(Point coords) {
        this.coords = coords;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    @Override
    public String toString() {
        return "Pawn { playerID: " + this.playerID + ", colorIndex: " + this.colorIndex + ", coords: " + this.coords + " }";
    }
}
