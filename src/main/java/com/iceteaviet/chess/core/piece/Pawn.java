package main.java.com.iceteaviet.chess.core.piece;

import com.google.common.collect.ImmutableList;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.player.Alliance;
import main.java.com.iceteaviet.chess.core.player.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represent for Pawn Piece
 *
 * @see Piece
 * <p>
 * Created by MyPC on 5/10/2017.
 */
public class Pawn extends Piece {

    private final static int[] MOVE_VECTOR_CANDIDATE_COORD = {7, 8, 9, 16};

    public Pawn(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.PAWN, piecePosition, pieceAlliance);
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.knightBonus(this.piecePosition);
    }

    @Override
    public Collection<Move> calculateLegalMove(final GameBoard gameBoard) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int coordOffset : MOVE_VECTOR_CANDIDATE_COORD) {
            final int destinationCoordCandidate = this.piecePosition + (this.pieceAlliance.getDirection() * coordOffset);

            if (!BoardUtils.isValidTileCoordinate(destinationCoordCandidate)) {
                continue;
            }

            boolean x = this.isFirstMove();
            boolean y = BoardUtils.SEVENTH_RANK.get(piecePosition) && this.getAlliance().isBlack();
            boolean z = BoardUtils.SECOND_RANK.get(piecePosition) && this.getAlliance().isWhite();

            if (coordOffset == 8 && !gameBoard.getTile(destinationCoordCandidate).isTileOccupied()) {

                if (this.pieceAlliance.isPawnPromotionSquare(destinationCoordCandidate)) {
                    legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(gameBoard, this, destinationCoordCandidate)));
                } else {
                    legalMoves.add(new Move.PawnMove(gameBoard, this, destinationCoordCandidate));
                }
                if (piecePosition == 52) {
                }
            } else if (coordOffset == 16 && this.isFirstMove() && (
                    (BoardUtils.SEVENTH_RANK.get(piecePosition) && this.getAlliance().isBlack()) ||
                            (BoardUtils.SECOND_RANK.get(piecePosition) && this.getAlliance().isWhite()))) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!gameBoard.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !gameBoard.getTile(destinationCoordCandidate).isTileOccupied()) {
                    legalMoves.add(new Move.PawnJump(gameBoard, this, destinationCoordCandidate));

                }

            } else if (coordOffset == 7 && !(((BoardUtils.EIGHT_COLUMN.get(this.piecePosition)) && this.pieceAlliance.isWhite())
                    || ((BoardUtils.FIRST_COLUMN.get(this.piecePosition)) && this.pieceAlliance.isBlack()))) {
                if (gameBoard.getTile(destinationCoordCandidate).isTileOccupied()) {
                    final Piece pieceOnCandidate = gameBoard.getTile(destinationCoordCandidate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getAlliance()) {

                        if (this.pieceAlliance.isPawnPromotionSquare(destinationCoordCandidate)) {
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(gameBoard, this, destinationCoordCandidate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(gameBoard, this, destinationCoordCandidate, pieceOnCandidate));
                        }
                        if (piecePosition == 52) {
                        }
                    }
                } else if (gameBoard.getEnPassantPawn() != null) {
                    if (gameBoard.getEnPassantPawn().getPosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = gameBoard.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getAlliance()) {
                            legalMoves.add(new Move.PawnEnPassantAttackMove(gameBoard, this, destinationCoordCandidate, pieceOnCandidate));
                        }
                    }
                }


            } else if (coordOffset == 9 && !(((BoardUtils.FIRST_COLUMN.get(this.piecePosition)) && this.pieceAlliance.isWhite())
                    || ((BoardUtils.EIGHT_COLUMN.get(this.piecePosition)) && this.pieceAlliance.isBlack()))) {
                if (gameBoard.getTile(destinationCoordCandidate).isTileOccupied()) {
                    final Piece pieceOnCandidate = gameBoard.getTile(destinationCoordCandidate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getAlliance()) {

                        if (this.pieceAlliance.isPawnPromotionSquare(destinationCoordCandidate)) {
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(gameBoard, this, destinationCoordCandidate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(gameBoard, this, destinationCoordCandidate, pieceOnCandidate));
                        }
                        if (piecePosition == 52) {
                        }
                    }
                } else if (gameBoard.getEnPassantPawn() != null) {
                    if (gameBoard.getEnPassantPawn().getPosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = gameBoard.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getAlliance()) {
                            legalMoves.add(new Move.PawnEnPassantAttackMove(gameBoard, this, destinationCoordCandidate, pieceOnCandidate));
                        }
                    }
                }
            }

        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn move(final Move move) {
        return new Pawn(move.getMovedPiece().getAlliance(), move.getDestinationCoordinate());
    }


    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece() {
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }
}
