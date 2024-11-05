package minmax;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.Optional;
import java.util.Random;

/**
 * A "simple" algorithm that can calculate a weight based on the move
 */
public class DeterministicAlgorithm {
    private static final Random random = new Random();

    private static final double BLUE_PLAYER_DEF_COEFF = random.nextDouble(0, 1);
    private static final double CYAN_PLAYER_DEF_COEFF = random.nextDouble(0, 1);

    /**
     * Determine if whether the move is good or bad
     * <br>
     * The weight can be from 100 and -100, where 100 is a VERY GOOD move, and -100 the worst decision of your life
     * @param boardStatus The status of the board at this point
     * @param playerID The player's id
     * @param pawn The pawn who we want to move
     * @param move The move that will be calculated
     * @return The calculated weight between [100; -100]
     */
    public static double determineWeight(
            BoardStatus boardStatus,
            int playerID,
            Pawn pawn,
            Point move,
            int depth
    )
    {
        if (boardStatus.isWin())
            return (100 * (MinMax.DEPTH / (double) depth)) + defensive(boardStatus, playerID, pawn, move, depth);

        return defensive(boardStatus, playerID, pawn, move, depth) + aggressive(boardStatus, playerID, pawn, move, depth);
    }

    private static double aggressive(
            BoardStatus boardStatus,
            int playerID,
            Pawn pawn,
            Point move,
            int depth
    )
    {
        double offensiveWeight = 0.0;

        boolean canWin = false;
        List<Point> allowedMoves = boardStatus.getPossibleMoves(playerID, move);
        for (Point p: allowedMoves) {
            if ((playerID == 0 && p.x == 7) || (playerID == 1 && p.x == 0)) {
                offensiveWeight += 50;
                canWin = true;
            }
        }

        // privilege 4 and 5 in X
        if (!canWin) {
            if (move.x == 3 || move.x == 4)
                offensiveWeight += 10;
        }

        offensiveWeight *= playerID == 0 ? (1 - BLUE_PLAYER_DEF_COEFF) : (1 - CYAN_PLAYER_DEF_COEFF);
        return offensiveWeight;
    }

    private static double defensive(
            BoardStatus boardStatus,
            int playerID,
            Pawn pawn,
            Point move,
            int depth
    )
    {
        // offensive
        double defensiveWeight = 0.0;


        // block the pawns of the opponent
        for (Pawn opponentPawn: boardStatus.getPawns((playerID + 1) % 2)) {
            List<Point> possibleMoves = boardStatus.getPossibleMoves(
                    (playerID + 1) % 2,
                    opponentPawn.getCoords()
            );

            boolean canWin = possibleMoves.stream()
                    .anyMatch(m -> playerID == 0 ? m.x == 0 : m.x == 7);

            for (Point possibleMove: possibleMoves) {
                if (possibleMove.x == move.x && possibleMove.y == move.y)
                    defensiveWeight += canWin ? 50 : 25;
            }
        }


        // detect bot pawns that will be blocked by this move
        List<Pawn> pawnBlocked = getPawnsInTrajectory(
                boardStatus,
                move,
                (playerID + 1) % 2,
                boardStatus.getPawns(playerID)
        );

        if (!pawnBlocked.isEmpty())
            defensiveWeight /=  (pawnBlocked.size() / 5.0);

        defensiveWeight *= playerID == 0 ? BLUE_PLAYER_DEF_COEFF : CYAN_PLAYER_DEF_COEFF;

        return defensiveWeight;
    }

    private static List<Pawn> getPawnsInTrajectory(BoardStatus boardStatus, Point move, int playerID, List<Pawn> opponentPawns){
        List<Pawn> opponentPawnsInTrajectory = new ArrayList<>();

        if (playerID == 0) {
            for (int x = move.x + 1; x < 7; x++) {
                Optional<Pawn> maybe = isOpponentAt(opponentPawns, x, move.y);
                if (maybe.isPresent()) {
                    opponentPawnsInTrajectory.add(maybe.get());
                    break;
                }
            }

            int x = move.x, y = move.y;

            // right diagonal
            while (x < 7 && y < 7) {
                x++;
                y++;

                Optional<Pawn> maybe = isOpponentAt(opponentPawns, x, y);
                if (maybe.isPresent()) {
                    opponentPawnsInTrajectory.add(maybe.get());
                    break;
                }
            }

            x = move.x;
            y = move.y;

            // left diagonal
            while (y > 0 && x < 7) {
                y--;
                x++;

                Optional<Pawn> maybe = isOpponentAt(opponentPawns, x, y);
                if (maybe.isPresent()) {
                    opponentPawnsInTrajectory.add(maybe.get());
                    break;
                }
            }
        }
        else {
            for (int x = move.x - 1; x > 0; x--) {
                Optional<Pawn> maybe = isOpponentAt(opponentPawns, x, move.y);
                if (maybe.isPresent()) {
                    opponentPawnsInTrajectory.add(maybe.get());
                    break;
                }
            }

            int x = move.x, y = move.y;

            // right diagonal
            while (x > 0 && y < 7) {
                x--;
                y++;

                Optional<Pawn> maybe = isOpponentAt(opponentPawns, x, y);
                if (maybe.isPresent()) {
                    opponentPawnsInTrajectory.add(maybe.get());
                    break;
                }
            }

            x = move.x;
            y = move.y;

            // left diagonal
            while (x > 0 && y > 0) {
                x--;
                y--;

                Optional<Pawn> maybe = isOpponentAt(opponentPawns, x, y);
                if (maybe.isPresent()) {
                    opponentPawnsInTrajectory.add(maybe.get());
                    break;
                }
            }
        }

        return opponentPawnsInTrajectory;
    }

    private static Optional<Pawn> isOpponentAt(List<Pawn> opponentPawns, int x, int y) {
        for (Pawn op: opponentPawns) {
            if (op.getCoords().y == y && op.getCoords().x == x) {
                return Optional.of(op);
            }
        }
        return Optional.empty();
    }
}
