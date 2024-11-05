package minmax;

import trifleConsole.model.TrifleBoard;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Node {
    private Pawn pawnInvolved;
    private Point moveDone;
    private int currentPlayerID;
    private double weight;

    private final List<Node> children = new ArrayList<>();

    public Node(Pawn pawnInvolved, Point moveDone, int currentPlayerID) {
        this.pawnInvolved = pawnInvolved;
        this.moveDone = moveDone;
        this.currentPlayerID = currentPlayerID;
    }
    public Node(){}

    public double getWeight(){
        return weight;
    }

    public int getCurrentPlayerID() {
        return currentPlayerID;
    }
    public Pawn getPawnInvolved() {
        return pawnInvolved;
    }
    public Point getMoveDone() {
        return moveDone;
    }

    private List<Pawn> getAllowedPawns(BoardStatus boardStatus) {
        List<Pawn> allowedPawns = new ArrayList<>();

        if (boardStatus.isStartOfGame() || (boardStatus.getLastBlueMove() == null && boardStatus.getLastCyanMove() == null)) {
            List<Pawn> pawns = boardStatus.getPawns(currentPlayerID);
            return List.of(pawns.get((int) Math.floor(Math.random() * pawns.size())));
//            allowedPawns.addAll(boardStatus.getPawns(currentPlayerID));
        } else {
            // YEET
            Point backgroundLastMoveCoords = boardStatus.getLastMove((currentPlayerID + 1) % 2);

            int backgroundColorIndex = TrifleBoard.BOARD[backgroundLastMoveCoords.x][backgroundLastMoveCoords.y];

            // get the pawn
            Pawn pawn = boardStatus.getPawn(currentPlayerID, backgroundColorIndex);
            allowedPawns.add(pawn);
        }

        return allowedPawns;
    }

    private void calculateWeight(BoardStatus boardStatus, int currentDepth) {
        long start = System.nanoTime();
        this.weight = DeterministicAlgorithm.determineWeight(
                boardStatus,
                currentPlayerID,
                pawnInvolved,
                moveDone,
                currentDepth
        );
        long end = System.nanoTime();
        boardStatus.getTracker().newWeightTime(end - start);
    }

    public void buildRoot(BoardStatus boardStatus, int botID, int depth) {
        this.currentPlayerID = botID;
        List<Pawn> allowedPawns = getAllowedPawns(boardStatus);

        for (Pawn pawn : allowedPawns) {
            List<Point> possibleMoves = boardStatus.getPossibleMoves(currentPlayerID, pawn.getCoords());

            for (Point move: possibleMoves) {
                Node node = new Node(pawn, move, currentPlayerID);
                node.buildTree(boardStatus, depth);

                this.children.add(node);
            }
        }
    }

    private void buildTree(BoardStatus boardStatus, int depth) {
        if (depth < 1 || boardStatus.isWin()) {
            this.calculateWeight(boardStatus, depth);
            return;
        }

        boardStatus.getTracker().newNode(depth);

        boardStatus.movePawn(
                getCurrentPlayerID(),
                getPawnInvolved().getColorIndex(),
                moveDone
        );
        Point oldLastMove = boardStatus.getLastMove(currentPlayerID) == null ?
                null : (Point) boardStatus.getLastMove(currentPlayerID).clone();
        boardStatus.setLastMove(currentPlayerID, moveDone);

        List<Point> possibleMoves = boardStatus.getPossibleMoves(currentPlayerID, getPawnInvolved().getCoords());
        if (possibleMoves.isEmpty()) {
            this.calculateWeight(boardStatus, depth);
        }

        for (Point move: possibleMoves) {
            Node node = new Node(pawnInvolved, move, (currentPlayerID + 1) % 2);
            node.buildTree(boardStatus, depth - 1);

            this.children.add(node);
        }


        boardStatus.setLastMove(currentPlayerID, oldLastMove);
        // At the end
        boardStatus.movePawn(
                getCurrentPlayerID(),
                getPawnInvolved().getColorIndex(),
                getPawnInvolved().getCoords()
        );
    }

    public Node minimax(int botID){
        if (children.isEmpty())
            return null;

        Node betterNode = null;

        for (Node child : children) {
            if (betterNode == null) {
                betterNode = child;
                continue;
            }

            double evaluated = child.minimaxInternal(botID);
            if (betterNode.getWeight() < evaluated) {
                betterNode = child;
            }
        }

//        betterNode.getMoveDone().x = 7 - betterNode.getMoveDone().x;

        // reverse x and y?
        int oldX = betterNode.getMoveDone().x;
        betterNode.getMoveDone().x = betterNode.getMoveDone().y;
        betterNode.getMoveDone().y = oldX;

        return betterNode;
    }

    private double minimaxInternal(int botID) {
        if (this.children.isEmpty())
            return this.getWeight();

        if (isMaximisingPlayer(botID)){
            double maxEval = Integer.MIN_VALUE;

            for (Node child: this.children) {
                double evaluated = child.minimaxInternal(botID);
                maxEval = Math.max(maxEval, evaluated);
            }

            return maxEval;
        } else {
            double minEval = Integer.MAX_VALUE;

            for (Node child: this.children) {
                double evaluated = child.minimaxInternal(botID);
                minEval = Math.min(minEval, evaluated);
            }

            return minEval;
        }
    }

    private boolean isMaximisingPlayer(int botID){
        return botID == getCurrentPlayerID();
    }

    @Override
    public String toString(){
        return "Node { weight: " + weight
                + ", currentPlayerID: " + currentPlayerID
                + ", pawnInvolved: " + pawnInvolved
                + ", moveDone: " + moveDone + " }";
    }
}
