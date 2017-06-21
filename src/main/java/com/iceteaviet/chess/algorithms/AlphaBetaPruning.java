package main.java.com.iceteaviet.chess.algorithms;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import main.java.com.iceteaviet.chess.core.board.Board;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.Move;
import main.java.com.iceteaviet.chess.core.player.MoveTransition;
import main.java.com.iceteaviet.chess.core.player.Player;
import main.res.values.string;

import java.util.Collection;
import java.util.Comparator;
import java.util.Observable;

/**
 * AlfaBeta, a subclass of MoveAlgorithm, is an alfabeta search algorithm.
 * This technique is called "alfa-beta pruning". It is a refined minimax
 * strategy that identifies unfavorable branches in the move tree and removes
 * them early, thus speeding up the search process.
 * <p>
 * Created by Genius Doan on 6/20/2017.
 */
public class AlphaBetaPruning extends Observable implements MoveAlgorithm {
    private static final int MAX_QUIESCENCE = 5000;

    private final int searchDepth;
    private final BoardEvaluator boardEvaluator;
    private long boardsEvaluated;
    private long executionTime;
    private int quiescenceCount;

    public AlphaBetaPruning(final int searchDepth) {
        this.boardEvaluator = StandardBoardEvaluator.getInstance();
        this.searchDepth = searchDepth;
        this.boardsEvaluated = 0;
        this.quiescenceCount = 0;
    }

    private static String score(final Player currentPlayer,
                                final int highestSeenValue,
                                final int lowestSeenValue) {

        if (currentPlayer.getAlliance().isWhite()) {
            return "[score: " + highestSeenValue + "]";
        } else if (currentPlayer.getAlliance().isBlack()) {
            return "[score: " + lowestSeenValue + "]";
        }
        throw new RuntimeException("bad bad boy!");
    }

    private static String getSpentTime(final long start, final long end) {
        final long timeTaken = (end - start) / 1000000;
        return timeTaken + " ms";
    }

    @Override
    public String toString() {
        return string.alpha_beta_name;
    }

    @Override
    public long getBoardEvaluatedNumbers() {
        return this.boardsEvaluated;
    }

    @Override
    public Move eval(final Board board, int searchDepth) {
        final long startTime = System.currentTimeMillis();
        final Player currentPlayer = board.getCurrentPlayer();
        Move bestMove = Move.MoveFactory.createMove(null, -1, -1);
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.getCurrentPlayer() + " is THINKING with depth = " + this.searchDepth);
        int moveCounter = 1;
        int numMoves = board.getCurrentPlayer().getLegalMoves().size();

        for (final Move move : MoveSorter.EXPENSIVE.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            this.quiescenceCount = 0;
            final String s;
            if (moveTransition.getMoveStatus().isDone()) {
                final long candidateMoveStartTime = System.nanoTime();
                currentValue = currentPlayer.getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue) :
                        max(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                if (currentPlayer.getAlliance().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                    if (moveTransition.getTransitionBoard().blackPlayer().isInCheckMate()) {
                        break;
                    }
                } else if (currentPlayer.getAlliance().isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                    if (moveTransition.getTransitionBoard().whitePlayer().isInCheckMate()) {
                        break;
                    }
                }

                final String quiescenceInfo = " " + score(currentPlayer, highestSeenValue, lowestSeenValue) + " q: " + this.quiescenceCount;
                s = "\t" + toString() + "(" + this.searchDepth + "), m: (" + moveCounter + "/" + numMoves + ") " + move + ", best:  " + bestMove

                        + quiescenceInfo + ", t: " + getSpentTime(candidateMoveStartTime, System.nanoTime());
            } else {
                s = "\t" + toString() + ", m: (" + moveCounter + "/" + numMoves + ") " + move + " is illegal! best: " + bestMove;
            }
            System.out.println(s);
            setChanged();
            notifyObservers(s);
            moveCounter++;
        }

        this.executionTime = System.currentTimeMillis() - startTime;
        System.out.printf("%s SELECTS %s [#boards evaluated = %d, time taken = %d ms, rate = %.1f\n", board.getCurrentPlayer(),
                bestMove, this.boardsEvaluated, this.executionTime, (1000 * ((double) this.boardsEvaluated / this.executionTime)));
        return bestMove;
    }

    private int max(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || BoardUtils.isEndGame(board)) {
            this.boardsEvaluated++;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : MoveSorter.STANDARD.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentHighest = Math.max(currentHighest, min(moveTransition.getTransitionBoard(),
                        calculateQuiescenceDepth(moveTransition, depth), currentHighest, lowest));
                if (currentHighest >= lowest) {
                    return lowest;
                }
            }
        }
        return currentHighest;
    }

    private int min(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || BoardUtils.isEndGame(board)) {
            this.boardsEvaluated++;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;
        for (final Move move : MoveSorter.STANDARD.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentLowest = Math.min(currentLowest, max(moveTransition.getTransitionBoard(),
                        calculateQuiescenceDepth(moveTransition, depth), highest, currentLowest));
                if (currentLowest <= highest) {
                    return highest;
                }
            }
        }
        return currentLowest;
    }

    private int calculateQuiescenceDepth(final MoveTransition moveTransition,
                                         final int depth) {
        if (depth == 1 && this.quiescenceCount < MAX_QUIESCENCE) {
            int activityMeasure = 0;
            if (moveTransition.getTransitionBoard().getCurrentPlayer().isInCheck()) {
                activityMeasure += 2;
            }
            for (final Move move : BoardUtils.lastNMoves(moveTransition.getTransitionBoard(), 4)) {
                if (move.isAttack()) {
                    activityMeasure += 1;
                }
            }
            if (activityMeasure > 3) {
                this.quiescenceCount++;
                return 2;
            }
        }
        return depth - 1;
    }

    private enum MoveSorter {
        STANDARD {
            @Override
            Collection<Move> sort(final Collection<Move> moves) {
                return Ordering.from(new Comparator<Move>() {
                    @Override
                    public int compare(final Move move1,
                                       final Move move2) {
                        return ComparisonChain.start()
                                .compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
                                .compare(BoardUtils.mvvlva(move2), BoardUtils.mvvlva(move1))
                                .result();
                    }
                }).immutableSortedCopy(moves);
            }
        },
        EXPENSIVE {
            @Override
            Collection<Move> sort(final Collection<Move> moves) {
                return Ordering.from(new Comparator<Move>() {
                    @Override
                    public int compare(final Move move1,
                                       final Move move2) {
                        return ComparisonChain.start()
                                .compareTrueFirst(BoardUtils.kingThreat(move1), BoardUtils.kingThreat(move2))
                                .compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
                                .compare(BoardUtils.mvvlva(move2), BoardUtils.mvvlva(move1))
                                .result();
                    }
                }).immutableSortedCopy(moves);
            }
        };

        abstract Collection<Move> sort(Collection<Move> moves);
    }
}
