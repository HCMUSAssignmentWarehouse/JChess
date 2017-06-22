package main.java.com.iceteaviet.chess.gui.layout;

import main.java.com.iceteaviet.chess.core.board.GameBoard;
import main.java.com.iceteaviet.chess.gui.ChessGameWatcher;
import main.java.com.iceteaviet.chess.gui.UIConstants;
import main.java.com.iceteaviet.chess.gui.UIUtils;
import main.java.com.iceteaviet.chess.gui.dialog.NetworkDialog;
import main.java.com.iceteaviet.chess.gui.view.Toolbar;
import main.java.com.iceteaviet.chess.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public class MainFrame extends BaseFrame {

    NetworkDialog networkDialog;

    public MainFrame(String title) {
        super(title);
        setSize(UIConstants.OUTER_FRAME_DIMENSION);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public boolean onCreateOptionsMenu(Toolbar toolbar) {
        if (toolbar != null) {
            //Add undo, redo
            try {
                ImageIcon undoIcon = UIUtils.getScaledIconFromResources(this.getClass(), "undo.png", 40, 40);
                JLabel lblUndo = new JLabel(undoIcon);
                lblUndo.setBorder(new EmptyBorder(8, 8, 8, 8));
                ImageIcon redoIcon = UIUtils.getScaledIconFromResources(this.getClass(), "redo.png", 40, 40);
                JLabel lblRedo = new JLabel(redoIcon);
                lblRedo.setBorder(new EmptyBorder(8, 8, 8, 8));

                JPanel pnl = new JPanel();
                pnl.setOpaque(false);
                pnl.add(lblUndo);
                pnl.add(lblRedo);
                JPanel eastPanel = new JPanel(new BorderLayout());
                eastPanel.setOpaque(false);
                eastPanel.add(pnl, BorderLayout.SOUTH);
                toolbar.add(eastPanel, BorderLayout.EAST);

                lblUndo.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //TODO: Restart AI when undo
                        if (ChessGameWatcher.getInstance().getMoveLog().size() > 0) {
                            ChessGameWatcher.getInstance().undoLastMove();
                        }
                    }
                });

                lblRedo.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //TODO: Redo
                    }
                });

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onCreateOptionsMenu(toolbar);
    }

    public void setMenuBarEnabled(boolean enabled) {
        if (enabled) {
            final JMenuBar tableMenuBar = createTableMenuBar();
            this.setJMenuBar(tableMenuBar);
        } else {
            this.setJMenuBar(null);
        }
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferenceMenu());
        tableMenuBar.add(createOptionsMenu());
        tableMenuBar.add(createNetworkMenu());
        return tableMenuBar;
    }

    private JMenu createNetworkMenu() {
        JMenu networkMenu = new JMenu("Network");

        JMenuItem hostMenu = new JMenuItem("Become a host");
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "lan-connect.png", 24, 24);
            hostMenu.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hostMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                networkDialog = NetworkDialog.getInstance(MainFrame.this, true);
                networkDialog.setVisible(true);
            }
        });
        networkMenu.add(hostMenu);

        JMenuItem clientMenu = new JMenuItem("Connect to existing host");
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "access-point-network.png", 24, 24);
            clientMenu.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                networkDialog = NetworkDialog.getInstance(MainFrame.this, false);
                networkDialog.setVisible(true);
            }
        });
        networkMenu.add(clientMenu);

        return networkMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem openPGN = new JMenuItem("Load PGN File", KeyEvent.VK_O);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "pgn.png", 24, 24);
            openPGN.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showOpenDialog(ChessGameWatcher.getInstance().getMainGameFrame());
                if (option == JFileChooser.APPROVE_OPTION) {
                    FileUtils.loadPGNFile(chooser.getSelectedFile());
                }
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem saveToPGN = new JMenuItem("Save Game", KeyEvent.VK_S);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "save.png", 24, 24);
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
                final int option = chooser.showSaveDialog(ChessGameWatcher.getInstance().getMainGameFrame());
                if (option == JFileChooser.APPROVE_OPTION) {
                    FileUtils.savePGNFile(chooser.getSelectedFile());
                }
            }
        });
        fileMenu.add(saveToPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);

        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "close.png", 24, 24);
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
        optionsMenu.setMnemonic(KeyEvent.VK_O);

        final JMenuItem gameRenewMenuItem = new JMenuItem("New Game", KeyEvent.VK_P);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "renew.png", 24, 24);
            gameRenewMenuItem.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameRenewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ChessGameWatcher.getInstance().renewGame();
            }

        });
        optionsMenu.add(gameRenewMenuItem);

        JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");

        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "settings.png", 24, 24);
            setupGameMenuItem.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChessGameWatcher.getInstance().getGameSetupDialog().promptUser();
                ChessGameWatcher.getInstance().setupUpdate(ChessGameWatcher.getInstance().getGameSetupDialog());
            }
        });

        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    private JMenu createPreferenceMenu() {
        final JMenu prefrenceMenu = new JMenu("Preferences");
        JMenuItem flipBoardMenuItem = new JMenuItem("Flip GameBoard");
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "flip.png", 24, 24);
            flipBoardMenuItem.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChessGameWatcher.getInstance().flipBoard();
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
                final Color colorChoice = JColorChooser.showDialog(ChessGameWatcher.getInstance().getMainGameFrame(), "Choose Dark Tile Color",
                        ChessGameWatcher.getInstance().getMainGameFrame().getBackground());
                if (colorChoice != null) {
                    ChessGameWatcher.getInstance().getBoardPanel().setTileDarkColor(colorChoice);
                }
            }
        });

        chooseLightMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Color colorChoice = JColorChooser.showDialog(ChessGameWatcher.getInstance().getMainGameFrame(), "Choose Light Tile Color",
                        ChessGameWatcher.getInstance().getMainGameFrame().getBackground());
                if (colorChoice != null) {
                    ChessGameWatcher.getInstance().getBoardPanel().setTileLightColor(colorChoice);
                }
            }
        });

        chooseLegalHighlightMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("implement me");
                //TODO: Change highlight color
                repaint();
            }
        });


        prefrenceMenu.addSeparator();
        JCheckBoxMenuItem legalMoveHighLighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", ChessGameWatcher.getInstance().isHighlightLegalMoves());
        legalMoveHighLighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChessGameWatcher.getInstance().setHighlightLegalMoves(legalMoveHighLighterCheckbox.isSelected());
            }
        });

        prefrenceMenu.add(legalMoveHighLighterCheckbox);

        return prefrenceMenu;
    }
}
