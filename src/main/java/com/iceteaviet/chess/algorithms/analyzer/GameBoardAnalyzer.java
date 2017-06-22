package main.java.com.iceteaviet.chess.algorithms.analyzer;

import main.java.com.iceteaviet.chess.core.board.GameBoard;

/**
 * Created by MyPC on 5/19/2017.
 */
public interface GameBoardAnalyzer {
    int evaluate(GameBoard gameBoard, int depth);
}
