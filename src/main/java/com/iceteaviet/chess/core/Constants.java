package main.java.com.iceteaviet.chess.core;

/**
 * Constants is an interface consisting of useful values.
 *
 * Created by Genius Doan on 6/13/2017.
 */
public interface Constants {
    // Piece types
    byte TYPE_PAWN = 0;
    byte TYPE_ROOK = 1;
    byte TYPE_KNIGHT = 2;
    byte TYPE_BISHOP = 3;
    byte TYPE_QUEEN = 4;
    byte TYPE_KING = 5;

    // Move notation
    String[] NAME = {"Pa", "Kn", "Bi", "Ro", "Qu", "Ki"};
    String[] TYPE = {"", "N", "B", "R", "Q", "K"};
    String[] COORD_X = {"a", "b", "c", "d", "e", "f", "g", "h"};
    int[] cost = {1, 5, 5, 10, 40, 100};

    // Time
    long ONE_SECOND = 1000;
    long FIVE_SECONDS = ONE_SECOND * 5;
    long TWENTY_SECONDS = ONE_SECOND * 20;
    long ONE_MINUTE = ONE_SECOND * 60;
    long FIVE_MINUTES = ONE_MINUTE * 5;
    long TEN_MINUTES = ONE_MINUTE * 10;
    long TWENTY_MINUTES = ONE_MINUTE * 20;

    public static Alliance FIRST_MOVE_ALLIANCE = Alliance.WHITE;
}
