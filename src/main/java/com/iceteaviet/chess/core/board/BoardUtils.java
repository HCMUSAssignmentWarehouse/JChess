package main.java.com.iceteaviet.chess.core.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import main.java.com.iceteaviet.chess.core.piece.King;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.player.MoveTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MyPC on 5/17/2017.
 */
public class BoardUtils {
    public static final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    public static final int START_TILE_INDEX = 0;
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;
    public static final List<Boolean> FIRST_COLUMN = initColumn(0);
    public static final List<Boolean> SECOND_COLUMN = initColumn(1);
    public static final List<Boolean> SEVEN_COLUMN = initColumn(6);
    public static final List<Boolean> EIGHT_COLUMN = initColumn(7);
    public static final List<Boolean> EIGHTH_RANK = initRow(0);
    public static final List<Boolean> SEVENTH_RANK = initRow(8);
    public static final List<Boolean> SIXTH_RANK = initRow(16);
    public static final List<Boolean> FIFTH_RANK = initRow(24);
    public static final List<Boolean> FOURTH_RANK = initRow(32);
    public static final List<Boolean> THIRD_RANK = initRow(40);
    public static final List<Boolean> SECOND_RANK = initRow(48);
    public static final List<Boolean> FIRST_RANK = initRow(56);
    public final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();
    private BoardUtils() {
        throw new RuntimeException("You cannot instantiate me!");
    }

    private static List<Boolean> initRow(int rowNumber) {
        final Boolean[] row = new Boolean[NUM_TILES];
        for(int i = 0; i < row.length; i++) {
            row[i] = false;
        }
        do {
            row[rowNumber] = true;
            rowNumber++;
        } while (rowNumber % NUM_TILES_PER_ROW != 0);
        return ImmutableList.copyOf(row);
    }

    private static List<Boolean> initColumn(int columnNumber) {
        final Boolean[] column = new Boolean[NUM_TILES];
        for(int i = 0; i < column.length; i++) {
            column[i] = false;
        }
        do {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        } while (columnNumber < NUM_TILES);

        return ImmutableList.copyOf(column);
    }

    private static List<String> initializeAlgebraicNotation() {
        return ImmutableList.copyOf(new String[]{
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        });
    }

    public static boolean isValidTileCoordinate(final int coordidate) {
        return coordidate >= START_TILE_INDEX && coordidate < NUM_TILES;
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }

    private Map<String, Integer> initializePositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return ImmutableMap.copyOf(positionToCoordinate);
    }

    public static boolean isThreatenedBoardImmediate(final Board board) {
        return board.whitePlayer().isInCheck() || board.blackPlayer().isInCheck();
    }

    public static boolean kingThreat(final Move move) {
        final Board board = move.getBoard();
        MoveTransition transition = board.getCurrentPlayer().makeMove(move);
        return transition.getTransitionBoard().getCurrentPlayer().isInCheck();
    }

    public static boolean isKingPawnTrap(final Board board,
                                         final King king,
                                         final int frontTile) {
        final Tile tile = board.getTile(frontTile);
        if(tile.isTileOccupied()) {
            final Piece piece = tile.getPiece();
            if(piece.getPieceType().equals(Piece.PieceType.PAWN) && piece.getPieceAlliance() != king.getPieceAlliance()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEndGame(final Board board) {
        return board.getCurrentPlayer().isInCheckMate() ||
                board.getCurrentPlayer().isInStaleMate();
    }

    public int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static int mvvlva(final Move move) {

        final Piece movingPiece = move.getMovedPiece();

        if(move.isAttack()) {
            final Piece attackedPiece = move.getAttackedPiece();
            return (attackedPiece.getPieceValue() - movingPiece.getPieceValue() +  Piece.PieceType.KING.getPieceValue()) * 100;
        }

        return Piece.PieceType.KING.getPieceValue() - movingPiece.getPieceValue();
    }

    public static List<Move> lastNMoves(final Board board, int N) {
        final List<Move> moveHistory = new ArrayList<>();
        Move currentMove = board.getTransitionMove();
        int i = 0;
        while(currentMove != Move.MoveFactory.createMove(null, -1, -1) && i < N) {
            moveHistory.add(currentMove);
            currentMove = currentMove.getBoard().getTransitionMove();
            i++;
        }
        return ImmutableList.copyOf(moveHistory);
    }
}
