package main.java.com.iceteaviet.chess.core.player;

import com.google.common.collect.ImmutableList;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.board.Tile;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.piece.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BlackPlayer represent for Black type player in chess game
 * <p>
 * Created by MyPC on 5/12/2017.
 */
public class BlackPlayer extends Player {
    public BlackPlayer(final GameBoard gameBoard, final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(gameBoard, blackStandardLegalMoves, whiteStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePiece() {
        return this.gameBoard.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.gameBoard.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentLegals) {
        if (this.isInCheck() || this.isCastled()) {
            return ImmutableList.of();
        }

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !isInCheck() && this.playerKing.getPosition() == 4) {
            //blacks king side castle
            if (!this.gameBoard.getTile(5).isTileOccupied() && !this.gameBoard.getTile(6).isTileOccupied()) {
                final Tile rookTile = this.gameBoard.getTile(7);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(5, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnTile(6, opponentLegals).isEmpty()
                            && rookTile.getPiece().getType().isRook()) {
                        if (!BoardUtils.isKingPawnTrap(this.gameBoard, this.playerKing, 12)) {
                            kingCastles.add(new Move.KingSideCastleMove(this.gameBoard, this.playerKing, 6,
                                    (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                        }
                    }
                }
            }

            //blacks queen side castle
            if (!this.gameBoard.getTile(1).isTileOccupied() && !this.gameBoard.getTile(2).isTileOccupied()
                    && !this.gameBoard.getTile(3).isTileOccupied()) {
                final Tile rookTile = this.gameBoard.getTile(0);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                        && Player.calculateAttacksOnTile(2, opponentLegals).isEmpty()
                        && Player.calculateAttacksOnTile(3, opponentLegals).isEmpty()
                        && rookTile.getPiece().getType().isRook()) {
                    if (!BoardUtils.isKingPawnTrap(this.gameBoard, this.playerKing, 12)) {
                        kingCastles.add(
                                new Move.QueenSideCastleMove(this.gameBoard, this.playerKing, 2, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
