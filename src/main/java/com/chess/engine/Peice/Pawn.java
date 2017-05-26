package main.java.com.chess.engine.Peice;

import main.java.com.chess.engine.Alliance;
import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.Move;
import main.java.com.chess.engine.board.BoardUtil;

import main.java.com.chess.engine.board.Move.PawnAttackMove;
import main.java.com.chess.engine.board.Move.PawnJump;
import main.java.com.chess.engine.board.Move.PawnMove;
import main.java.com.chess.engine.board.Move.PawnPromotion;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MyPC on 5/10/2017.
 */
public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {7,8,9,16};

    public Pawn(final Alliance peiceAlliance, final int peicePosition) {
        super(PeiceType.PAWN,peicePosition, peiceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMove(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinationCoordinate =  this.peicePosition + (this.peiceAlliance.getDirection() * currentCandidateOffset);

            if (!BoardUtil.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }

            boolean x = this.isFirstMove();
            boolean y = BoardUtil.SEVENTH_RANK[peicePosition] && this.getPeiceAlliance().isBlack();
            boolean z = BoardUtil.SECOND_RANK[peicePosition] && this.getPeiceAlliance().isWhite();

            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){

                if (this.peiceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board,this,candidateDestinationCoordinate)));
                }else {
                    legalMoves.add(new PawnMove(board,this,candidateDestinationCoordinate));
                }
                if (peicePosition == 52){
                }
            }else if (currentCandidateOffset == 16 && this.isFirstMove() &&(
                    (BoardUtil.SEVENTH_RANK[peicePosition] && this.getPeiceAlliance().isBlack() )||
                            (BoardUtil.SECOND_RANK[peicePosition] && this.getPeiceAlliance().isWhite()))){
                final int behindCandidateDestinationCoordinate = this.peicePosition + (this.peiceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    legalMoves.add(new PawnJump(board,this,candidateDestinationCoordinate));

                }

            }
            else if (currentCandidateOffset == 7 && !(((BoardUtil.EIGHT_COLUMN[this.peicePosition]) && this.peiceAlliance.isWhite() )
                    || ((BoardUtil.FIRST_COLUMN[this.peicePosition]) && this.peiceAlliance.isBlack()))){
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPeice();
                    if (this.peiceAlliance != pieceOnCandidate.getPeiceAlliance()){

                        if (this.peiceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate)));
                        }else {
                            legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                        if (peicePosition == 52){
                        }
                    }
                }else if(board.getEnPassantPawn() != null){
                    if (board.getEnPassantPawn().getPeicePosition() == (this.peicePosition + (this.peiceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.peiceAlliance != pieceOnCandidate.getPeiceAlliance()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }



            }else if (currentCandidateOffset == 9 && !(((BoardUtil.FIRST_COLUMN[this.peicePosition]) && this.peiceAlliance.isWhite() )
                    || ((BoardUtil.EIGHT_COLUMN[this.peicePosition]) && this.peiceAlliance.isBlack()))){
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPeice();
                    if (this.peiceAlliance != pieceOnCandidate.getPeiceAlliance()){

                        if (this.peiceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate)));
                        }else {
                            legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                        if (peicePosition == 52){
                        }
                    }
                }else if(board.getEnPassantPawn() != null){
                    if (board.getEnPassantPawn().getPeicePosition() == (this.peicePosition - (this.peiceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.peiceAlliance != pieceOnCandidate.getPeiceAlliance()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }

        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePeice(final Move move) {
        return new Pawn(move.getMovedPiece().getPeiceAlliance(),move.getDestinationCoordinate());
    }


    @Override
    public String toString() {
        return PeiceType.PAWN.toString();
    }

    public Piece getPromotionPiece(){
        return new Queen(this.peiceAlliance,this.peicePosition,false);
    }
}
