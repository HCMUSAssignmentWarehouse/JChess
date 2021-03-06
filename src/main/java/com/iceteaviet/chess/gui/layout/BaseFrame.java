package main.java.com.iceteaviet.chess.gui.layout;

import main.java.com.iceteaviet.chess.gui.view.BaseView;
import main.java.com.iceteaviet.chess.gui.view.Toolbar;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public abstract class BaseFrame extends JFrame implements BaseView {
    protected int width, height;
    protected Toolbar mToolbar = null;
    protected boolean isShowToolbar = false;

    protected BaseFrame() {
        this("Untitled layout");
    }

    protected BaseFrame(String title) {
        this(title, defaultWidth, defaultHeight);
    }

    protected BaseFrame(String title, int width, int height) {
        super(title);
        this.width = width;
        this.height = height;

        // invoke the JFrame constructor
        setLayout(new BorderLayout());
        setSize(width + MARGIN, height + MARGIN);

        //Init layout
        initLayoutView();
        initData();
    }

    @Override
    public void initLayoutView() {
        //Toolbar
        if (isShowToolbar) {
            mToolbar = new Toolbar(width, 80, true);
            add(mToolbar);
        }
    }

    @Override
    public void initData() {
    }

    public boolean onCreateOptionsMenu(Toolbar toolbar) {
        return true;
    }

    public void setShowToolbar(boolean isShow) {
        //Toolbar
        if (isShow) {
            mToolbar = new Toolbar(width, 80, true);
            add(mToolbar, BorderLayout.NORTH);
            onCreateOptionsMenu(mToolbar);
        }
        this.isShowToolbar = isShow;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}
