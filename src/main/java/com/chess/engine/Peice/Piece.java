package main.java.com.chess.engine.Peice;

import main.java.com.chess.engine.Alliance;
import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.Move;

import java.util.Collection;

/**
 * Created by MyPC on 5/9/2017.
 */
public abstract class Piece {

    protected final PeiceType peiceType;
    protected final int peicePosition;
    protected final Alliance peiceAlliance;
    protected final boolean isFirstMove;
    private final int cacheHashCode;

    Piece(final PeiceType peiceType, final int peicePosition, final Alliance peiceAlliance){
        this.peiceType = peiceType;
        this.peiceAlliance = peiceAlliance;
        this.peicePosition = peicePosition;
        this.isFirstMove = true;
        this.cacheHashCode = computeHashCode();
    }

    private int computeHashCode() {

        int result = peiceType.hashCode();
        result = 31 * result + peiceAlliance.hashCode();
        result = 31 * result + peicePosition;
        result = 31 * result + (isFirstMove ? 1: 0);

        return result;
    }

    public PeiceType getPeiceType() {
        return this.peiceType;
    }

    public boolean isFirstMove(){
        return this.isFirstMove;
    }

    public Alliance getPeiceAlliance(){
        return this.peiceAlliance;
    }
    public abstract Collection<Move> calculateLegalMove(final Board board);

    public abstract Piece movePeice(Move move);

    public Integer getPeicePosition() {
        return peicePosition;
    }

    public int getPeiceValue(){
        return this.peiceType.getPeiceValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Piece)){
            return false;
        }

        final Piece otherPiece = (Piece)obj;
        return peicePosition == otherPiece.getPeicePosition() && peiceType == otherPiece.getPeiceType()
                && peiceAlliance == otherPiece.getPeiceAlliance() && isFirstMove == otherPiece.isFirstMove();

    }

    @Override
    public int hashCode() {

        return this.cacheHashCode;
    }

    public enum PeiceType{
        PAWN("P",100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N",300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B",300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R",500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q",900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K",10000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String peiceName;
        private int peiceValue;

        PeiceType(final String peiceName,  final int peiceValue){
            this.peiceName = peiceName;
            this.peiceValue = peiceValue;
        }


        public int getPeiceValue(){
            return this.peiceValue;
        }
        public abstract boolean isKing();
        public abstract boolean isRook();
        @Override
        public String toString() {
            return this.peiceName;
        }


    }
}
