package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.player.Move;

/**
 * MoveAlgorithm is a super class for search algorithms. It consists of all the
 * required utilities defining a playable chess game
 * <p>
 * Created by Genius Doan on 5/19/2017.
 */
public interface MoveAlgorithm {
    // The chess gameBoard to be worked on
    //protected GameBoard gameBoard;

    public long getBoardEvaluatedNumbers();

    public Move eval(GameBoard gameBoard, int depth);
}
