package main.java.com.iceteaviet.chess.gui.layout;

import main.java.com.iceteaviet.chess.gui.UIConstants;

import javax.swing.*;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public class MainFrame extends BaseFrame {
    public MainFrame (String title) {
        super(title);
        setSize(UIConstants.OUTER_FRAME_DIMENSION);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
