package main.java.com.chess.engine.player;

import main.java.com.chess.engine.Alliance;
import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.Move;
import main.java.com.chess.engine.Peice.King;
import main.java.com.chess.engine.Peice.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MyPC on 5/12/2017.
 */
public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    protected Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves =  ImmutableList.copyOf(Iterables.concat(legalMoves,calculateKingCastles(legalMoves,opponentMoves)));
        this.isInCheck = !Player.calculateAttracksOnTile(this.playerKing.getPeicePosition(),opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttracksOnTile(Integer peicePosition, Collection<Move> opponentMoves) {
        final List<Move> attrackMoves = new ArrayList<>();
        for (final Move move: opponentMoves){
            if(peicePosition == move.getDestinationCoordinate()){
                attrackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attrackMoves);
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    private King establishKing(){

        for (final Piece piece : getActivePeice()){
            if (piece.getPeiceType().isKing()){
                return (King) piece;
            }
        }

        throw new RuntimeException("Should not reach here! Not a valid Board!");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public boolean isInCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {

        for (final Move move: this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(final Move move){

        if (!isMoveLegal(move)){
            return new MoveTransition(this.board,move,MoveStatus.ILLEGAL_MOVE);
        }

        Board transitionBoard = move.execute();
        final Collection<Move> kingAttracks = Player.calculateAttracksOnTile(transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPeicePosition(),
                transitionBoard.getCurrentPlayer().getLegalMoves());

        if (!kingAttracks.isEmpty()){
            return new MoveTransition(this.board,move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }


        return new MoveTransition(transitionBoard,move,MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePeice();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move>calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
