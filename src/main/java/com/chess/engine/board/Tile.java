package main.java.com.chess.engine.board;


import main.java.com.chess.engine.piece.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MyPC on 5/9/2017.
 */
public abstract class Tile {
    protected final int tileCoordinate;

    private Tile(int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    }

    private static final Map<Integer,EmptyTile> EMPTY_TILE_CACHE = createAllPossipleEmptyTile();

    private static Map<Integer,EmptyTile> createAllPossipleEmptyTile() {
        final Map<Integer,EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0 ; i < BoardUtil.NUM_TILES ; i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }

    public static Tile createTile(final int tileCoordinate, final Piece piece){
        return piece != null ? new OccupiedTile(tileCoordinate, piece): EMPTY_TILE_CACHE.get(tileCoordinate);
    }

    public abstract boolean isTileOccupied();
    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile{

        EmptyTile(final int tileCoordinate) {
            super(tileCoordinate);
        }

        @Override
        public String toString(){
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

    public static final class OccupiedTile extends Tile{

        private final Piece pieceOnTile;

        OccupiedTile(int tileCoordinate, Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase(): getPiece().toString();
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
