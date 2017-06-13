package main.java.com.iceteaviet.chess.gui.view;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public interface BaseView {
    public static final int MARGIN_TOP = 80;
    public static final int MARGIN = 16;
    public static final int LINE_HEIGHT = 32;

    int defaultWidth = 900;
    int defaultHeight = 600;

    void initLayoutView();

    void initData();
}
