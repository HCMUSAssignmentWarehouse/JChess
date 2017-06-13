package main.java.com.iceteaviet.chess.gui.layout;

import main.java.com.iceteaviet.chess.gui.Table;
import main.java.com.iceteaviet.chess.gui.UIConstants;
import main.java.com.iceteaviet.chess.gui.UIUtils;
import main.java.com.iceteaviet.chess.gui.view.Toolbar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public class MainFrame extends BaseFrame {
    public MainFrame (String title) {
        super(title);
        setSize(UIConstants.OUTER_FRAME_DIMENSION);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public boolean onCreateOptionsMenu(Toolbar toolbar) {
        if (toolbar != null)
        {
            //Add undo, redo
            try {
                ImageIcon undoIcon = UIUtils.getScaledIconFromResources("undo.png", 40, 40);
                JLabel lblUndo = new JLabel(undoIcon);
                lblUndo.setBorder(new EmptyBorder(8,8,8,8));
                ImageIcon redoIcon = UIUtils.getScaledIconFromResources("redo.png", 40, 40);
                JLabel lblRedo = new JLabel(redoIcon);
                lblRedo.setBorder(new EmptyBorder(8,8,8,8));

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
                        //TODO: Undo
                        if(Table.getInstance().getMoveLog().size() > 0) {
                            Table.getInstance().undoLastMove();
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
}
