package main.java.com.iceteaviet.chess.gui.layout;

import com.google.common.primitives.Ints;
import main.java.com.iceteaviet.chess.core.board.Move;
import main.java.com.iceteaviet.chess.core.piece.Piece;
import main.java.com.iceteaviet.chess.gui.Table;
import main.java.com.iceteaviet.chess.gui.UIConstants;
import main.java.com.iceteaviet.chess.gui.UIUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by MyPC on 5/17/2017.
 */
public class TakenPiecesPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private final JPanel northPanel;
    private final JPanel southPanel;

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(UIConstants.PRIMARY_BG_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(UIConstants.PRIMARY_BG_COLOR);
        this.southPanel.setBackground(UIConstants.PRIMARY_BG_COLOR);
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(UIConstants.LEFT_TAKEN_PIECES_PANEL_DIMENSION);
    }

    public void redo(final Table.MoveLog moveLog) {
        southPanel.removeAll();
        northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if (takenPiece.getPieceAlliance().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else if (takenPiece.getPieceAlliance().isBlack()) {
                    blackTakenPieces.add(takenPiece);
                } else {
                    throw new RuntimeException("Should not reach here!");
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(final Piece p1, final Piece p2) {
                return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
            }
        });

        Collections.sort(blackTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(final Piece p1, final Piece p2) {
                return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
            }
        });


        for (final Piece takenPiece : whiteTakenPieces) {
            try {
                String iconName = takenPiece.getPieceAlliance().toString().substring(0, 1)
                        + takenPiece.toString()
                        + UIConstants.CHESS_DRAWABLE_EXTENSION;
                ImageIcon icon = UIUtils.getScaledIconFromResources(iconName.toLowerCase(), 40, 40);
                final JLabel imageLabel = new JLabel(icon);
                this.southPanel.add(imageLabel);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        for (final Piece takenPiece : blackTakenPieces) {
            try {
                String iconName = takenPiece.getPieceAlliance().toString().substring(0, 1)
                        + takenPiece.toString()
                        + UIConstants.CHESS_DRAWABLE_EXTENSION;
                final ImageIcon icon = UIUtils.getScaledIconFromResources(iconName.toLowerCase(), 40, 40);
                final JLabel imageLabel = new JLabel(icon);
                this.northPanel.add(imageLabel);

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        validate();
    }
}
