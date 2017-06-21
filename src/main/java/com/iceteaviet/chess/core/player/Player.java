package main.java.com.iceteaviet.chess.core.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import main.java.com.iceteaviet.chess.core.Alliance;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.piece.King;
import main.java.com.iceteaviet.chess.core.piece.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Player is a base class represent for a player
 * <p>
 * Created by MyPC on 5/12/2017.
 */
public abstract class Player {
    protected final GameBoard gameBoard;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    protected final boolean isInCheck;

    protected Player(final GameBoard gameBoard, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.gameBoard = gameBoard;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPosition(), opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(Integer piecePosition, Collection<Move> opponentMoves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : opponentMoves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    private King establishKing() {

        for (final Piece piece : getActivePiece()) {
            if (piece.getType().isKing()) {
                return (King) piece;
            }
        }

        throw new RuntimeException("Should not reach here! Not a valid GameBoard!");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {

        for (final Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCastled() {
        //TODO: Update king to getInstance is castled info
        return false;
    }

    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }

    public MoveTransition makeMove(final Move move) {

        if (!isMoveLegal(move)) {
            return new MoveTransition(this.gameBoard, move, MoveStatus.ILLEGAL_MOVE);
        }

        GameBoard transitionBoard = move.execute();
        final Collection<Move> kingAttracks = Player.calculateAttacksOnTile(transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPosition(),
                transitionBoard.getCurrentPlayer().getLegalMoves());

        if (!kingAttracks.isEmpty()) {
            return new MoveTransition(this.gameBoard, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }


        return new MoveTransition(this.gameBoard, transitionBoard, move, MoveStatus.DONE);
    }

    public MoveTransition unMakeMove(final Move move) {
        return new MoveTransition(this.gameBoard, move.undo(), move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePiece();

    public abstract Alliance getAlliance();

    public abstract Player getOpponent();

    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
