package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.board.Board;
import main.java.com.iceteaviet.chess.core.board.Move;

/**
 * MoveAlgorithm is a super class for search algorithms. It consists of all the
 * required utilities defining a playable chess game
 * <p>
 * Created by Genius Doan on 5/19/2017.
 */
public interface MoveAlgorithm {
    // The chess board to be worked on
    //protected Board board;

    public long getBoardEvaluatedNumbers();

    public Move eval(Board board, int depth);
}
