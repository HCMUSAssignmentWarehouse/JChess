package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.Peice.Piece;
import com.chess.engine.Peice.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MyPC on 5/12/2017.
 */
public class BlackPlayer extends Player {
    public BlackPlayer(final Board board,final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(board,blackStandardLegalMoves,whiteStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePeice() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (this.playerKing.isFirstMove()&& !isInCheck()){
            if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()){
                final Tile rookTile = this.board.getTile(7);

                if (rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove()){
                    if (Player.calculateAttracksOnTile(5,opponentLegals).isEmpty()
                            && Player.calculateAttracksOnTile(6,opponentLegals).isEmpty()
                            && rookTile.getPeice().getPeiceType().isRook()){
                        kingCastles.add(new KingSideCastleMove(this.board,this.playerKing,6,
                                (Rook)rookTile.getPeice(),rookTile.getTileCoordinate(),5));
                    }
                }
            }

            if (!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied()
                    && !this.board.getTile(3).isTileOccupied()){
                final Tile rookTile = this.board.getTile(0);

                if (rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove()
                        && Player.calculateAttracksOnTile(2,opponentLegals).isEmpty()
                        && Player.calculateAttracksOnTile(3,opponentLegals).isEmpty()
                        &&rookTile.getPeice().getPeiceType().isRook()){
                    kingCastles.add(new QueenSideCastleMove(this.board,this.playerKing,2,
                            (Rook)rookTile.getPeice(),rookTile.getTileCoordinate(),3));
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
