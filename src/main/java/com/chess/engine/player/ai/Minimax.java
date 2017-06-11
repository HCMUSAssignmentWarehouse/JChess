package main.java.com.chess.engine.player.ai;

import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.Move;
import main.java.com.chess.engine.player.MoveTransition;

/**
 * Created by MyPC on 5/19/2017.
 */
public class Minimax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long boardsEvaluated;

    public Minimax(final int searchDepth) {
        this.searchDepth = searchDepth;
        this.boardEvaluator = new StandardBoardEvaluator();
    }

    private static boolean isEndGameScenario(final Board board) {
        return board.getCurrentPlayer().isInCheckMate() || board.getCurrentPlayer().isInStaleMate();
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board, int depth) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.getCurrentPlayer() + " THINKING with depth = " + depth);
        int numMoves = board.getCurrentPlayer().getLegalMoves().size();
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                currentValue = board.getCurrentPlayer().getAlliance().isWhite() ?
                        min(transition.getTransitionBoard(), depth - 1) :
                        max(transition.getTransitionBoard(), depth - 1);

                if (board.getCurrentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.getCurrentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executeTime = System.currentTimeMillis() - startTime;

        return bestMove;
    }

    public int min(final Board board, final int depth) {
        if (depth == 0 || isEndGameScenario(board)) { //game over
            return this.boardEvaluator.evaluate(board, depth);
        }

        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                final int currentValue = max(transition.getTransitionBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }

            }

        }
        return lowestSeenValue;
    }

    public int max(final Board board, final int depth) {
        if (depth == 0 || isEndGameScenario(board)) { //game over
            return this.boardEvaluator.evaluate(board, depth);
        }

        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                final int currentValue = min(transition.getTransitionBoard(), depth - 1);
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }
}
