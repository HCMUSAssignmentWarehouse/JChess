package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.player.Move;
import main.java.com.iceteaviet.chess.core.player.MoveTransition;
import main.res.values.string;

import java.util.concurrent.atomic.AtomicLong;

/**
 * MiniMax, a subclass of MoveAlgorithm, is a MiniMax search algorithm.
 * The MiniMax strategy serves as the foundation of chess programs.
 * It is a strategy that minimizes the maximum risk for a player, going through
 * all of the branches in the move tree and evaluating the gameBoard position.
 * <p>
 * Created by Genius Doan on 5/19/2017.
 */
public class MiniMax implements MoveAlgorithm {
    private final int searchDepth;
    private final GameBoardAnalyzer gameBoardAnalyzer;
    private long boardsEvaluated;
    private long executionTime;
    private FreqTableRow[] freqTable;
    private int freqTableIndex;

    public MiniMax(final int searchDepth) {
        this.gameBoardAnalyzer = ArthurBoardAnalyzer.getInstance();
        this.boardsEvaluated = 0;
        this.searchDepth = searchDepth;
    }

    private static boolean isEndGame(final GameBoard gameBoard) {
        return gameBoard.getCurrentPlayer().isInCheckMate() ||
                gameBoard.getCurrentPlayer().isInStaleMate();
    }

    @Override
    public String toString() {
        return string.minimax_name;
    }

    @Override
    public long getBoardEvaluatedNumbers() {
        return this.boardsEvaluated;
    }

    @Override
    public Move eval(final GameBoard gameBoard, int depth) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = Move.MoveFactory.createMove(null, -1, -1);
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(gameBoard.getCurrentPlayer() + " is THINKING with depth = " + this.searchDepth);
        this.freqTable = new FreqTableRow[gameBoard.getCurrentPlayer().getLegalMoves().size()];
        this.freqTableIndex = 0;
        int moveCounter = 1;
        final int numMoves = gameBoard.getCurrentPlayer().getLegalMoves().size();
        for (final Move move : gameBoard.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = gameBoard.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final FreqTableRow row = new FreqTableRow(move);
                this.freqTable[this.freqTableIndex] = row;
                currentValue = gameBoard.getCurrentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getFromBoard(), this.searchDepth - 1) :
                        max(moveTransition.getTransitionBoard(), this.searchDepth - 1);
                System.out.println("\t" + toString() + " analyzing move (" + moveCounter + "/" + numMoves + ") " + move +
                        " scores " + currentValue + " " + this.freqTable[this.freqTableIndex]);
                this.freqTableIndex++;
                if (gameBoard.getCurrentPlayer().getAlliance().isWhite() &&
                        currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (gameBoard.getCurrentPlayer().getAlliance().isBlack() &&
                        currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            } else {
                System.out.println("\t" + toString() + " can't execute move (" + moveCounter + "/" + numMoves + ") " + move);
            }
            moveCounter++;
        }

        this.executionTime = System.currentTimeMillis() - startTime;
        System.out.printf("%s SELECTS %s [#boards = %d time taken = %d ms, rate = %.1f\n", gameBoard.getCurrentPlayer(),
                bestMove, this.boardsEvaluated, this.executionTime, (1000 * ((double) this.boardsEvaluated / this.executionTime)));
        long total = 0;
        for (final FreqTableRow row : this.freqTable) {
            if (row != null) {
                total += row.getCount();
            }
        }
        if (this.boardsEvaluated != total) {
            System.out.println("somethings wrong with the # of boards evaluated!");
        }
        return bestMove;
    }

    public int min(final GameBoard gameBoard,
                   final int depth) {
        if (depth == 0) {
            this.boardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.gameBoardAnalyzer.evaluate(gameBoard, depth);
        }
        if (isEndGame(gameBoard)) {
            return this.gameBoardAnalyzer.evaluate(gameBoard, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : gameBoard.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = gameBoard.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    public int max(final GameBoard gameBoard,
                   final int depth) {
        if (depth == 0) {
            this.boardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.gameBoardAnalyzer.evaluate(gameBoard, depth);
        }
        if (isEndGame(gameBoard)) {
            return this.gameBoardAnalyzer.evaluate(gameBoard, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : gameBoard.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = gameBoard.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    private static class FreqTableRow {

        private final Move move;
        private final AtomicLong count;

        FreqTableRow(final Move move) {
            this.count = new AtomicLong();
            this.move = move;
        }

        public long getCount() {
            return this.count.get();
        }

        public void increment() {
            this.count.incrementAndGet();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.move.getCurrentCoordinate()) +
                    BoardUtils.getPositionAtCoordinate(this.move.getDestinationCoordinate()) + " : " + this.count;
        }
    }
}
