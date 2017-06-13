package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.board.Board;
import main.java.com.iceteaviet.chess.core.board.Move;

/**
 * Created by MyPC on 5/19/2017.
 */
public interface MoveStrategy {
    public Move execute(Board board, int depth);
}
