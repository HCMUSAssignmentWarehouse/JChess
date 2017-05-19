package main.java.com.chess.engine.player;

import main.java.com.chess.engine.Alliance;
import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.Move;
import main.java.com.chess.engine.board.Move.KingSideCastleMove;
import main.java.com.chess.engine.board.Tile;
import main.java.com.chess.engine.Peice.Piece;
import main.java.com.chess.engine.Peice.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MyPC on 5/12/2017.
 */
public class WhitePlayer extends Player{
    public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board,whiteStandardLegalMoves,blackStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePeice() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (this.playerKing.isFirstMove()&& !isInCheck()){
            if (!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()){
                final Tile rookTile = this.board.getTile(63);

                if (rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove()){
                    if (Player.calculateAttracksOnTile(61,opponentLegals).isEmpty()
                            && Player.calculateAttracksOnTile(62,opponentLegals).isEmpty()
                            && rookTile.getPeice().getPeiceType().isRook()){
                        kingCastles.add(new KingSideCastleMove(this.board,this.playerKing,62,
                                (Rook)rookTile.getPeice(),rookTile.getTileCoordinate(),61));
                    }
                }
            }

            if (!this.board.getTile(59).isTileOccupied() && !this.board.getTile(58).isTileOccupied()
                    && !this.board.getTile(57).isTileOccupied()){
                final Tile rookTile = this.board.getTile(56);

                if (rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove()
                        && Player.calculateAttracksOnTile(58,opponentLegals).isEmpty()
                        && Player.calculateAttracksOnTile(59,opponentLegals).isEmpty()
                        &&rookTile.getPeice().getPeiceType().isRook()){
                    kingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing,58,
                            (Rook)rookTile.getPeice(),rookTile.getTileCoordinate(),58));
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
