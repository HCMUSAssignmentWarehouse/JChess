package main.java.com.iceteaviet.chess.network;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Genius Doan on 6/15/2017.
 * this class represents an end-point on a network
 * <p>
 * TODO: Consider extends Thread or Runable to make multi-threading
 */
public abstract class NetworkEndPoint extends Thread {
    /**
     * The default port to use
     */
    protected static final int DEFAULT_PORT = NetworkConstants.DEFAULT_PORT;

    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_DISCONNECTED = -1;
    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_SERVER_STARTED = 2;
    public static final int STATUS_SERVER_CLOSED = 3;
    public static final int STATUS_CLIENT_CLOSED = 2;
    public static final int STATUS_ERROR = -2;


    /**
     * The default host IP to use
     */
    protected static final String DEFAULT_IP = "127.0.0.1";
    /**
     * true if this pc is server; false if client
     */
    public static boolean server;
    /**
     * A socket connection to the opponent
     */
    protected Socket opponent;
    /**
     * A buffered input stream with the opponent
     */
    protected BufferedReader netIn;
    /**
     * A buffered output stream with the opponent
     */
    protected BufferedWriter netOut;
    /**
     * A chat session
     */
    protected ChessChat chat;
    /**
     * A chess game session
     */
    protected ChessPlay netPlay;
    protected OnNetworkUpdateListener mListener;

    protected NetworkEndPoint(String name) {
        //super(name);
    }

    /**
     * Start a chat session with the opponent. This method starts two threads:
     * a ChatListener thread listens for incoming chat messages from the
     * opponent, and a ChatSend thread which listens for user input and then
     * sends that input to the opponents ChatListener thread.
     *
     * @param doc      the text area that holds the chat output.
     * @param field    the text field that holds the user input to send.
     * @param userName some name to identify the user.
     */
    public void startChat(JEditorPane doc, JTextField field, String userName) {
        if (opponent != null) {
            chat = new ChessChat(opponent, doc, field, userName);
            NetworkManager.getInstance().setConnected(true);
            if (mListener != null)
                mListener.onStatusUpdate(STATUS_CONNECTED, "Connection established.");
        }
    }

    public void startGame() {
        netPlay = new ChessPlay(opponent);
    }

    public void closeConnection() {
        try {
            if (netIn != null)
                netIn.close();

            if (netOut != null)
                netOut.close();

            if (opponent != null)
                opponent.close();

            if (mListener != null)
                mListener.onStatusUpdate(STATUS_DISCONNECTED,"Connection closed.");
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println("Error: Closing problem");
        }
    }

    public ChessPlay getPlay() {
        return netPlay;
    }

    public ChessChat getChat() {
        return chat;
    }

    public OnNetworkUpdateListener getOnNetworkUpdateListener() {
        return mListener;
    }

    public void setOnNetworkUpdateListener(OnNetworkUpdateListener listener) {
        this.mListener = listener;
    }

    public abstract void processIncomingData();

    public abstract void getStreams() throws IOException;

    public abstract void sendEndMessage();

    public interface OnNetworkUpdateListener {
        public void onStatusUpdate(int statusCode, String statusMessage);
    }
}