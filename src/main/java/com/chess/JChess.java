package main.java.com.chess;

import main.java.com.chess.engine.board.Board;
import main.java.com.chess.gui.Table;

public class JChess {

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        System.out.print(board);

        Table.get().show();
    }
}
