package minmax;

public class MinMax {
    /**
     * The move done by the opponent
     */
    private Node root;
    private final Tracker tracker;

    public static int DEPTH = 50;

    public MinMax() {
        this.root = new Node();
        this.tracker = new Tracker();
    }

    public void buildTree(BoardStatus boardStatus, int botID) {
        this.tracker.reset();
        boardStatus.setTracker(this.tracker);

        this.tracker.startTreeBuilderTimer();
        this.root.buildRoot(boardStatus, botID, DEPTH);
        this.tracker.endTreeBuilderTimer();
    }

    public Node minimax(int botID){
        this.tracker.startFindPathTimer();
        Node result = this.root.minimax(botID);
        this.tracker.endFindPathTimer();
        return result;
    }

    public void reset(){
        this.root = new Node();
    }

    public Tracker getTracker() {
        return this.tracker;
    }
}
