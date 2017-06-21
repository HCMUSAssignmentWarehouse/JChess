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
 * Represent for Knight Piece
 *
 * @see Piece
 * <p>
 * Created by MyPC on 5/9/2017.
 */
public class Knight extends Piece {

    private final static int[] MOVE_VECTOR_CANDIDATE_COORD = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN.get(currentPosition) && ((candidateOffset == -17) || (candidateOffset == -10) || (candidateOffset == 6)
                || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.SECOND_COLUMN.get(currentPosition) && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    private static boolean isSevenColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.SEVEN_COLUMN.get(currentPosition) && ((candidateOffset == -6) || (candidateOffset == 10));
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHT_COLUMN.get(currentPosition) && ((candidateOffset == 17) || (candidateOffset == 10) || (candidateOffset == -6)
                || (candidateOffset == -15));
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.knightBonus(this.piecePosition);
    }

    @Override
    public Collection<Move> calculateLegalMove(GameBoard gameBoard) {


        final List<Move> legalMoves = new ArrayList<>();

        for (final int coordOffset : MOVE_VECTOR_CANDIDATE_COORD) {
            final int destinationCoordCandidate = this.piecePosition + coordOffset;

            if (BoardUtils.isValidTileCoordinate(destinationCoordCandidate)) {

                if (isFirstColumnExclusion(this.piecePosition, coordOffset) || isSecondColumnExclusion(this.piecePosition, coordOffset)
                        || isSevenColumnExclusion(this.piecePosition, coordOffset) || isEightColumnExclusion(this.piecePosition, coordOffset)) {
                    continue;
                }
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
    public Knight move(final Move move) {
        return new Knight(move.getMovedPiece().getAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

}
