package main.java.com.iceteaviet.chess.core.player;

import main.java.com.iceteaviet.chess.core.board.Board;
import main.java.com.iceteaviet.chess.core.board.Move;

/**
 * Created by MyPC on 5/12/2017.
 */
public class MoveTransition {

    private final Board fromBoard;
    private final Board toBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board transitionBoard, final Move move, final MoveStatus moveStatus) {
        this(transitionBoard, transitionBoard, move, moveStatus);
    }

    public MoveTransition(final Board fromBoard,
                          final Board toBoard,
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

    public Board getFromBoard() {
        return this.fromBoard;
    }

    public Board getTransitionBoard() {
        return this.toBoard;
    }
}
