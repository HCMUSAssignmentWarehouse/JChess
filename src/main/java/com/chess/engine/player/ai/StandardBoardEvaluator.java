package main.java.com.chess.engine.player.ai;

import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.piece.Piece;
import main.java.com.chess.engine.player.Player;

/**
 * Created by MyPC on 5/24/2017.
 */
public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 1000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    private static int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * deptBonus(depth) : 0;
    }

    private static int deptBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int check(Player player) {

        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for (final Piece piece : player.getActivePiece()) {
            pieceValueScore = piece.getPieceValue();
        }
        return pieceValueScore;
    }

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player) + mobility(player) + check(player) + checkmate(player, depth) + castle(player);
    }

    private int castle(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }
}
