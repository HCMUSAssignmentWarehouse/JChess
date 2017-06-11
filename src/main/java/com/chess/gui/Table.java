package main.java.com.chess.gui;

import com.google.common.collect.Lists;
import main.java.com.chess.engine.board.Board;
import main.java.com.chess.engine.board.BoardUtil;
import main.java.com.chess.engine.board.Move;
import main.java.com.chess.engine.board.Tile;
import main.java.com.chess.engine.piece.Piece;
import main.java.com.chess.engine.player.MoveTransition;
import main.java.com.chess.engine.player.ai.Minimax;
import main.java.com.chess.engine.player.ai.MoveStrategy;
import main.java.com.chess.gui.layout.GameHistoryPanel;
import main.java.com.chess.gui.layout.MainFrame;
import main.java.com.chess.gui.layout.TakenPiecesPanel;
import main.java.com.chess.gui.dialog.GameSetupDialog;
import main.java.com.chess.gui.dialog.MessageBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Created by MyPC on 5/12/2017.
 */
public class Table extends Observable {
    private static final Table INSTANCE = new Table();
    private final MainFrame gameFrame;
    private final BoardPanel boardPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final MoveLog moveLog;
    private final GameSetupDialog gameSetupDialog;
    private Board chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highLightLegalMoves;
    private Move computerMove;

    private Table() {
        this.gameFrame = new MainFrame("Arthur Chess");
        this.chessBoard = Board.createStandardBoard();
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameHistoryPanel = new GameHistoryPanel();
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetupDialog = new GameSetupDialog(this.gameFrame, true);
        this.boardPanel = new BoardPanel();
        this.boardDirection = BoardDirection.NORMAL;
        this.highLightLegalMoves = true;
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setShowToolbar(true);
        this.gameFrame.setVisible(true);
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferenceMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem openPGN = new JMenuItem("Load PGN File");

        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("pgn.png", 24, 24);
            openPGN.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn File");
            }
        });

        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");

        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("close.png",24,24);
            exitMenuItem.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");

        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("settings.png", 24, 24);
            setupGameMenuItem.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetupDialog().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetupDialog());
            }
        });

        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    private void setupUpdate(final GameSetupDialog gameSetupDialog) {
        setChanged();
        notifyObservers();
    }

    private JMenu createPreferenceMenu() {
        final JMenu prefrenceMenu = new JMenu("Preferences");
        JMenuItem  flipBoardMenuItem = new JMenuItem("Flip Board");
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("flip.png", 24, 24);
            flipBoardMenuItem.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposites();
                boardPanel.drawBoard(chessBoard);
            }
        });
        prefrenceMenu.add(flipBoardMenuItem);
        prefrenceMenu.addSeparator();
        JCheckBoxMenuItem legalMoveHighLighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", highLightLegalMoves);
        legalMoveHighLighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highLightLegalMoves = legalMoveHighLighterCheckbox.isSelected();
            }
        });

        prefrenceMenu.add(legalMoveHighLighterCheckbox);

        return prefrenceMenu;
    }

    public Board getGameBoard() {
        return this.chessBoard;
    }

    public GameSetupDialog getGameSetupDialog() {
        return this.gameSetupDialog;
    }

    public MoveLog getMoveLog() {
        return this.moveLog;
    }

    public GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    public TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    public BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    public void updateComputerMove(final Move move) {
        this.computerMove = move;
    }

    private void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);

    }

    public enum PlayerType {
        HUMAN,
        COMPUTER
    }

    public enum BoardDirection {
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

        abstract List<TilePanel> travese(final List<TilePanel> boardTiles);

        abstract BoardDirection opposites();
    }

    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if (Table.get().getGameSetupDialog().isAIPlayer(Table.get().getGameBoard().getCurrentPlayer())
                    && !Table.get().getGameBoard().getCurrentPlayer().isInCheckMate()
                    && !Table.get().getGameBoard().getCurrentPlayer().isInStaleMate()) {
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();

            }

            if (Table.get().getGameBoard().getCurrentPlayer().isInCheckMate()) {
                System.out.print("Game over, " + Table.get().getGameBoard().getCurrentPlayer() + " is in checkmate!");
            }
            if (Table.get().getGameBoard().getCurrentPlayer().isInStaleMate()) {
                System.out.print("Game over, " + Table.get().getGameBoard().getCurrentPlayer() + " is in stalemate!");
            }
        }
    }

    private static class AIThinkTank extends SwingWorker<Move, String> {
        private AIThinkTank() {

        }

        @Override
        protected Move doInBackground() throws Exception {

            final MoveStrategy minimax = new Minimax(4);
            final Move bestMove = minimax.execute(Table.get().getGameBoard(), 4);
            return bestMove;
        }

        @Override
        protected void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
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

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(0, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtil.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            System.out.print(this.boardTiles.size());
            setPreferredSize(UIConstants.BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.travese(boardTiles)) {
                tilePanel.drawTile(board);
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

                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(tileId);

                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());

                            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
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

                                if (gameSetupDialog.isAIPlayer(chessBoard.getCurrentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }

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

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highLightLegals(board);
            validate();
            repaint();
        }

        private void highLightLegals(final Board board) {
            if (highLightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(UIUtils.getIconFromResources("green_dot.png")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.getCurrentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMove(board);
            }
            return Collections.emptyList();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {
                try {
                    String imgName = board.getTile(this.tileId).getPiece().getPieceAllianceLetter()
                            + board.getTile(this.tileId).getPiece().toString()
                            + UIConstants.CHESS_DRAWABLE_EXTENSION;
                    ImageIcon icon = UIUtils.getScaledIconFromResources(imgName.toLowerCase(),UIConstants.DEFAULT_PIECE_ICON_SIZE, UIConstants.DEFAULT_PIECE_ICON_SIZE);
                    add(new JLabel(icon));
                } catch (IOException e) {
                    e.printStackTrace();
                    MessageBox.showErrorByException("Unable to load piece icon from resources!", e);
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtil.EIGHTH_RANK[this.tileId] ||
                    BoardUtil.SIXTH_RANK[this.tileId] ||
                    BoardUtil.FOURTH_RANK[this.tileId] ||
                    BoardUtil.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? UIConstants.PRIMARY_COLOR : UIConstants.PRIMARY_COLOR_DARK);
            } else if (BoardUtil.SEVENTH_RANK[this.tileId] ||
                    BoardUtil.FIFTH_RANK[this.tileId] ||
                    BoardUtil.THIRD_RANK[this.tileId] ||
                    BoardUtil.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? UIConstants.PRIMARY_COLOR : UIConstants.PRIMARY_COLOR_DARK);
            }

        }
    }


}
