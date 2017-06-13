package main.java.com.iceteaviet.chess.gui.view;

import main.java.com.iceteaviet.chess.gui.OnClickListener;
import main.java.com.iceteaviet.chess.gui.UIUtils;
import main.java.com.iceteaviet.chess.gui.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public class Toolbar extends JPanel implements BaseView {
    public static final int R_ID_BTN_SIGNIN = 0;
    public static final int R_ID_BTN_SIGNOUT = 1;

    //Local variables
    private int defaultWidth, defaultHeight;
    private OnClickListener mListener;

    public Toolbar(int width, int height) {
        super();
        this.defaultWidth = width;
        this.defaultHeight = height;
        initLayoutView();
    }

    public int getHeight() {
        return defaultHeight;
    }

    public int getWidth() {
        return defaultWidth;
    }

    @Override
    public void initLayoutView() {
       setLayout(new BorderLayout());
       setPreferredSize(new Dimension(defaultWidth, defaultHeight));
        setBackground(Color.decode(UIConstants.MATERIAL_TOOLBAR_COLOR));
        //setLayout(new BorderLayout());

        int logoSize = defaultHeight - 2 * MARGIN;
        JLabel logo = null;
        try {
            logo = new JLabel(UIUtils.getScaledIconFromResources("logo.png", logoSize, logoSize));
            logo.setBorder(new EmptyBorder(8,8,8,8));
            add(logo, BorderLayout.WEST);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(8,8,8,8));
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        JLabel jTitle = new JLabel("ARTHUR CHESS");
        jTitle.setBorder(new EmptyBorder(8,8,8,8));
        jTitle.setFont(new Font("Sans-serif", Font.BOLD, 20));
        jTitle.setForeground(Color.WHITE);
        titlePanel.add(jTitle);

        JLabel jSubTitle = new JLabel("Version 0.1. Copyright © IceTeaViet");
        jSubTitle.setBorder(new EmptyBorder(0,8,8,8));
        jSubTitle.setFont(new Font("Sans-serif", Font.PLAIN, 12));
        jSubTitle.setForeground(Color.WHITE);
        titlePanel.add(jSubTitle);

        add(titlePanel);
    }

    @Override
    public void initData() {

    }
}