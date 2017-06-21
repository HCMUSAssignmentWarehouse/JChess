package main.java.com.iceteaviet.chess.core.player;

/**
 * MoveStatus is status of a movement after doing a transition
 *
 * @see MoveTransition
 * @see Move
 * <p>
 * Created by MyPC on 5/12/2017.
 */
public enum MoveStatus {
    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    },
    LEAVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    public abstract boolean isDone();
}
