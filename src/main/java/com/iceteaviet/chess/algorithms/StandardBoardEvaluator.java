package main.java.com.iceteaviet.chess.algorithms;

import main.java.com.iceteaviet.chess.core.board.Board;
import main.java.com.iceteaviet.chess.core.board.Move;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.player.Player;

/**
 * Created by MyPC on 5/24/2017.
 */
public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;
    private final static int CASTLE_CAPABLE_BONUS = 25;
    private final static int MOBILITY_MULTIPLIER = 2;
    private final static int ATTACK_MULTIPLIER = 2;
    private final static int TWO_BISHOPS_BONUS = 50;

    private static StandardBoardEvaluator mInstance = null;

    private StandardBoardEvaluator() {
        //Singleton
    }

    public static StandardBoardEvaluator getInstance() {
        if (mInstance == null)
            mInstance = new StandardBoardEvaluator();

        return mInstance;
    }

    private static int mobilityScore(final Player player) {
        return MOBILITY_MULTIPLIER * mobilityRatio(player);
    }

    private static int mobilityRatio(final Player player) {
        return (int) ((player.getLegalMoves().size() * 100.0f) / player.getOpponent().getLegalMoves().size());
    }

    private static int checkmateScore(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : checkScore(player);
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int checkScore(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        int numBishops = 0;
        for (final Piece piece : player.getActivePiece()) {
            pieceValueScore += piece.getPieceValue() + piece.locationBonus();
            if (piece.getPieceType().equals(Piece.PieceType.BISHOP)) {
                numBishops++;
            }
        }
        return pieceValueScore + (numBishops == 2 ? TWO_BISHOPS_BONUS : 0);
    }

    private static int castleCapable(final Player player) {
        return player.isKingSideCastleCapable() || player.isQueenSideCastleCapable() ? CASTLE_CAPABLE_BONUS : 0;
    }

    private static int attackScore(final Player player) {
        int attackScore = 0;
        for (final Move move : player.getLegalMoves()) {
            if (move.isAttack()) {
                final Piece movedPiece = move.getMovedPiece();
                final Piece attackedPiece = move.getAttackedPiece();
                if (movedPiece.getPieceValue() <= attackedPiece.getPieceValue()) {
                    attackScore++;
                }
            }
        }
        return attackScore * ATTACK_MULTIPLIER;
    }

    private static int pawnStructScore(final Player player) {
        return PawnChessAnalyzer.getInstance().pawnStructureScore(player);
    }

    @Override
    public int evaluate(final Board board, final int depth) {
        return getPlayerScore(board.whitePlayer(), depth) - getPlayerScore(board.blackPlayer(), depth);
    }

    private int getPlayerScore(final Player player, final int depth) {
        return pieceValue(player)
                + mobilityScore(player)
                + checkmateScore(player, depth) //Also king threats
                + castleScore(player)
                + attackScore(player)
                + pawnStructScore(player);
    }

    private int castleScore(Player player) {
        return player.isCastled() ? CASTLE_BONUS : castleCapable(player);
    }
}
