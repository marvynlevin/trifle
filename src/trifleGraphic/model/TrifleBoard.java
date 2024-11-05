package trifleGraphic.model;

import trifleGraphic.boardifierGraphic.control.Logger;
import trifleGraphic.boardifierGraphic.model.ContainerElement;
import trifleGraphic.boardifierGraphic.model.GameStageModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TrifleBoard extends ContainerElement {
    public static final String BOARD_ID = "trifle_board";

    public static final int[][] BOARD = {
            {0, 1, 2, 3, 4, 5, 6, 7},
            {5, 0, 3, 6, 1, 4, 7, 2},
            {6, 3, 0, 5, 2, 7, 4, 1},
            {3, 2, 1, 0, 7, 6, 5, 4},
            {4, 5, 6, 7, 0, 1, 2, 3},
            {1, 4, 7, 2, 5, 0, 3, 6},
            {2, 7, 4, 1, 6, 3, 0, 5},
            {7, 6, 5, 4, 3, 2, 1, 0}
    };

    public int opponentVerticalCounter = 0;
    public int counterCases = 0;

    public TrifleBoard(int x, int y, GameStageModel gameStageModel) {
        super(BOARD_ID, x, y, 8, 8, gameStageModel);
        resetReachableCells(false);
    }

    public void setValidCells(Point coordinates, int playerId, int sumoLevel) {
        Logger.debug("setting valid cells :D", this);
        resetReachableCells(false);

        List<Point> validCells = this.computeValidCells(coordinates, playerId, sumoLevel);
        if (validCells != null) {
            System.out.println("called setValidCells");
            for (Point p: validCells) {
                reachableCells[p.y][p.x] = true;
            }
        }
    }

    public boolean canPawnMove(Pawn pawn, int playerID){
        Point c = pawn.getCoords();
        if (playerID == 0) {
            return (c.y < 7 && getElement(c.y + 1, c.x) == null)
                    || (c.y < 7 && c.x < 7 && getElement(c.y + 1, c.x + 1) == null)
                    || (c.y < 7 && c.x > 0 && getElement(c.y + 1, c.x - 1) == null);
        }
        else {
            return (c.x > 0 && getElement(c.x - 1, c.y) == null)
                    || (c.y > 0 && c.x < 7 && getElement(c.y - 1, c.x + 1) == null)
                    || (c.y > 0 && c.x > 0 && getElement(c.y - 1, c.x - 1) == null);
        }
    }

    /**
     * @param coords the coordinates of the move wanted
     * @param playerId The current player ID
     * @return The list of allowed moves
     */
    public List<Point> computeValidCells(Point coords, int playerId, int sumoLevel) {
        List<Point> validCells = new ArrayList<>();
        Pawn p = (Pawn) getElement(coords.y, coords.x);

        if (p == null)
            return validCells;

        this.opponentVerticalCounter = 0;
        this.counterCases = 1;

        boolean withoutOpponent = true;
        boolean canOshi;

        if (playerId == 0) {
            // player is based on the top and must go to the bottom

            canOshi = coords.y < 7 && getElement(coords.y + 1, coords.x) != null;

            // check on the vertical
            vertical: for (int y = coords.y + 1; y <= 7; y++) {
                // number of possible moves
                if (p.getNumberCasesPlayable() < counterCases) break vertical;

                // normal valid cell
                if ((getElement(y, coords.x) == null) && withoutOpponent) {
                    validCells.add(new Point(coords.x, y));
                    counterCases++;
                }
                // detect opponent pawn
                if (getElement(y, coords.x) != null && withoutOpponent) {
                    withoutOpponent = false;
                }

                // oshi if possible
                if (!withoutOpponent && canOshi) {
                    Pawn cellPawn = (Pawn) getElement(coords.x, y);

                    if (cellPawn != null && p.getSumoLevel() < cellPawn.getSumoLevel()) break vertical;
                    else if (cellPawn != null && cellPawn.getPlayerID() == p.getPlayerID()) break vertical;
                    else if (cellPawn != null && sumoLevel < opponentVerticalCounter) break vertical;
                    else if (getElement(y, coords.x) == null) {
                        validCells.add(new Point(coords.x, y - opponentVerticalCounter));
                        break vertical;
                    } else opponentVerticalCounter++;
                }
            }

            // check on the right diagonal
            int x = coords.x, y = coords.y;
            this.counterCases = 1;

            while (x < 7 && y < 7) {
                if (p.getNumberCasesPlayable() < counterCases) break;
                x++;
                y++;
                if (getElement(y, x) == null) {
                    validCells.add(new Point(x, y));
                    this.counterCases++;
                } else break;
            }

            // now reset x and y, and do the left diagonal
            x = coords.x; y = coords.y;
            this.counterCases = 1;

            while (x > 0 && y < 7) {
                if (p.getNumberCasesPlayable() < counterCases) break;
                x--;
                y++;
                if (getElement(y, x) == null) {
                    validCells.add(new Point(x, y));
                    this.counterCases++;
                } else break;
            }

            // Done.
        }
        else {
            // player is based on the bottom and must go to the top

            canOshi = coords.y < 7 && getElement(coords.y + 1, coords.x) != null;

            // The same as the upper code, but with inverse conditions
            // edited line will be commented with `+` after

            vertical: for (int y = coords.y - 1; y >= 0; y--) {
                // number of possible moves
                if (p.getNumberCasesPlayable() < counterCases) break ;

                // normal valid cell
                if ((getElement(y, coords.x) == null) && withoutOpponent) {
                    validCells.add(new Point(coords.x, y));
                    this.counterCases++;
                }

                // detect opponent pawn
                if (getElement(y, coords.x) != null && withoutOpponent) {
                    withoutOpponent = false;
                }

                // oshi if possible
                if (!withoutOpponent && canOshi) {
                    Pawn cellPawn = (Pawn) getElement(coords.x, y);


                    if (cellPawn != null && p.getSumoLevel() < cellPawn.getSumoLevel()) break vertical;
                    else if (cellPawn != null && cellPawn.getPlayerID() == p.getPlayerID()) break vertical;
                    else if (cellPawn != null && sumoLevel < this.opponentVerticalCounter) break vertical;
                    else if (getElement(y, coords.x) == null) {
                        validCells.add(new Point(coords.x, y + this.opponentVerticalCounter));
                        break vertical;
                    } else this.opponentVerticalCounter++;
                }
            }


            // check on the right diagonal
            int x = coords.x, y = coords.y;
            this.counterCases = 1;

            while (x < 7 && y > 0) {
                if (p.getNumberCasesPlayable() < counterCases) break;
                x++;
                y--; // +
                if (getElement(y, x) == null) {
                    validCells.add(new Point(x, y));
                    this.counterCases++;
                } else break;
            }

            // now reset x and y, and do the left diagonal
            x = coords.x; y = coords.y;
            this.counterCases = 1;

            while (x > 0 && y > 0) { // +
                if (p.getNumberCasesPlayable() < counterCases) break;
                x--;
                y--; // +
                if (getElement(y, x) == null) {
                    validCells.add(new Point(x, y));
                    this.counterCases++;
                } else break;
            }
        }
        return validCells;
    }

    public int getWidth() {
        int result = 0;
        for (int[] row : BOARD) {
            result = Math.max(result, row.length);
        }
        return  result;
    }

    public int getHeight() {
        int result = 0;
        for (int[] row : BOARD) {
            result++;
        }
        return result;
    }
}
