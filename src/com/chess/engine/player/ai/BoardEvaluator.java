package com.chess.engine.player.ai;

import com.chess.engine.board.Board;

/**
 * Created by MyPC on 5/19/2017.
 */
public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}
