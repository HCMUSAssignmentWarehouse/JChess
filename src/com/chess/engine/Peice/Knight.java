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
 * Created by MyPC on 5/9/2017.
 */
public class Knight extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {-17,-15,-10,-6,6,10,15,17};

    public Knight(final Alliance peiceAlliance, final int peicePosition) {
        super(PeiceType.KNIGHT,peicePosition, peiceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMove(Board board) {


        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinateCoordidate = this.peicePosition + currentCandidateOffset;

            if (BoardUtil.isValidTileCoordinate(candidateDestinateCoordidate)){

                if (isFirstColumnExclusion(this.peicePosition,currentCandidateOffset) || isSecondColumnExclusion(this.peicePosition,currentCandidateOffset)
                        ||isSevenColumnExclusion(this.peicePosition,currentCandidateOffset)|| isEightColumnExclusion(this.peicePosition,currentCandidateOffset)){
                    continue;
                }
                final Tile candidateDestinationTile = board.getTile(candidateDestinateCoordidate);
                if (! candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board,this,candidateDestinateCoordidate));
                }else {
                    final Piece pieceDestination = candidateDestinationTile.getPeice();
                    final Alliance peiceAlliance = pieceDestination.getPeiceAlliance();

                    if (this.peiceAlliance != peiceAlliance){
                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinateCoordidate, pieceDestination));
                    }
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Knight movePeice(final Move move) {
        return new Knight(move.getMovedPiece().getPeiceAlliance(),move.getDestinationCoordinate());
    }

    @Override
    public String toString() {
        return PeiceType.KNIGHT.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.FIRST_COLUMN[currentPosition] && ((candidateOffset == -17) || (candidateOffset == -10) || (candidateOffset == 6)
                || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.SECOND_COLUMN[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    private static boolean isSevenColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.SEVEN_COLUMN[currentPosition] && ((candidateOffset == -6) || (candidateOffset == 10));
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.EIGHT_COLUMN[currentPosition] && ((candidateOffset == 17) || (candidateOffset == 10) || (candidateOffset == -6)
                || (candidateOffset == -15));
    }

}
