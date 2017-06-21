package main.java.com.iceteaviet.chess.core.player;

import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.piece.Pawn;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.piece.Rook;

/**
 * Move is an abstract class for represent a movement of a Chess Piece in Game Board
 * Move contains the two coordinates for piece from and to location.
 * Move also can be performed on a board or game.
 *
 * @see MoveTransition
 * @see Piece
 * <p>
 * Created by MyPC on 5/9/2017.
 */
public abstract class Move {

    private static final Move NULL_MOVE = new NullMove();
    protected final GameBoard gameBoard;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    private Move(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate) {
        this.gameBoard = gameBoard;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final GameBoard gameBoard, final int destinationCoordinate) {
        this.gameBoard = gameBoard;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;

    }

    public GameBoard getGameBoard() {
        return this.gameBoard;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;

        if (movedPiece == null)
            return result;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPosition();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Move)) {
            return false;
        }

        final Move otherMove = (Move) obj;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getMovedPiece().equals(otherMove.getMovedPiece());
    }


    public GameBoard execute() {
        final GameBoard.Builder builder = new GameBoard.Builder();
        for (final Piece piece : this.gameBoard.getCurrentPlayer().getActivePiece()) {
            if (!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        for (final Piece piece : this.gameBoard.getCurrentPlayer().getOpponent().getActivePiece()) {
            builder.setPiece(piece);
        }

        builder.setPiece(this.movedPiece.move(this));
        builder.setMoveMaker(this.gameBoard.getCurrentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    public int getCurrentCoordinate() {
        return this.getMovedPiece().getPosition();
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public GameBoard undo() {
        final GameBoard.Builder builder = new GameBoard.Builder();
        for (final Piece piece : this.gameBoard.getAllPieces()) {
            builder.setPiece(piece);
        }
        builder.setMoveMaker(this.gameBoard.getCurrentPlayer().getAlliance());
        return builder.build();
    }

    public static final class MajorMove extends Move {

        public MajorMove(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate) {
            super(gameBoard, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof MajorMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return movedPiece.getType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }


    public static class PawnPromotion extends Move {

        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getGameBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof PawnPromotion && (this.decoratedMove.equals(obj));
        }

        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public GameBoard execute() {
            final GameBoard pawnMovedBoard = this.decoratedMove.execute();
            final GameBoard.Builder builder = new GameBoard.Builder();
            for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getActivePiece()) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePiece()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().move(this));
            builder.setMoveMaker(pawnMovedBoard.getCurrentPlayer().getAlliance());
            return builder.build();

        }

        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return "";
        }
    }


    public static class AttackMove extends Move {

        private final Piece attackedPiece;

        public AttackMove(final GameBoard gameBoard,
                          final Piece pieceMoved,
                          final int destinationCoordinate,
                          final Piece pieceAttacked) {
            super(gameBoard, pieceMoved, destinationCoordinate);
            this.attackedPiece = pieceAttacked;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }


        @Override
        public boolean isAttack() {
            return true;
        }
    }

    public static final class PawnMove extends Move {

        public PawnMove(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate) {
            super(gameBoard, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof PawnMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }


    public static class MajorAttackMove extends AttackMove {
        public MajorAttackMove(final GameBoard gameBoard,
                               final Piece pieceMoved,
                               final int destinationCoordination,
                               final Piece pieceAttacked) {
            super(gameBoard, pieceMoved, destinationCoordination, pieceAttacked);

        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof MajorMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return movedPiece.getType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove {

        public PawnAttackMove(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate, final Piece attackPiece) {
            super(gameBoard, movedPiece, destinationCoordinate, attackPiece);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPosition()).substring(0, 1) + "x"
                    + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof PawnAttackMove && super.equals(obj);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate, final Piece attackPiece) {
            super(gameBoard, movedPiece, destinationCoordinate, attackPiece);
        }


        @Override
        public GameBoard execute() {
            final GameBoard.Builder builder = new GameBoard.Builder();
            for (final Piece piece : this.gameBoard.getCurrentPlayer().getActivePiece()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.gameBoard.getCurrentPlayer().getOpponent().getActivePiece()) {
                if (!piece.equals(this.getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }

            builder.setPiece(this.movedPiece.move(this));
            builder.setMoveMaker(this.gameBoard.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();

        }
    }

    public static final class PawnJump extends Move {

        public PawnJump(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate) {
            super(gameBoard, movedPiece, destinationCoordinate);
        }

        @Override
        public GameBoard execute() {

            final GameBoard.Builder builder = new GameBoard.Builder();
            for (final Piece piece : this.gameBoard.getCurrentPlayer().getActivePiece()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.gameBoard.getCurrentPlayer().getOpponent().getActivePiece()) {
                builder.setPiece(piece);
            }

            final Pawn movedPawn = (Pawn) this.movedPiece.move(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.gameBoard.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    static abstract class CastleMove extends Move {

        protected final Rook castleRook;
        protected final int castleRookStart;
        private final int castleRookDestination;

        public CastleMove(final GameBoard gameBoard,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination
        ) {
            super(gameBoard, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public GameBoard execute() {
            final GameBoard.Builder builder = new GameBoard.Builder();
            for (final Piece piece : this.gameBoard.getCurrentPlayer().getActivePiece()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.gameBoard.getCurrentPlayer().getOpponent().getActivePiece()) {
                builder.setPiece(piece);
            }

            builder.setPiece(movedPiece.move(this));
            builder.setPiece(new Rook(this.castleRook.getAlliance(), this.castleRookDestination));
            builder.setMoveMaker(this.gameBoard.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCasleMove = (CastleMove) obj;
            return super.equals(otherCasleMove) && this.castleRook.equals(otherCasleMove.getCastleRook());
        }
    }


    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate, final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(gameBoard, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof KingSideCastleMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final GameBoard gameBoard, final Piece movedPiece, final int destinationCoordinate, final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(gameBoard, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof QueenSideCastleMove && super.equals(obj);
        }


        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    public static final class NullMove extends Move {

        public NullMove() {
            super(null, 65);
        }

        @Override
        public GameBoard execute() {

            throw new RuntimeException("cannot execute the null move!");
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Not instantiable!");
        }

        public static Move createMove(final GameBoard gameBoard, final int currentCoordinate, final int destinationCoordinate) {
            if (gameBoard == null)
                return NULL_MOVE;

            for (final Move move : gameBoard.getAllLegalMoves()) {
                if (move.getDestinationCoordinate() == destinationCoordinate && move.getCurrentCoordinate() == currentCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }

}
