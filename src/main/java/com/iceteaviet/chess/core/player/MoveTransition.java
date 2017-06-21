package main.java.com.iceteaviet.chess.core.player;

import main.java.com.iceteaviet.chess.core.board.GameBoard;

/**
 * MoveTransition is represent for a movement transition
 *
 * @see Move
 * <p>
 * Created by MyPC on 5/12/2017.
 */
public class MoveTransition {

    private final GameBoard fromBoard;
    private final GameBoard toBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final GameBoard transitionBoard, final Move move, final MoveStatus moveStatus) {
        this(transitionBoard, transitionBoard, move, moveStatus);
    }

    public MoveTransition(final GameBoard fromBoard,
                          final GameBoard toBoard,
                          final Move transitionMove,
                          final MoveStatus moveStatus) {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.move = transitionMove;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public GameBoard getFromBoard() {
        return this.fromBoard;
    }

    public GameBoard getTransitionBoard() {
        return this.toBoard;
    }
}
