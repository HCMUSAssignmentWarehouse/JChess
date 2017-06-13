package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.board.Board;

/**
 * Created by MyPC on 5/19/2017.
 */
public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}
