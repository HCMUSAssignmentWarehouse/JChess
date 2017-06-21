package main.java.com.iceteaviet.chess.core.board;


import com.google.common.collect.ImmutableMap;
import main.java.com.iceteaviet.chess.core.piece.Piece;

import java.util.HashMap;
import java.util.Map;

/**
 * Tile is part of Game Board. It is a cell in game board and It built game board
 *
 * @see GameBoard
 * <p>
 * Created by MyPC on 5/9/2017.
 */
public abstract class Tile {
    private static final Map<Integer, EmptyTile> mEmptyTileCached = createAllPossipleEmptyTile();
    protected final int tileCoordinate;

    private Tile(int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    private static Map<Integer, EmptyTile> createAllPossipleEmptyTile() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i < BoardUtils.TILE_COUNT; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }

    public static Tile createTile(final int tileCoordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : mEmptyTileCached.get(tileCoordinate);
    }

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile {

        EmptyTile(final int tileCoordinate) {
            super(tileCoordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile {

        private final Piece pieceOnTile;

        OccupiedTile(int tileCoordinate, Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getAlliance().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }
    }
}
