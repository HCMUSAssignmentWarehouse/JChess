package main.java.com.iceteaviet.chess.gui.dialog;

import main.java.com.iceteaviet.chess.gui.view.BaseView;
import main.java.com.iceteaviet.chess.gui.view.Toolbar;

import javax.swing.*;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public class BaseDialog extends JDialog implements BaseView {

    protected Toolbar toolbar;
    protected int width, height;

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
        //Toolbar
        toolbar = new Toolbar(width, 80);
        add(toolbar);
    }

    @Override
    public void initData() {

    }
}