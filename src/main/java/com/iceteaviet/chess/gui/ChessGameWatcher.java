package main.java.com.iceteaviet.chess.gui;

import com.google.common.collect.Lists;
import main.java.com.iceteaviet.chess.algorithms.ChessAI;
import main.java.com.iceteaviet.chess.core.player.Alliance;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.core.board.Tile;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.player.Move;
import main.java.com.iceteaviet.chess.core.player.MoveTransition;
import main.java.com.iceteaviet.chess.core.player.Player;
import main.java.com.iceteaviet.chess.gui.dialog.GameSetupDialog;
import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;
import main.java.com.iceteaviet.chess.gui.layout.GameHistoryPanel;
import main.java.com.iceteaviet.chess.gui.layout.MainFrame;
import main.java.com.iceteaviet.chess.gui.layout.RightMenuPanel;
import main.java.com.iceteaviet.chess.gui.layout.TakenPiecesPanel;
import main.java.com.iceteaviet.chess.network.NetworkManager;
import main.res.values.string;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * ChessGameWatcher is an game manager, it controls all flow of chess game
 * <p>
 * Created by MyPC on 5/12/2017.
 */
public class ChessGameWatcher extends Observable {
    private static final ChessGameWatcher INSTANCE = new ChessGameWatcher();
    private final MainFrame gameFrame;
    private final BoardPanel boardPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final RightMenuPanel rightMenuPanel;
    private final MoveLog moveLog;
    private final GameSetupDialog gameSetupDialog;
    private GameBoard chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves = true;
    private Player mainPlayer; //Bottom side player
    private boolean isNetPlay = false;
    private Move computerMove;

    private ChessGameWatcher() {
        this.gameFrame = new MainFrame(string.game_name);
        this.chessBoard = GameBoard.createStandardBoard();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.rightMenuPanel = new RightMenuPanel();
        this.gameFrame.add(this.rightMenuPanel, BorderLayout.EAST);
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetupDialog = new GameSetupDialog(this.gameFrame, true);
        this.boardPanel = new BoardPanel();
        this.boardDirection = BoardDirection.NORMAL;
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setShowToolbar(true);
    }

    public static ChessGameWatcher getInstance() {
        return INSTANCE;
    }

    public void show() {
        this.gameFrame.setMenuBarEnabled(true);
        this.gameFrame.setVisible(true);
        ChessGameWatcher.getInstance().getMoveLog().clear();
        ChessGameWatcher.getInstance().getGameHistoryPanel().redo(chessBoard, ChessGameWatcher.getInstance().getMoveLog());
        ChessGameWatcher.getInstance().getTakenPiecesPanel().redo(ChessGameWatcher.getInstance().getMoveLog());
        ChessGameWatcher.getInstance().getBoardPanel().drawBoard(ChessGameWatcher.getInstance().getGameBoard());
    }

    public Component getMainGameFrame() {
        return this.gameFrame;
    }

    public void movePiece(int currCoord, int desCoord) {
        final Move move = Move.MoveFactory.createMove(chessBoard, currCoord, desCoord);

        final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
        if (transition.getMoveStatus().isDone()) {
            chessBoard = transition.getTransitionBoard();
            moveLog.addMove(move);

            //Switch the clock
            if (move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
                //White finished moving, now turn on the black clock
                this.getRightMenuPanel().getChronometerW().pause();
                this.getRightMenuPanel().getChronometerB().startOrResume();
            } else {
                this.getRightMenuPanel().getChronometerB().pause();
                this.getRightMenuPanel().getChronometerW().startOrResume();
            }
        }
    }

    public void undoLastMove() {
        final Move lastMove = ChessGameWatcher.getInstance().getMoveLog().removeMove(ChessGameWatcher.getInstance().getMoveLog().size() - 1);
        this.chessBoard = this.chessBoard.getCurrentPlayer().unMakeMove(lastMove).getTransitionBoard();
        this.computerMove = null;
        ChessGameWatcher.getInstance().getMoveLog().removeMove(lastMove);
        ChessGameWatcher.getInstance().getGameHistoryPanel().redo(chessBoard, ChessGameWatcher.getInstance().getMoveLog());
        ChessGameWatcher.getInstance().getTakenPiecesPanel().redo(ChessGameWatcher.getInstance().getMoveLog());
        ChessGameWatcher.getInstance().getBoardPanel().drawBoard(chessBoard);
    }

    public void setupUpdate(final GameSetupDialog gameSetupDialog) {
        setChanged();
        notifyObservers();
    }

    public Player getMainPlayer() {
        return mainPlayer;
    }

    public void setMainPlayer(Player mainPlayer) {
        this.mainPlayer = mainPlayer;
    }

    public boolean isNetPlay() {
        return isNetPlay;
    }

    public void setNetPlay(boolean isNetPlay) {
        this.isNetPlay = isNetPlay;
    }

    public GameBoard getGameBoard() {
        return this.chessBoard;
    }

    public RightMenuPanel getRightMenuPanel() {
        return this.rightMenuPanel;
    }

    public GameSetupDialog getGameSetupDialog() {
        return this.gameSetupDialog;
    }

    public MoveLog getMoveLog() {
        return this.moveLog;
    }

    public GameHistoryPanel getGameHistoryPanel() {
        return this.rightMenuPanel.getGameHistoryPanel();
    }

    public TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    public BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    public void updateGameBoard(final GameBoard gameBoard) {
        this.chessBoard = gameBoard;
    }

    public void updateAIMove(final Move move) {
        this.computerMove = move;
    }

    public void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    public boolean isHighlightLegalMoves() {
        return highlightLegalMoves;
    }

    public void setHighlightLegalMoves(boolean b) {
        this.highlightLegalMoves = b;
    }

