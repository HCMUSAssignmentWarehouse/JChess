package main.java.com.iceteaviet.chess.gui.dialog;

import main.java.com.iceteaviet.chess.gui.view.BaseView;
import main.java.com.iceteaviet.chess.gui.view.Toolbar;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public class BaseDialog extends JDialog implements BaseView {
    public static final int MARGIN_TOP = 56;

    protected Toolbar toolbar;
    protected int width, height;
    private boolean showToolbar = false;

    public BaseDialog() {
        this("Untitled dialog");
    }

    public BaseDialog(JFrame frame, boolean modal) {
        super(frame,modal);
    }

    public BaseDialog(String title, int width, int height) {
        super();
        this.width = width;
        this.height = height;

        setTitle(title);
        // invoke the JFrame constructor
        setSize(width, height);
        //setLayout( new FlowLayout() );       // set the layout manager
        //setLayout(null);

        //Init layout
        initLayoutView();
    }

    public BaseDialog(String title) {
        this(title, defaultWidth, defaultHeight);
    }


    @Override
    public void initLayoutView() {
    }

    @Override
    public void initData() {

    }

    public void setShowToolbar(boolean isShow, boolean withLayoutManager) {
        this.showToolbar = isShow;
        if (showToolbar) {
            toolbar = new Toolbar(width, 48, false);
            if (withLayoutManager)
                add(toolbar, BorderLayout.NORTH);
            else {
                setLayout(null);
                toolbar.setBounds(0,0, width, 48);
                add(toolbar);
            }
        }
    }

    public boolean isShowToolbar() {
        return showToolbar;
    }
}