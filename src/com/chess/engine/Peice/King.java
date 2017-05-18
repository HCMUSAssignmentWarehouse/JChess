package com.chess.engine.Peice;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtil;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MyPC on 5/10/2017.
 */
public class King extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {-9,-8,-7,-1,1,7,8,9};


    public King(final Alliance peiceAlliance,final int peicePosition) {
        super(PeiceType.KING,peicePosition, peiceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMove(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinationCoordinate = this.peicePosition + currentCandidateOffset;

            if (isFirstColumnExclusion(this.peicePosition,currentCandidateOffset) || isEightColumnExclusion(this.peicePosition,currentCandidateOffset)){
                continue;
            }

            if (BoardUtil.isValidTileCoordinate(candidateDestinationCoordinate)){
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (! candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));
                }else {
                    final Piece pieceDestination = candidateDestinationTile.getPeice();
                    final Alliance peiceAlliance = pieceDestination.getPeiceAlliance();

                    if (this.peiceAlliance != peiceAlliance){
                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate, pieceDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }


    @Override
    public String toString() {
        return PeiceType.KING.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == 1)
                || (candidateOffset == 7));
    }

    @Override
    public King movePeice(final Move move) {
        return new King(move.getMovedPiece().getPeiceAlliance(),move.getDestinationCoordinate());
    }


    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.SECOND_COLUMN[currentPosition] && ((candidateOffset == -7) || (candidateOffset == -1)
                || (candidateOffset == 9) );
    }

}
