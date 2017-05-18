package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.Peice.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

/**
 * Created by MyPC on 5/9/2017.
 */
public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Pawn enPassantPawn;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private Board(Builder builder){
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePeices(this.gameBoard,Alliance.WHITE);
        this.blackPieces = calculateActivePeices(this.gameBoard,Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardLegalMoves = calculateLegalMove(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMove(this.blackPieces);

        whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves,blackStandardLegalMoves);
        blackPlayer = new BlackPlayer(this,whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString(){
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < BoardUtil.NUM_TILES ; i++){
            final String tileText = this.gameBoard.get(i).toString();
            stringBuilder.append(String.format("%3s",tileText));
            if ((i+1)%BoardUtil.NUM_TILES_PER_ROW == 0){
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Player whitePlayer(){
        return this.whitePlayer;
    }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    private Collection<Move> calculateLegalMove(final Collection<Piece> pieces) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece : pieces){
            Piece tem = piece;
            legalMoves.addAll(piece.calculateLegalMove(this));
        }

        return ImmutableList.copyOf(legalMoves);
    }

    private Collection<Piece> calculateActivePeices(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        for(final Tile tile: gameBoard){
            if (tile.isTileOccupied()){
                final Piece piece = tile.getPeice();
                if (piece.getPeiceAlliance() == alliance){
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    private List<Tile> createGameBoard(Builder builder) {

        final Tile[] tiles = new Tile[BoardUtil.NUM_TILES];
        for (int i = 0 ; i < BoardUtil.NUM_TILES ; i ++){
            tiles[i] = Tile.createTile(i,builder.boardConfig.get(i));
        }

        return ImmutableList.copyOf(tiles);
    }

    public static Board createStandardBoard(){
        final Builder builder= new Builder();
        builder.setPeice(new Rook(Alliance.BLACK,0));
        builder.setPeice(new Knight(Alliance.BLACK,1));
        builder.setPeice(new Bishop(Alliance.BLACK,2));
        builder.setPeice(new Queen(Alliance.BLACK,3));
        builder.setPeice(new King(Alliance.BLACK,4));
        builder.setPeice(new Bishop(Alliance.BLACK,5));
        builder.setPeice(new Knight(Alliance.BLACK,6));
        builder.setPeice(new Rook(Alliance.BLACK,7));
        builder.setPeice(new Pawn(Alliance.BLACK,8));
        builder.setPeice(new Pawn(Alliance.BLACK,9));
        builder.setPeice(new Pawn(Alliance.BLACK,10));
        builder.setPeice(new Pawn(Alliance.BLACK,11));
        builder.setPeice(new Pawn(Alliance.BLACK,12));
        builder.setPeice(new Pawn(Alliance.BLACK,13));
        builder.setPeice(new Pawn(Alliance.BLACK,14));
        builder.setPeice(new Pawn(Alliance.BLACK,15));


        builder.setPeice(new Pawn(Alliance.WHITE,48));
        builder.setPeice(new Pawn(Alliance.WHITE,49));
        builder.setPeice(new Pawn(Alliance.WHITE,50));
        builder.setPeice(new Pawn(Alliance.WHITE,51));
        builder.setPeice(new Pawn(Alliance.WHITE,52));
        builder.setPeice(new Pawn(Alliance.WHITE,53));
        builder.setPeice(new Pawn(Alliance.WHITE,54));
        builder.setPeice(new Pawn(Alliance.WHITE,55));
        builder.setPeice(new Rook(Alliance.WHITE,56));
        builder.setPeice(new Knight(Alliance.WHITE,57));
        builder.setPeice(new Bishop(Alliance.WHITE,58));
        builder.setPeice(new Queen(Alliance.WHITE,59));
        builder.setPeice(new King(Alliance.WHITE,60));
        builder.setPeice(new Bishop(Alliance.WHITE,61));
        builder.setPeice(new Knight(Alliance.WHITE,62));
        builder.setPeice(new Rook(Alliance.WHITE,63));

        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();

    }

    public Tile getTile(final int tileCoordinate){

        return gameBoard.get(tileCoordinate);
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(),this.blackPlayer.getLegalMoves()));
    }

    public static class Builder{

        Map<Integer,Piece> boardConfig;
        Alliance nextMoveMaker;
        private Pawn enPassantPawn;

        public Builder(){
            this.boardConfig = new HashMap<>();
        }

        public Builder setPeice(final Piece piece){
            this.boardConfig.put(piece.getPeicePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
