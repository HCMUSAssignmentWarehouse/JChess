package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtil;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.Peice.Piece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Created by MyPC on 5/12/2017.
 */
public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highLightLegalMoves;
    private final TakenPeicePanel takenPiecesPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final MoveLog moveLog;



    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static String defaultPeiceImagesPath = "icon/";
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    public Table(){
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        this.chessBoard = Board.createStandardBoard();
        final JMenuBar tableMenuBar =  createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.takenPiecesPanel = new TakenPeicePanel();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameHistoryPanel = new GameHistoryPanel();
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.moveLog = new MoveLog();

        this.boardPanel = new BoardPanel();
        this.boardDirection = BoardDirection.NORMAL;
        this.highLightLegalMoves = false;
        this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
        this.gameFrame.setVisible(true);

    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferenceMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn File");
            }
        });

        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferenceMenu(){
        final JMenu prefrenceMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposites();
                boardPanel.drawBoard(chessBoard);
            }
        });
        prefrenceMenu.add(flipBoardMenuItem);
        prefrenceMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighLighterCheckbox = new JCheckBoxMenuItem("HighLight Legal Moves",false);
        legalMoveHighLighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highLightLegalMoves = legalMoveHighLighterCheckbox.isSelected();
            }
        });

        prefrenceMenu.add(legalMoveHighLighterCheckbox);

        return prefrenceMenu;
    }

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;

         BoardPanel(){
             super(new GridLayout(0,8));
             this.boardTiles = new ArrayList<>();
             for (int i = 0; i < BoardUtil.NUM_TILES; i++) {
                 final TilePanel tilePanel = new TilePanel(this, i);
                 this.boardTiles.add(tilePanel);
                 add(tilePanel);
             }
             System.out.print(this.boardTiles.size());
             setPreferredSize(BOARD_PANEL_DIMENSION);
             validate();
        }

        public void drawBoard(final Board board){
             removeAll();
             for (final TilePanel tilePanel:boardDirection.travese(boardTiles)){
                 tilePanel.drawTile(board);
                 add(tilePanel);
             }
             validate();
             repaint();
        }

    }

    public enum BoardDirection{
        NORMAL {
            @Override
            List<TilePanel> travese(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposites() {
                return FLPPED;
            }
        },
        FLPPED {
            @Override
            List<TilePanel> travese(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposites() {
                return NORMAL;
            }
        };
        abstract List<TilePanel> travese(final List<TilePanel>boardTiles);
        abstract BoardDirection opposites();
    }

    public static class MoveLog{
        private final List<Move>moves;
        MoveLog(){
            this.moves = new ArrayList<>();
        }

        public List<Move>getMoves(){
            return this.moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }


    private class TilePanel extends JPanel{
        private final int tileId;

        TilePanel(final BoardPanel boardPanel,
                  final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePeiceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {

                    if (isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    }else if (isLeftMouseButton(e)){
                        if (sourceTile == null){
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPeice();
                            if (humanMovedPiece == null){
                                sourceTile = null;
                            }
                        }else{
                            destinationTile = chessBoard.getTile(tileId);

                            final Move move = Move.MoveFactory.createMove(chessBoard,sourceTile.getTileCoordinate(),destinationTile.getTileCoordinate());

                            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);

                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();

        }

        public void drawTile(final Board board){
            assignTileColor();
            assignTilePeiceIcon(board);
            hightLightLegals(board);
            validate();
            repaint();
        }

        private void hightLightLegals(final Board board){
            if(highLightLegalMoves){
                for (final Move move: peiceLegalMoves(board)){
                    if (move.getDestinationCoordinate() == this.tileId){
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("green_dot.png")))));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> peiceLegalMoves(final Board board){
            if (humanMovedPiece != null && humanMovedPiece.getPeiceAlliance() == board.getCurrentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMove(board);
            }
            return Collections.emptyList();
        }

        private void assignTilePeiceIcon(final Board board){
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPeiceImagesPath + board.getTile(this.tileId).getPeice().getPeiceAlliance().toString().substring(0,1)+board.getTile(this.tileId).getPeice().toString()+".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtil.EIGHTH_RANK[this.tileId] ||
                    BoardUtil.SIXTH_RANK[this.tileId] ||
                    BoardUtil.FOURTH_RANK[this.tileId] ||
                    BoardUtil.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if(BoardUtil.SEVENTH_RANK[this.tileId] ||
                    BoardUtil.FIFTH_RANK[this.tileId] ||
                    BoardUtil.THIRD_RANK[this.tileId]  ||
                    BoardUtil.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }

        }
    }


}
