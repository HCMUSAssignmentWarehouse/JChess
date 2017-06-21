package main.java.com.iceteaviet.chess.core.piece;

import com.google.common.collect.ImmutableList;
import main.java.com.iceteaviet.chess.core.Alliance;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.board.Tile;
import main.java.com.iceteaviet.chess.core.player.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represent for Arthur King
 *
 * @see Piece
 * <p>
 * Created by MyPC on 5/10/2017.
 */
public class King extends Piece {

    private final static int[] MOVE_VECTOR_CANDIDATE_COORD = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    private final boolean isCastled;

    public King(final Alliance alliance,
                final int piecePosition,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        this(alliance, piecePosition, DEFAULT_FIRST_MOVE, false, kingSideCastleCapable, queenSideCastleCapable);
    }

    public King(final Alliance alliance,
                final int piecePosition,
                final boolean isFirstMove,
                final boolean isCastled,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, alliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN.get(currentPosition) && ((candidateOffset == -9) || (candidateOffset == 1)
                || (candidateOffset == 7));
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.SECOND_COLUMN.get(currentPosition) && ((candidateOffset == -7) || (candidateOffset == -1)
                || (candidateOffset == 9));
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.kingBonus(this.piecePosition);
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }

    public boolean isCastled() {
        return this.isCastled;
    }

    @Override
    public Collection<Move> calculateLegalMove(GameBoard gameBoard) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int coordOffset : MOVE_VECTOR_CANDIDATE_COORD) {
            final int destinationCoordCandidate = this.piecePosition + coordOffset;

            if (isFirstColumnExclusion(this.piecePosition, coordOffset) || isEightColumnExclusion(this.piecePosition, coordOffset)) {
                continue;
            }

            if (BoardUtils.isValidTileCoordinate(destinationCoordCandidate)) {
                final Tile candidateDestinationTile = gameBoard.getTile(destinationCoordCandidate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(gameBoard, this, destinationCoordCandidate));
                } else {
                    final Piece pieceDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceDestination.getAlliance();

                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new Move.MajorAttackMove(gameBoard, this, destinationCoordCandidate, pieceDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    @Override
    public King move(final Move move) {
        return new King(this.pieceAlliance, move.getDestinationCoordinate(), false, move.isCastlingMove(), false, false);
    }

}
