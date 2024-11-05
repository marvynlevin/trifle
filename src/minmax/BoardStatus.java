package minmax;

import trifleConsole.model.TrifleBoard;
import trifleConsole.model.TrifleStageModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardStatus {
    private final int[][] matrix;
    private final List<Pawn> bluePawns;
    private final List<Pawn> cyanPawns;

    private Tracker tracker;

    private Point lastBlueMove;
    private Point lastCyanMove;

    public BoardStatus(
            List<trifleConsole.model.Pawn> bluePawns,
            List<trifleConsole.model.Pawn> cyanPawns,
            TrifleBoard board,
            TrifleStageModel trifleStageModel
    )
    {
        this.bluePawns = bluePawns.stream().map(Pawn::new).toList();
        this.cyanPawns = cyanPawns.stream().map(Pawn::new).toList();

        if (trifleStageModel.getLastBluePlayerMove() != null) {
            this.lastBlueMove = new Point(
                    trifleStageModel.getLastBluePlayerMove().y,
                    trifleStageModel.getLastBluePlayerMove().x
            );
        }

        if (trifleStageModel.getLastCyanPlayerMove() != null) {
            this.lastCyanMove = new Point(
                    trifleStageModel.getLastCyanPlayerMove().y,
                    trifleStageModel.getLastCyanPlayerMove().x
            );
        }

        this.matrix = new int[8][8];
        for (Pawn bluePawn: this.bluePawns) {
            this.matrix[bluePawn.getCoords().x][bluePawn.getCoords().y] = 1;
        }

        for (Pawn cyanPawn: this.cyanPawns) {
            this.matrix[cyanPawn.getCoords().x][cyanPawn.getCoords().y] = 2;
        }
    }

    public BoardStatus(List<trifleGraphic.model.Pawn> bluePlayer, List<trifleGraphic.model.Pawn> cyanPlayer, trifleGraphic.model.TrifleBoard board, trifleGraphic.model.TrifleStageModel stageModel) {
        this.bluePawns = bluePlayer.stream().map(Pawn::new).toList();
        this.cyanPawns = cyanPlayer.stream().map(Pawn::new).toList();

        if (stageModel.getLastBluePlayerMove() != null) {
            this.lastBlueMove = new Point(
                    stageModel.getLastBluePlayerMove().y,
                    stageModel.getLastBluePlayerMove().x
            );
        }

        if (stageModel.getLastCyanPlayerMove() != null) {
            this.lastCyanMove = new Point(
                    stageModel.getLastCyanPlayerMove().y,
                    stageModel.getLastCyanPlayerMove().x
            );
        }

        this.matrix = new int[8][8];
        for (Pawn bluePawn: this.bluePawns) {
            this.matrix[bluePawn.getCoords().x][bluePawn.getCoords().y] = 1;
        }

        for (Pawn cyanPawn: this.cyanPawns) {
            this.matrix[cyanPawn.getCoords().x][cyanPawn.getCoords().y] = 2;
        }
    }

    public Point getLastBlueMove() {
        return this.lastBlueMove;
    }
    public Point getLastCyanMove() {
        return this.lastCyanMove;
    }
    public Point getLastMove(int playerID){
        if (playerID == 0) return getLastBlueMove();
        else return getLastCyanMove();
    }

    public Tracker getTracker(){    
        return this.tracker;
    }
    public void setTracker(Tracker tracker){
        this.tracker = tracker;
    }

    public void setLastMove(int playerID, Point move){
        if (move == null)
            return;

        if (playerID == 0) this.lastBlueMove = (Point) move.clone();
        else this.lastCyanMove = (Point) move.clone();
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public boolean isStartOfGame(){
        for (Pawn bluePawn: bluePawns) {
            if (bluePawn.getCoords().x != 0)
                return false;
        }
        for (Pawn cyanPawn: cyanPawns) {
            if (cyanPawn.getCoords().x != 7)
                return false;
        }

        return true;
    }

    public List<Pawn> getPawns(int playerID) {
        return playerID == 0 ? this.bluePawns : this.cyanPawns;
    }

    public Pawn getPawn(int playerID, int colorIndex) {
        if (playerID == 0) {
            for (Pawn bluePawn: this.bluePawns) {
                if (bluePawn.getColorIndex() == colorIndex)
                    return bluePawn;
            }
        } else {
            for (Pawn cyanPawn: this.cyanPawns) {
                if (cyanPawn.getColorIndex() == colorIndex)
                    return cyanPawn;
            }
        }

        return null;
    }

    public void movePawn(int playerID, int colorIndex, Point move) {
        Pawn pawn = this.getPawn(playerID, colorIndex);
        if (pawn != null)
            pawn.setCoords((Point) move.clone());
    }

    public boolean isPointOccupied(Point p) {
        return isPointOccupied(p.x, p.y);
    }
    public boolean isPointOccupied(int x, int y) {
        return this.matrix[x][y] != 0;
    }

    public boolean isWin(){
        return bluePawns.stream().anyMatch(p -> p.getCoords().x == 7)
                || cyanPawns.stream().anyMatch(p -> p.getCoords().x == 0);
    }

    public List<Point> getPossibleMoves(int playerID, Point p) {
        List<Point> possibleMoves = new ArrayList<>();

        if (playerID == 0) {
            // horizontally
            for (int x = p.x + 1; x < 7; x++) {
                if (isPointOccupied(x, p.y)) break;
                possibleMoves.add(new Point(x, p.y));
            }

            int x = p.x, y = p.y;

            // right diagonal
            while (x < 7 && y < 7) {
                x++;
                y++;

                if (isPointOccupied(x, y)) break;
                possibleMoves.add(new Point(x, y));
            }

            x = p.x;
            y = p.y;

            // left diagonal
            while (y > 0 && x < 7) {
                y--;
                x++;

                if (isPointOccupied(x, y)) break;
                possibleMoves.add(new Point(x, y));
            }

        } else {
            // horizontally
            for (int x = p.x - 1; x > 0; x--) {
                if (isPointOccupied(x, p.y)) break;
                possibleMoves.add(new Point(x, p.y));
            }

            int x = p.x, y = p.y;

            // right diagonal
            while (x > 0 && y < 7) {
                x--;
                y++;
                if (isPointOccupied(x, y)) break;
                possibleMoves.add(new Point(x, y));
            }

            x = p.x;
            y = p.y;

            // left diagonal
            while (x > 0 && y > 0) {
                x--;
                y--;

                if (isPointOccupied(x, y)) break;
                possibleMoves.add(new Point(x, y));
            }
        }

        return possibleMoves;
    }
}
