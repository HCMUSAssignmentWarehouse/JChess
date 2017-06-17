package main.java.com.iceteaviet.chess.network;

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

    //private ChessGame chessGame;

    public ChessPlay(Socket opponent) {
        //chessGame = ChessComponent.getInstance().chessGame;
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
    public void receive(String msg) {
        msg = msg.substring(NetworkConstants.CHESS_MOVE_PREFIX.length());

        StringTokenizer st = new StringTokenizer(msg, ":");

        int fromCol = Integer.parseInt(st.nextToken());
        int fromRow = Integer.parseInt(st.nextToken());
        int toCol = Integer.parseInt(st.nextToken());
        int toRow = Integer.parseInt(st.nextToken());

        //chessGame.movePiece(new Move(fromCol, fromRow, toCol, toRow));
    }

    /**
     * Move the chess piece from row <fromRow> and
     * column <fromCol> to row <toRow> and column
     * <toCol>. Checking must be done beforehand if the
     * move is legal.
     */
    public void move(int fromCol, int fromRow,
                     int toCol, int toRow) {
        try {
            String msg = NetworkConstants.CHESS_MOVE_PREFIX + fromCol + ":" + fromRow + ":" + toCol + ":" + toRow + "\n";
            System.out.println("message=" + msg);
            netOut.write(msg);
            netOut.flush();
        } catch (IOException e) {

        }
    }
}