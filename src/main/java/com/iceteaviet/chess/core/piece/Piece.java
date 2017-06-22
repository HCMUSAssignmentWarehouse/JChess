package main.java.com.iceteaviet.chess.core.piece;

import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.player.Alliance;
import main.java.com.iceteaviet.chess.core.player.Move;

import java.util.Collection;

/**
 * Created by NhaTran on 5/9/2017.
 * <p>
 * Base class of Chess Pieces.
 */
public abstract class Piece {
    protected static final boolean DEFAULT_FIRST_MOVE = true;
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cacheHashCode;

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance) {
        this(pieceType, piecePosition, pieceAlliance, DEFAULT_FIRST_MOVE);
    }

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        this.cacheHashCode = getHashCode();
    }

    private int getHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);

        return result;
    }

    public abstract int locationBonus();

    public PieceType getType() {
        return this.pieceType;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public Alliance getAlliance() {
        return this.pieceAlliance;
    }

    public abstract Collection<Move> calculateLegalMove(final GameBoard gameBoard);

    public abstract Piece move(Move move);

    public Integer getPosition() {
        return piecePosition;
    }

    public int getValue() {
        return this.pieceType.getPieceValue();
    }

    public String getAllianceLetter() {
        return getAlliance().toString().substring(0, 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Piece)) {
            return false;
        }

        final Piece otherPiece = (Piece) obj;
        return piecePosition == otherPiece.getPosition() && pieceType == otherPiece.getType()
                && pieceAlliance == otherPiece.getAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cacheHashCode;
    }

    public enum PieceType {
        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K", 10000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }


        public int getPieceValue() {
            return this.pieceValue;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();

        @Override
        public String toString() {
            return this.pieceName;
        }
    }
}
