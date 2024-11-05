package trifleGraphic.model;

import rules.GameMode;
import rules.PlayerMode;
import trifleGraphic.Trifle;
import trifleGraphic.boardifierGraphic.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static trifleGraphic.view.TrifleStageView.BOARD_WIDTH;

public class TrifleStageModel extends GameStageModel {
    TrifleBoard board;
    private List<Pawn> bluePlayer;
    private List<Pawn> cyanPlayer;

    private final List<BackgroundCell> backgroundCells;

    private Point lastBluePlayerMove;
    private Point lastCyanPlayerMove;

    private TextElement playerName;
    private TextElement roundCounter;
    private TextElement playerPoints;

    private GameMode gameMode     = GameMode.defaultValue();
    private PlayerMode playerMode = PlayerMode.defaultValue();

    private final List<TextElement> movesHistory;
    private final List<OldMove> oldMovesList;

    private boolean isBluePlayerBlocked = false;
    private boolean isCyanPlayerBlocked = false;

    private int state;

    // STATES
    public final static int SELECT_PAWN_STATE = 1;
    public final static int SELECT_DEST_STATE = 2;


    /*
     *
     *
     *   ====== METHODS ======
     *
     *
     */
    public TrifleStageModel(String name, Model model) {
        super(name, model);

        this.bluePlayer      = new ArrayList<>();
        this.cyanPlayer      = new ArrayList<>();
        this.backgroundCells = new ArrayList<>();
        this.oldMovesList    = new ArrayList<>();
        this.movesHistory    = new ArrayList<>();

        this.state = SELECT_PAWN_STATE;
    }

    public StageElementsFactory getDefaultElementFactory() {
        return new TrifleStageFactory(this);
    }

    public ContainerElement getBoard() {
        return board;
    }
    public void setBoard(TrifleBoard board) {
        this.board = board;
        addContainer(board);
    }

    public int getState(){
        return this.state;
    }
    public void setState(int state){
        this.state = state;
    }

    public boolean isBluePlayerBlocked() {
        return isBluePlayerBlocked;
    }
    public void setBluePlayerBlocked(boolean isBluePlayerBlocked) {
        this.isBluePlayerBlocked = isBluePlayerBlocked;
    }

    public boolean isCyanPlayerBlocked() {
        return isCyanPlayerBlocked;
    }
    public void setCyanPlayerBlocked(boolean isCyanPlayerBlocked) {
        this.isCyanPlayerBlocked = isCyanPlayerBlocked;
    }

    public boolean isPlayerBlocked(int playerID){
        if (playerID == 0) return isBluePlayerBlocked();
        else return isCyanPlayerBlocked();
    }
    public void setPlayerBlocked(int playerID, boolean blocked){
        if (playerID == 0) setBluePlayerBlocked(blocked);
        else setCyanPlayerBlocked(blocked);
    }

    public List<Pawn> getBluePlayer() { return this.bluePlayer; }
    public void setBluePawns(List<Pawn> bluePawns) {
        this.bluePlayer = bluePawns;
        for (Pawn pawn: bluePawns) addPawnToBoard(pawn);
    }

    public List<Pawn> getCyanPlayer() { return this.cyanPlayer; }
    public void setCyanPawns(List<Pawn> cyanPawns) {
        this.cyanPlayer = cyanPawns;
        for (Pawn pawn: cyanPawns) addPawnToBoard(pawn);
    }

    private void addPawnToBoard(Pawn pawn) {
        this.board.addElement(pawn, pawn.getCoords().y, pawn.getCoords().x);
        addElement(pawn);
    }

    public void addBackgroundCell(BackgroundCell backgroundCell) {
        this.backgroundCells.add(backgroundCell);
        addElement(backgroundCell);
    }
    public List<BackgroundCell> getBackgroundCells() {
        return this.backgroundCells;
    }

    public Point getLastBluePlayerMove() { return this.lastBluePlayerMove; }
    public void setLastBluePlayerMove(Point lastBluePlayerMove) {
        this.lastBluePlayerMove = lastBluePlayerMove;
    }

    public Point getLastCyanPlayerMove() { return this.lastCyanPlayerMove; }
    public void setLastCyanPlayerMove(Point lastCyanPlayerMove) {
        this.lastCyanPlayerMove = lastCyanPlayerMove;
    }

    public Point getLastPlayerMove(int playerID){
        return playerID == 0 ? getLastBluePlayerMove() : getLastCyanPlayerMove();
    }

    public List<Pawn> getPlayerPawns(int playerID) {
        if (playerID == 0) return getBluePlayer();
        else return getCyanPlayer();
    }
    public Pawn getPlayerPawn(int playerID, int pawnID) {
        List<Pawn> pawns = getPlayerPawns(playerID);

        for (Pawn pawn: pawns) {
            if (pawn.getColorIndex() == pawnID) return pawn;
        }
        return null;
    }

    public TextElement getPlayerName() {
        return this.playerName;
    }
    public void setPlayerName(TextElement playerName) {
        this.playerName = playerName;
        addElement(playerName);
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public PlayerMode getPlayerMode(){
        return this.playerMode;
    }
    public void setPlayerMode(PlayerMode playerMode){
        this.playerMode = playerMode;
    }

    public TextElement getRoundCounter() {
        return this.roundCounter;
    }
    public void setRoundCounter(TextElement roundCounter) {
        this.roundCounter = roundCounter;
        addElement(roundCounter);
    }

    public TextElement getPlayerPoints() {
        return playerPoints;
    }
    public void setPlayerPoints(TextElement playerPoints) {
        this.playerPoints = playerPoints;
        addElement(playerPoints);
    }

    /**
     * Create or update the TextElement which store the number of points for each player
     */
    public void updatePlayerPoints(int bluePlayerPoints, int cyanPlayerPoints) {
        String text = model.getPlayers().get(0).getName() + ": " + bluePlayerPoints
                + "   " + model.getPlayers().get(1).getName() + ": " + cyanPlayerPoints;

        if (this.getPlayerPoints() == null) {
            TextElement playerPointsCounter = new TextElement(text, this);
            playerPointsCounter.setLocation(BOARD_WIDTH, 94);
            this.setPlayerPoints(playerPointsCounter);
        }
        else {
            // update the text
            this.getPlayerPoints().setText(text);
        }
    }

    public List<TextElement> getMovesHistory() {
        return this.movesHistory;
    }
    public List<OldMove> getOldMovesList(){
        return this.oldMovesList;
    }
    public void addOldMove(OldMove move){
        this.oldMovesList.add(move);
    }

    public static final int MAX_HISTORY_SIZE = 14;

    public void updateHistory(){
        if (this.movesHistory.isEmpty())
            return;

        // 13 -> 12, 12 -> 11, ...
        for (int i = 0; i < MAX_HISTORY_SIZE - 1; i++) {
            TextElement thisText = this.movesHistory.get(i);
            thisText.setText(this.movesHistory.get(i + 1).getText());
        }

        TextElement newMove = this.movesHistory.get(MAX_HISTORY_SIZE - 1);
        newMove.setText(oldMovesList.get(oldMovesList.size() - 1).toString());
    }

}
