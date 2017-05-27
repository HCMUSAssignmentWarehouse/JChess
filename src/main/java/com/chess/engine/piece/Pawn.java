package main.java.com.chess.engine.piece;

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

    public Pawn(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.PAWN,piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMove(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE){
            final int candidateDestinationCoordinate =  this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);

            if (!BoardUtil.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }

            boolean x = this.isFirstMove();
            boolean y = BoardUtil.SEVENTH_RANK[piecePosition] && this.getPieceAlliance().isBlack();
            boolean z = BoardUtil.SECOND_RANK[piecePosition] && this.getPieceAlliance().isWhite();

            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){

                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new PawnPromotion(new PawnMove(board,this,candidateDestinationCoordinate)));
                }else {
                    legalMoves.add(new PawnMove(board,this,candidateDestinationCoordinate));
                }
                if (piecePosition == 52){
                }
            }else if (currentCandidateOffset == 16 && this.isFirstMove() &&(
                    (BoardUtil.SEVENTH_RANK[piecePosition] && this.getPieceAlliance().isBlack() )||
                            (BoardUtil.SECOND_RANK[piecePosition] && this.getPieceAlliance().isWhite()))){
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    legalMoves.add(new PawnJump(board,this,candidateDestinationCoordinate));

                }

            }
            else if (currentCandidateOffset == 7 && !(((BoardUtil.EIGHT_COLUMN[this.piecePosition]) && this.pieceAlliance.isWhite() )
                    || ((BoardUtil.FIRST_COLUMN[this.piecePosition]) && this.pieceAlliance.isBlack()))){
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){

                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate)));
                        }else {
                            legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                        if (piecePosition == 52){
                        }
                    }
                }else if(board.getEnPassantPawn() != null){
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }



            }else if (currentCandidateOffset == 9 && !(((BoardUtil.FIRST_COLUMN[this.piecePosition]) && this.pieceAlliance.isWhite() )
                    || ((BoardUtil.EIGHT_COLUMN[this.piecePosition]) && this.pieceAlliance.isBlack()))){
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){

                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate)));
                        }else {
                            legalMoves.add(new PawnAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                        if (piecePosition == 52){
                        }
                    }
                }else if(board.getEnPassantPawn() != null){
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))){
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            legalMoves.add(new Move.PawnEnPassantAttackMove(board,this,candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }

        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedPiece().getPieceAlliance(),move.getDestinationCoordinate());
    }


    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece(){
        return new Queen(this.pieceAlliance,this.piecePosition,false);
    }
}
