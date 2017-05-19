package main.java.com.chess.engine;

import main.java.com.chess.engine.board.BoardUtil;
import main.java.com.chess.engine.player.BlackPlayer;
import main.java.com.chess.engine.player.Player;
import main.java.com.chess.engine.player.WhitePlayer;

/**
 * Created by MyPC on 5/9/2017.
 */
public enum Alliance {
    WHITE {
        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(final int position) {
            return BoardUtil.EIGHTH_RANK[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(final int position) {
            return BoardUtil.FIRST_RANK[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    public abstract int getOppositeDirection();
    public abstract int getDirection();
    public abstract boolean isBlack();
    public abstract boolean isWhite();
    public abstract boolean isPawnPromotionSquare(final int position);
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
