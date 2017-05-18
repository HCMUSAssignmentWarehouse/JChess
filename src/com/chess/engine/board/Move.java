package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.Peice.Pawn;
import com.chess.engine.Peice.Piece;
import com.chess.engine.Peice.Rook;

/**
 * Created by MyPC on 5/9/2017.
 */
public abstract class Move {

    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board, final int destinationCoordinate){
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;

    }

    public Board getBoard(){
        return this.board;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPeicePosition();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Move)){
            return false;
        }

        final Move otherMove = (Move)obj;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getMovedPiece().equals(otherMove.getMovedPiece());
    }


    public Board execute() {
        final Builder builder = new Builder();
        for(final Piece piece :this.board.getCurrentPlayer().getActivePeice()){
            if (!this.movedPiece.equals(piece)){
                builder.setPeice(piece);
            }
        }

        for (final Piece piece :this.board.getCurrentPlayer().getOpponent().getActivePeice()){
            builder.setPeice(piece);
        }

        builder.setPeice(this.movedPiece.movePeice(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
        return  builder.build();
    }

    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public int getCurrentCoordinate() {
        return this.getMovedPiece().getPeicePosition();
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public static final class MajorMove extends Move{

        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof MajorMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return movedPiece.getPeiceType().toString()+ BoardUtil.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }


    public static class PawnPromotion extends Move{

        final Move decoratedMove;
        final Pawn promotedPawn;
        public PawnPromotion(final Move decoratedMove){
            super(decoratedMove.getBoard(),decoratedMove.getMovedPiece(),decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn)decoratedMove.getMovedPiece();
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
        public Board execute() {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getActivePeice()) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPeice(piece);
                }
            }
            for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePeice()) {
                builder.setPeice(piece);
            }
            builder.setPeice(this.promotedPawn.getPromotionPiece().movePeice(this));
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



    public static class AttackMove extends Move{

        private final Piece attackedPiece;

        public AttackMove(final Board board,
                   final Piece pieceMoved,
                   final int destinationCoordinate,
                   final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate);
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

    public static final class PawnMove extends Move{

        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof PawnMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return BoardUtil.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }


    public static class MajorAttackMove extends AttackMove{
        public MajorAttackMove(final Board board,
                               final Piece pieceMoved,
                               final int destinationCoordination,
                               final Piece pieceAttacked){
            super(board, pieceMoved,destinationCoordination, pieceAttacked);

        }
        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof MajorMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return movedPiece.getPeiceType()+ BoardUtil.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackPiece) {
            super(board, movedPiece, destinationCoordinate, attackPiece);
        }

        @Override
        public String toString() {
            return BoardUtil.getPositionAtCoordinate(this.movedPiece.getPeicePosition()).substring(0,1) +"x"
                    + BoardUtil.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof PawnAttackMove && super.equals(obj);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove{

        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackPiece) {
            super(board, movedPiece, destinationCoordinate, attackPiece);
        }


        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece :this.board.getCurrentPlayer().getActivePeice()){
                if (!this.movedPiece.equals(piece)){
                    builder.setPeice(piece);
                }
            }

            for (final Piece piece :this.board.getCurrentPlayer().getOpponent().getActivePeice()){
                if (!piece.equals(this.getAttackedPiece())){
                    builder.setPeice(piece);
                }
            }

            builder.setPeice(this.movedPiece.movePeice(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();

        }
    }

    public static final class PawnJump extends Move{

        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {

            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePeice()){
                if (!this.movedPiece.equals(piece)){
                    builder.setPeice(piece);
                }
            }

            for (final Piece piece :this.board.getCurrentPlayer().getOpponent().getActivePeice()){
                builder.setPeice(piece);
            }

            final Pawn movedPawn = (Pawn)this.movedPiece.movePeice(this);
            builder.setPeice(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtil.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    static abstract class CastleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStart;
        private final int castleRookDestination;

        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination
        ) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePeice()){
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPeice(piece);
                }
            }

            for (final Piece piece :this.board.getCurrentPlayer().getOpponent().getActivePeice()){
                builder.setPeice(piece);
            }

            builder.setPeice(movedPiece.movePeice(this));
            builder.setPeice(new Rook(this.castleRook.getPeiceAlliance(),this.castleRookDestination));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
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
            if (this == obj){
                return true;
            }
            if (!(obj instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCasleMove = (CastleMove)obj;
            return super.equals(otherCasleMove)&& this.castleRook.equals(otherCasleMove.getCastleRook());
        }
    }


    public static final class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate,castleRook,castleRookStart,castleRookDestination);
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

    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate,castleRook,castleRookStart,castleRookDestination);
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

    public static final class NullMove extends Move{

        public NullMove() {
            super(null, 65);
        }

        @Override
        public Board execute() {

            throw new RuntimeException("cannot execute the null move!");
        }

        @Override
        public int getCurrentCoordinate() {
                return -1;
        }
    }

    public static class MoveFactory{
        private MoveFactory(){
            throw new RuntimeException("Not instantiable!");
        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate){
            for (final Move move:board.getAllLegalMoves()){
                if (move.getDestinationCoordinate() == destinationCoordinate && move.getCurrentCoordinate() == currentCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }

}