    public void flipBoard() {
        boardDirection = boardDirection.opposites();
        boardPanel.drawBoard(chessBoard);
    }

    public void onEndGame() {
        ChessGameWatcher.getInstance().rightMenuPanel.getChronometerB().stop();
        ChessGameWatcher.getInstance().rightMenuPanel.getChronometerW().stop();
    }

    public void drawBoardAfterMove() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                rightMenuPanel.getGameHistoryPanel().redo(chessBoard, moveLog);
                takenPiecesPanel.redo(moveLog);

                //if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                ChessGameWatcher.getInstance().moveMadeUpdate(PlayerType.HUMAN);
                //}

                boardPanel.drawBoard(chessBoard);
            }
        });
    }

    public enum PlayerType {
        HUMAN,
        COMPUTER
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposites() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposites() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        abstract BoardDirection opposites();
    }

    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if (ChessGameWatcher.getInstance().getGameSetupDialog().isAIPlayer(ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer())
                    && !ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer().isInCheckMate()
                    && !ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer().isInStaleMate()) {
                ChessAI.getInstance(ChessGameWatcher.getInstance().gameSetupDialog.isAlphaBetaStock()).move();
            }

            if (ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer().isInCheckMate()) {
                System.out.print("Game over, " + ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer() + " is in checkmate!");
                MessageBox.showInfo("Game Over: Player " + ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer() + " is in checkmate!", "Game Over");
                ChessGameWatcher.getInstance().onEndGame();
            }
            if (ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer().isInStaleMate()) {
                System.out.print("Game over, " + ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer() + " is in stalemate!");
                MessageBox.showInfo("Game Over: Player " + ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer() + " is in stalemate!", "Game Over");
                ChessGameWatcher.getInstance().onEndGame();
            }
        }
    }


    public static class MoveLog {
        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    public class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.TILE_COUNT; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            System.out.print(this.boardTiles.size());
            setPreferredSize(UIConstants.BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setBackground(UIConstants.PRIMARY_BORDER_COLOR);
            validate();
        }

        public void drawBoard(final GameBoard gameBoard) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(gameBoard);
                add(tilePanel);
            }
            validate();
            repaint();
        }

    }

    private class TilePanel extends JPanel {
        private final int tileId;

        TilePanel(final BoardPanel boardPanel,
                  final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(UIConstants.TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {

                    if (ChessGameWatcher.getInstance().getGameSetupDialog().isAIPlayer(ChessGameWatcher.getInstance().getGameBoard().getCurrentPlayer()) ||
                            BoardUtils.isEndGame(ChessGameWatcher.getInstance().getGameBoard())) {
                        return;
                    }

                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);

                            if (isNetPlay) {
                                if (sourceTile.getPiece() != null
                                        && !sourceTile.getPiece().getAlliance().equals(mainPlayer.getAlliance())) {
                                    //Opponent chess
                                    sourceTile = null;
                                    MessageBox.showWarning(string.warning_cannot_move_opponent_chess, string.chess_network_play);
                                    return;
                                } else {
                                    //Same alliance
                                    if (!NetworkManager.getInstance().isConnected()) {
                                        //But connection between server and client is not established
                                        sourceTile = null;
                                        MessageBox.showWarning(string.warning_wait_connection, string.chess_network_play);
                                        return;
                                    }
                                }
                            }

                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(tileId);

                            movePiece(sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            if (isNetPlay && NetworkManager.getInstance().isConnected()) {
                                NetworkManager.getInstance().sendMoveMessages(sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            }

                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        drawBoardAfterMove();
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

        public void drawTile(final GameBoard gameBoard) {
            assignTileColor();
            assignTilePieceIcon(gameBoard);
            highLightLegals(gameBoard);
            validate();
            repaint();
        }

        private void highLightLegals(final GameBoard gameBoard) {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(gameBoard)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(UIUtils.getIconFromResources(this.getClass(), "green_dot.png")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final GameBoard gameBoard) {
            if (humanMovedPiece != null && humanMovedPiece.getAlliance() == gameBoard.getCurrentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMove(gameBoard);
            }
            return Collections.emptyList();
        }

        private void assignTilePieceIcon(final GameBoard gameBoard) {
            this.removeAll();
            if (gameBoard.getTile(this.tileId).isTileOccupied()) {
                try {
                    String imgName = gameBoard.getTile(this.tileId).getPiece().getAllianceLetter()
                            + gameBoard.getTile(this.tileId).getPiece().toString()
                            + UIConstants.CHESS_DRAWABLE_EXTENSION;
                    ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), imgName.toLowerCase(), UIConstants.DEFAULT_PIECE_ICON_SIZE, UIConstants.DEFAULT_PIECE_ICON_SIZE);
                    add(new JLabel(icon));
                } catch (IOException e) {
                    e.printStackTrace();
                    MessageBox.showErrorByException(string.error_cannot_load_image, e);
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK.get(this.tileId) ||
                    BoardUtils.SIXTH_RANK.get(this.tileId) ||
                    BoardUtils.FOURTH_RANK.get(this.tileId) ||
                    BoardUtils.SECOND_RANK.get(this.tileId)) {
                setBackground(this.tileId % 2 == 0 ? UIConstants.PRIMARY_COLOR : UIConstants.PRIMARY_COLOR_DARK);
            } else if (BoardUtils.SEVENTH_RANK.get(this.tileId) ||
                    BoardUtils.FIFTH_RANK.get(this.tileId) ||
                    BoardUtils.THIRD_RANK.get(this.tileId) ||
                    BoardUtils.FIRST_RANK.get(this.tileId)) {
                setBackground(this.tileId % 2 != 0 ? UIConstants.PRIMARY_COLOR : UIConstants.PRIMARY_COLOR_DARK);
            }

        }
    }
}
