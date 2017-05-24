package main.java.com.chess.engine.Peice;

import main.java.com.chess.engine.Alliance;
import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.BoardUtil;
import main.java.com.chess.engine.board.Move;
import main.java.com.chess.engine.board.Move.MajorAttackMove;
import main.java.com.chess.engine.board.Move.MajorMove;
import main.java.com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MyPC on 5/10/2017.
 */
public class Bishop extends Piece {
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9,-7,7,9};

    public Bishop(Alliance peiceAlliance, int peicePosition) {
        super(PeiceType.BISHOP,peicePosition, peiceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMove(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES){
            int candidateDestinationCoordinate = this.peicePosition;

            while (BoardUtil.isValidTileCoordinate(candidateDestinationCoordinate)){

                if(isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset) ||
                        isEightColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset)){
                    System.out.println("cot cam: "+ candidateDestinationCoordinate);
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;
                if (BoardUtil.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    }
                    else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPeice();
                        final Alliance pieceAlliance = pieceAtDestination.getPeiceAlliance();
                        if (this.peiceAlliance != pieceAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
                                    pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }



        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Bishop movePeice(final Move move) {
        return new Bishop(move.getMovedPiece().getPeiceAlliance(),move.getDestinationCoordinate());
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    @Override
    public String toString() {
        return PeiceType.BISHOP.toString();
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtil.EIGHT_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }

}
