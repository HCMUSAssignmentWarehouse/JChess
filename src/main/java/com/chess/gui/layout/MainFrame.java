package main.java.com.chess.gui.layout;

import main.java.com.chess.gui.UIConstants;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
