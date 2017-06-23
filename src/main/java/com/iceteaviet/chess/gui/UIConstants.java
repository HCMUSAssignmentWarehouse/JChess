package main.java.com.iceteaviet.chess.gui;

import java.awt.*;
import java.io.File;

/**
 * Created by Genius Doan on 6/10/2017.
 */
public class UIConstants {
    public static final int DEFAULT_WIDTH = 900;
    public static final int DEFAULT_HEIGHT = 600;
    public static final Dimension OUTER_FRAME_DIMENSION = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    public static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    public static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    public static final Dimension LEFT_TAKEN_PIECES_PANEL_DIMENSION = new Dimension(100, 80);
    public static final Dimension RIGHT_GAME_PANEL_DIMENSION = new Dimension(200, 400);
    public static final String CHESS_DRAWABLE_EXTENSION = ".png";
    public static final String DEFAULT_ICON_RESOURCE_PATH = "drawable/";
    public static final int DEFAULT_PIECE_ICON_SIZE = 56;
    public static final Color PRIMARY_COLOR = Color.decode("#ff9800");
    public static final Color PRIMARY_COLOR_DARK = Color.decode("#ef6c00");
    public static final Color PRIMARY_BG_COLOR = Color.decode("#616161");
    public static final Color MATERIAL_TOOLBAR_COLOR = Color.decode("#2196F2");
    public static final Color PRIMARY_BORDER_COLOR = Color.decode("#8B4726");
}
