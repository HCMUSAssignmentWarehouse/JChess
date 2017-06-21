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
 * Represent for Bishop Piece
 *
 * @see Piece
 * <p>
 * Created by MyPC on 5/10/2017.
 */
public class Bishop extends Piece {
    private final static int[] MOVE_VECTOR_CANDIDATE_COORD = {-9, -7, 7, 9};

    public Bishop(Alliance pieceAlliance, int piecePosition) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN.get(currentPosition) && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHT_COLUMN.get(currentPosition) && (candidateOffset == -7 || candidateOffset == 9);
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.bishopBonus(this.piecePosition);
    }

    @Override
    public Collection<Move> calculateLegalMove(GameBoard gameBoard) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int coordOffset : MOVE_VECTOR_CANDIDATE_COORD) {
            int destinationCoordCandidate = this.piecePosition;

            while (BoardUtils.isValidTileCoordinate(destinationCoordCandidate)) {
                if (isFirstColumnExclusion(destinationCoordCandidate, coordOffset) ||
                        isEightColumnExclusion(destinationCoordCandidate, coordOffset)) {
                    break;
                }

                destinationCoordCandidate += coordOffset;
                if (BoardUtils.isValidTileCoordinate(destinationCoordCandidate)) {
                    final Tile destinationTileCandidate = gameBoard.getTile(destinationCoordCandidate);
                    if (!destinationTileCandidate.isTileOccupied()) {
                        legalMoves.add(new Move.MajorMove(gameBoard, this, destinationCoordCandidate));
                    } else {
                        final Piece pieceAtDestination = destinationTileCandidate.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new Move.MajorAttackMove(gameBoard, this, destinationCoordCandidate,
                                    pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }


        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Bishop move(final Move move) {
        return new Bishop(move.getMovedPiece().getAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

}
