package main.java.com.iceteaviet.chess.core.piece;

import com.google.common.collect.ImmutableList;
import main.java.com.iceteaviet.chess.core.player.Alliance;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.board.Tile;
import main.java.com.iceteaviet.chess.core.player.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represent for Queen Piece
 *
 * @see Piece
 * <p>
 * Created by MyPC on 5/10/2017.
 */
public class Queen extends Piece {

    private final static int[] MOVE_VECTOR_CANDIDATE_COORD = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean isFirstMove;

    public Queen(Alliance pieceAlliance, int piecePosition) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance);
        this.isFirstMove = true;
    }

    public Queen(Alliance pieceAlliance, int piecePosition, boolean isFirstMove) {

        super(PieceType.QUEEN, piecePosition, pieceAlliance);
        this.isFirstMove = isFirstMove;
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN.get(currentPosition) && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHT_COLUMN.get(currentPosition) && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.queenBonus(this.piecePosition);
    }

    @Override
    public Collection<Move> calculateLegalMove(GameBoard gameBoard) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int coordOffset : MOVE_VECTOR_CANDIDATE_COORD) {
            int destinationCoordCandidate = this.piecePosition;

            while (BoardUtils.isValidTileCoordinate(destinationCoordCandidate)) {

                if (isFirstColumnExclusion(destinationCoordCandidate, coordOffset) || isEightColumnExclusion(destinationCoordCandidate, coordOffset)) {
                    break;
                }

                destinationCoordCandidate += coordOffset;

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
                        break;
                    }
                }
            }
        }


        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Queen move(final Move move) {
        return new Queen(move.getMovedPiece().getAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
    }
}
