package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

/**
 * Created by MyPC on 5/19/2017.
 */
public class Minimax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;

    public Minimax(){
        this.boardEvaluator = null;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board, int depth) {
        return null;
    }
}
