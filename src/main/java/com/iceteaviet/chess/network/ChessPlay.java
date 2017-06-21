package main.java.com.iceteaviet.chess.network;

import main.java.com.iceteaviet.chess.gui.ChessGameWatcher;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Genius Doan on 6/15/2017.
 */
public class ChessPlay {
    private Socket opponent;

    private BufferedWriter netOut;

    private BufferedReader netIn;

    private ChessGameWatcher table;

    public ChessPlay(Socket opponent) {
        table = ChessGameWatcher.getInstance();
        this.opponent = opponent;
        try {
            netIn = new BufferedReader(new InputStreamReader(opponent.getInputStream()));
            netOut = new BufferedWriter(new OutputStreamWriter(opponent.getOutputStream()));
        } catch (IOException e) {
        }
    }

    /**
     * The method called by MsgRedirector....
     */
    public void receiveMoveMessage(String msg) {
        msg = msg.substring(NetworkConstants.CHESS_MOVE_PREFIX.length());

        StringTokenizer st = new StringTokenizer(msg, NetworkConstants.CHESS_MOVE_DELIMITER);

        int currentCoordinate = Integer.parseInt(st.nextToken());
        int destinationCoordinate = Integer.parseInt(st.nextToken());

        table.movePiece(currentCoordinate, destinationCoordinate);
        table.drawBoardAfterMove();
    }

    /**
     * Move the chess piece from row <fromRow> and
     * column <fromCol> to row <toRow> and column
     * <toCol>. Checking must be done beforehand if the
     * move is legal.
     */
    public void sendMoveMessage(int currCoord, int desCoord) {
        try {
            String msg = NetworkConstants.CHESS_MOVE_PREFIX + currCoord + NetworkConstants.CHESS_MOVE_DELIMITER + desCoord + "\n";
            System.out.println("message=" + msg);
            netOut.write(msg);
            netOut.flush();
        } catch (IOException e) {

        }
    }
}