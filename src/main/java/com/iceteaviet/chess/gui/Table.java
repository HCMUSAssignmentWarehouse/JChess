package main.java.com.iceteaviet.chess.gui;

import com.google.common.collect.Lists;
import main.java.com.iceteaviet.chess.core.board.Board;
import main.java.com.iceteaviet.chess.core.board.BoardUtils;
import main.java.com.iceteaviet.chess.core.board.Move;
import main.java.com.iceteaviet.chess.core.board.Tile;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.core.player.MoveTransition;
import main.java.com.iceteaviet.chess.algorithms.Minimax;
import main.java.com.iceteaviet.chess.algorithms.MoveStrategy;
import main.java.com.iceteaviet.chess.gui.layout.GameHistoryPanel;
import main.java.com.iceteaviet.chess.gui.layout.MainFrame;
import main.java.com.iceteaviet.chess.gui.layout.TakenPiecesPanel;
import main.java.com.iceteaviet.chess.gui.dialog.GameSetupDialog;
import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;
import main.java.com.iceteaviet.chess.utils.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
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
    private boolean highLightLegalMoves = true;
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
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setShowToolbar(true);
        this.gameFrame.setVisible(true);
    }

    public static Table getInstance() {
        return INSTANCE;
    }

    public void show() {
        Table.getInstance().getMoveLog().clear();
        Table.getInstance().getGameHistoryPanel().redo(chessBoard, Table.getInstance().getMoveLog());
        Table.getInstance().getTakenPiecesPanel().redo(Table.getInstance().getMoveLog());
        Table.getInstance().getBoardPanel().drawBoard(Table.getInstance().getGameBoard());
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
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem openPGN = new JMenuItem("Load PGN File", KeyEvent.VK_O);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("pgn.png", 24, 24);
            openPGN.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showOpenDialog(Table.getInstance().getMainGameFrame());
                if (option == JFileChooser.APPROVE_OPTION) {
                    FileUtils.loadPGNFile(chooser.getSelectedFile());
                }
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem saveToPGN = new JMenuItem("Save Game", KeyEvent.VK_S);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("save.png", 24, 24);
            saveToPGN.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveToPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        return ".pgn";
                    }
                    @Override
                    public boolean accept(final File file) {
                        return file.isDirectory() || file.getName().toLowerCase().endsWith("pgn");
                    }
                });
                final int option = chooser.showSaveDialog(Table.getInstance().getMainGameFrame());
                if (option == JFileChooser.APPROVE_OPTION) {
                    FileUtils.savePGNFile(chooser.getSelectedFile());
                }
            }
        });
        fileMenu.add(saveToPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);

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

    private Component getMainGameFrame() {
        return this.gameFrame;
    }

    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);

        final JMenuItem gameRenewMenuItem = new JMenuItem("New Game", KeyEvent.VK_P);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("renew.png", 24, 24);
            gameRenewMenuItem.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameRenewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //TODO: Re-new the game

            }

        });
        optionsMenu.add(gameRenewMenuItem);

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
                Table.getInstance().getGameSetupDialog().promptUser();
                Table.getInstance().setupUpdate(Table.getInstance().getGameSetupDialog());
            }
        });

        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    public void undoLastMove() {
        final Move lastMove = Table.getInstance().getMoveLog().removeMove(Table.getInstance().getMoveLog().size() - 1);
        this.chessBoard = this.chessBoard.getCurrentPlayer().unMakeMove(lastMove).getTransitionBoard();
        this.computerMove = null;
        Table.getInstance().getMoveLog().removeMove(lastMove);
        Table.getInstance().getGameHistoryPanel().redo(chessBoard, Table.getInstance().getMoveLog());
        Table.getInstance().getTakenPiecesPanel().redo(Table.getInstance().getMoveLog());
        Table.getInstance().getBoardPanel().drawBoard(chessBoard);
        //Table.getInstance().getDebugPanel().redo();
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


        final JMenu colorChooserSubMenu = new JMenu("Choose Colors");
        colorChooserSubMenu.setMnemonic(KeyEvent.VK_S);

        final JMenuItem chooseDarkMenuItem = new JMenuItem("Choose Dark Tile Color");
        colorChooserSubMenu.add(chooseDarkMenuItem);

        final JMenuItem chooseLightMenuItem = new JMenuItem("Choose Light Tile Color");
        colorChooserSubMenu.add(chooseLightMenuItem);

        final JMenuItem chooseLegalHighlightMenuItem = new JMenuItem(
                "Choose Legal Move Highlight Color");
        colorChooserSubMenu.add(chooseLegalHighlightMenuItem);

        prefrenceMenu.add(colorChooserSubMenu);

        chooseDarkMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Color colorChoice = JColorChooser.showDialog(Table.getInstance().getMainGameFrame(), "Choose Dark Tile Color",
                        Table.getInstance().getMainGameFrame().getBackground());
                if (colorChoice != null) {
                    //TODO: Set chess board dark color
                    //Table.getInstance().getBoardPanel().setTileDarkColor(chessBoard, colorChoice);
                }
            }
        });

        chooseLightMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Color colorChoice = JColorChooser.showDialog(Table.getInstance().getMainGameFrame(), "Choose Light Tile Color",
                        Table.getInstance().getMainGameFrame().getBackground());
                if (colorChoice != null) {
                    //TODO: Set chess board light color
                    //Table.getInstance().getBoardPanel().setTileLightColor(chessBoard, colorChoice);
                }
            }
        });

        chooseLegalHighlightMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("implement me");
                //TODO: Change highlight color
                //Table.getInstance().getGameFrame().repaint();
            }
        });


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
            if (Table.getInstance().getGameSetupDialog().isAIPlayer(Table.getInstance().getGameBoard().getCurrentPlayer())
                    && !Table.getInstance().getGameBoard().getCurrentPlayer().isInCheckMate()
                    && !Table.getInstance().getGameBoard().getCurrentPlayer().isInStaleMate()) {
                final AIAsyncWorkerTask thinkTank = new AIAsyncWorkerTask();
                thinkTank.execute();
            }

            if (Table.getInstance().getGameBoard().getCurrentPlayer().isInCheckMate()) {
                System.out.print("Game over, " + Table.getInstance().getGameBoard().getCurrentPlayer() + " is in checkmate!");
                MessageBox.showInfo("Game Over: Player " + Table.getInstance().getGameBoard().getCurrentPlayer() + " is in checkmate!", "Game Over");
            }
            if (Table.getInstance().getGameBoard().getCurrentPlayer().isInStaleMate()) {
                System.out.print("Game over, " + Table.getInstance().getGameBoard().getCurrentPlayer() + " is in stalemate!");
                MessageBox.showInfo("Game Over: Player " + Table.getInstance().getGameBoard().getCurrentPlayer() + " is in stalemate!", "Game Over");
            }
        }
    }

    private static class AIAsyncWorkerTask extends SwingWorker<Move, String> {
        private AIAsyncWorkerTask() {

        }

        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy minimax = new Minimax(4);
            final Move bestMove = minimax.execute(Table.getInstance().getGameBoard(), 4);
            return bestMove;
        }

        @Override
        protected void done() {
            try {
                final Move bestMove = get();
                Table.getInstance().updateComputerMove(bestMove);
                Table.getInstance().updateGameBoard(Table.getInstance().getGameBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.getInstance().getMoveLog().addMove(bestMove);
                Table.getInstance().getGameHistoryPanel().redo(Table.getInstance().getGameBoard(), Table.getInstance().getMoveLog());
                Table.getInstance().getTakenPiecesPanel().redo(Table.getInstance().getMoveLog());
                Table.getInstance().getBoardPanel().drawBoard(Table.getInstance().getGameBoard());
                //TODO: Redo debug panel
                Table.getInstance().moveMadeUpdate(PlayerType.COMPUTER);

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
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            System.out.print(this.boardTiles.size());
            setPreferredSize(UIConstants.BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setBackground(Color.decode("#8B4726"));
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

                    if(Table.getInstance().getGameSetupDialog().isAIPlayer(Table.getInstance().getGameBoard().getCurrentPlayer()) ||
                            BoardUtils.isEndGame(Table.getInstance().getGameBoard())) {
                        return;
                    }

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

                                //if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                Table.getInstance().moveMadeUpdate(PlayerType.HUMAN);
                                //}

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
