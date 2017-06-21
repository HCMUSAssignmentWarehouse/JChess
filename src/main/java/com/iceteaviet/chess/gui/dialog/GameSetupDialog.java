package main.java.com.iceteaviet.chess.gui.dialog;

/**
 * Created by MyPC on 5/26/2017.
 */

import main.java.com.iceteaviet.chess.algorithms.ChessAI;
import main.java.com.iceteaviet.chess.core.player.Alliance;
import main.java.com.iceteaviet.chess.core.player.Player;
import main.java.com.iceteaviet.chess.gui.ChessGameWatcher;
import main.java.com.iceteaviet.chess.gui.layout.MainFrame;
import main.res.values.string;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameSetupDialog extends BaseDialog {
    private ChessGameWatcher.PlayerType whitePlayerType = ChessGameWatcher.PlayerType.HUMAN;
    private ChessGameWatcher.PlayerType blackPlayerType = ChessGameWatcher.PlayerType.COMPUTER;
    private JSpinner searchDepthSpinner;
    private JRadioButton btnAlphaBeta;

    public GameSetupDialog(MainFrame frame, boolean modal) {
        super(frame, modal);
        setTitle(string.game_setup_dialog_title);
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        final JRadioButton whiteHumanButton = new JRadioButton(string.human_label);
        final JRadioButton whiteComputerButton = new JRadioButton(string.computer_label);
        final JRadioButton blackHumanButton = new JRadioButton(string.human_label);
        final JRadioButton blackComputerButton = new JRadioButton(string.computer_label);

        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteHumanButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackComputerButton.setSelected(true); //Default black player is AI

        btnAlphaBeta = new JRadioButton(string.alpha_beta_name);
        btnAlphaBeta.setSelected(true);
        JRadioButton btnMiniMax = new JRadioButton(string.minimax_name);
        final ButtonGroup algorithmGroup = new ButtonGroup();
        algorithmGroup.add(btnAlphaBeta);
        algorithmGroup.add(btnMiniMax);

        getContentPane().add(myPanel);
        myPanel.add(new JLabel(string.player_white));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerButton);
        myPanel.add(new JLabel(string.player_black));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerButton);

        myPanel.add(new JLabel("Search"));
        myPanel.add(btnAlphaBeta);
        myPanel.add(btnMiniMax);
        this.searchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth", new SpinnerNumberModel(4, 0, Integer.MAX_VALUE, 1));

        final JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                whitePlayerType = whiteComputerButton.isSelected() ? ChessGameWatcher.PlayerType.COMPUTER : ChessGameWatcher.PlayerType.HUMAN;
                blackPlayerType = blackComputerButton.isSelected() ? ChessGameWatcher.PlayerType.COMPUTER : ChessGameWatcher.PlayerType.HUMAN;
                GameSetupDialog.this.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                GameSetupDialog.this.setVisible(false);
            }
        });

        myPanel.add(cancelButton);
        myPanel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    private static JSpinner addLabeledSpinner(Container c, String label, SpinnerModel model) {
        final JLabel l = new JLabel(label);
        c.add(l);
        final JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);
        return spinner;
    }

    public void promptUser() {
        setVisible(true);
        repaint();
    }

    public boolean isAIPlayer(final Player player) {
        if (player.getAlliance() == Alliance.WHITE) {
            return getWhitePlayerType() == ChessGameWatcher.PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == ChessGameWatcher.PlayerType.COMPUTER;
    }

    public ChessGameWatcher.PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    public ChessGameWatcher.PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }

    public int getSearchDepth() {
        return (Integer) this.searchDepthSpinner.getValue();
    }

    public boolean isAlphaBetaStock() {
        return btnAlphaBeta == null ? ChessAI.DEFAULT_ALPHA_BETA_ENABLED : btnAlphaBeta.isSelected();
    }
}