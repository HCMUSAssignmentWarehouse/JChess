package main.java.com.iceteaviet.chess.core.player;

import com.google.common.collect.ImmutableList;
import main.java.com.iceteaviet.chess.core.Alliance;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.board.Tile;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.piece.Rook;
import main.java.com.iceteaviet.chess.core.player.Move.KingSideCastleMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * WhitePlayer represent for White type player in chess game
 * <p>
 * Created by MyPC on 5/12/2017.
 */
public class WhitePlayer extends Player {
    public WhitePlayer(final GameBoard gameBoard, final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves) {
        super(gameBoard, whiteStandardLegalMoves, blackStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePiece() {
        return this.gameBoard.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.gameBoard.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentLegals) {
        if (this.isInCheck() || this.isCastled()) {
            return ImmutableList.of();
        }

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !isInCheck() && this.playerKing.getPosition() == 4) {
            if (!this.gameBoard.getTile(61).isTileOccupied() && !this.gameBoard.getTile(62).isTileOccupied()) {
                final Tile rookTile = this.gameBoard.getTile(63);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(61, opponentLegals).isEmpty()
                            && Player.calculateAttacksOnTile(62, opponentLegals).isEmpty()
                            && rookTile.getPiece().getType().isRook()) {
                        if (!BoardUtils.isKingPawnTrap(this.gameBoard, this.playerKing, 52)) {
                            kingCastles.add(new KingSideCastleMove(this.gameBoard, this.playerKing, 62, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                        }
                    }
                }
            }

            //whites queen side castle
            if (!this.gameBoard.getTile(59).isTileOccupied() && !this.gameBoard.getTile(58).isTileOccupied()
                    && !this.gameBoard.getTile(57).isTileOccupied()) {
                final Tile rookTile = this.gameBoard.getTile(56);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                        && Player.calculateAttacksOnTile(58, opponentLegals).isEmpty()
                        && Player.calculateAttacksOnTile(59, opponentLegals).isEmpty()
                        && rookTile.getPiece().getType().isRook()) {
                    if (!BoardUtils.isKingPawnTrap(this.gameBoard, this.playerKing, 52)) {
                        kingCastles.add(new Move.QueenSideCastleMove(this.gameBoard, this.playerKing, 58, (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
