package main.java.com.chess.engine.piece;

import com.google.common.collect.ImmutableList;
import main.java.com.chess.engine.Alliance;
import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.BoardUtil;
import main.java.com.chess.engine.board.Move;
import main.java.com.chess.engine.board.Move.MajorAttackMove;
import main.java.com.chess.engine.board.Move.MajorMove;
import main.java.com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MyPC on 5/10/2017.
 */
public class King extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {-9, -8, -7, -1, 1, 7, 8, 9};


    public King(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.KING, piecePosition, pieceAlliance);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtil.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == 1)
                || (candidateOffset == 7));
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtil.SECOND_COLUMN[currentPosition] && ((candidateOffset == -7) || (candidateOffset == -1)
                || (candidateOffset == 9));
    }

    @Override
    public Collection<Move> calculateLegalMove(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) || isEightColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }

            if (BoardUtil.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceDestination.getPieceAlliance();

                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceDestination));
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
    public King movePiece(final Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

}
