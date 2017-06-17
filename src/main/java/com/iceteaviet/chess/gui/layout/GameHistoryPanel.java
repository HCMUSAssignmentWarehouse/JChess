package main.java.com.iceteaviet.chess.gui.layout;

import main.java.com.iceteaviet.chess.core.board.Board;
import main.java.com.iceteaviet.chess.core.board.Move;
import main.java.com.iceteaviet.chess.gui.Table;
import main.java.com.iceteaviet.chess.gui.UIConstants;
import main.java.com.iceteaviet.chess.gui.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 5/17/2017.
 */
public class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;

    public GameHistoryPanel() {
        //this.setLayout(new BorderLayout());
        //setBackground(UIConstants.PRIMARY_BG_COLOR);
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        table.getTableHeader().setFont(new Font("Sans-serif", Font.BOLD, 12));
        UIUtils.setCellTextAllignment(table, SwingConstants.CENTER);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(new Dimension(Double.valueOf(UIConstants.RIGHT_GAME_PANEL_DIMENSION.getWidth()).intValue(), 120));
        this.add(scrollPane);
        this.setVisible(true);
    }

    private static String calculateCheckAndCheckMateHash(final Board board) {
        if (board.getCurrentPlayer().isInCheckMate()) {
            return "#";
        } else if (board.getCurrentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    public void redo(final Board board,
                     final Table.MoveLog moveHistory) {
        int currentRow = 0;
        this.model.clear();
        for (final Move move : moveHistory.getMoves()) {
            final String moveText = move.toString();
            if (move.getMovedPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            } else if (move.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if (moveHistory.getMoves().size() > 0) {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            final String moveText = lastMove.toString();

            if (lastMove.getMovedPiece().getPieceAlliance().isWhite()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            } else if (lastMove.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    private static class Row {

        private String whiteMove;
        private String blackMove;

        Row() {
        }

        public String getWhiteMove() {
            return this.whiteMove;
        }

        public void setWhiteMove(final String move) {
            this.whiteMove = move;
        }

        public String getBlackMove() {
            return this.blackMove;
        }

        public void setBlackMove(final String move) {
            this.blackMove = move;
        }

    }

    private static class DataModel extends DefaultTableModel {

        private static final String[] NAMES = {"White", "Black"};
        private final List<Row> values;

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            final Row currentRow = this.values.get(row);
            if (col == 0) {
                return currentRow.getWhiteMove();
            } else if (col == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object aValue,
                               final int row,
                               final int col) {
            final Row currentRow;
            if (this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if (col == 0) {
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            } else if (col == 1) {
                currentRow.setBlackMove((String) aValue);
                fireTableCellUpdated(row, col);
            }
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int col) {
            return NAMES[col];
        }
    }
}
