package main.java.com.iceteaviet.chess.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Genius Doan on 6/15/2017.
 */
public class MessageDispatcher implements Runnable {

    private Socket sock;

    private BufferedReader netIn;

    private NetworkEndPoint end;

    public MessageDispatcher(Socket sock, NetworkEndPoint end) {
        this.end = end;
        this.sock = sock;
        try {
            netIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (IOException e) {
            System.out.println("!!!!!Error: problem creating bufferedreader in MessageDispatcher!!!!!");
        }
    }

    /**
     * Start the thread that redirects incoming packets...
     * Processing incoming data
     */
    @Override
    public void run() {
        try {
            String msg = "";
            do {
                // wait for an incoming message
                if (sock.isClosed()) {
                    System.out.println("Sock closed");
                    NetworkEndPoint.OnNetworkUpdateListener listener = end.getOnNetworkUpdateListener();
                    if (listener != null)
                        listener.onStatusUpdate(NetworkEndPoint.STATUS_ERROR, "Network error! Connection closed.");
                    break;
                }
                System.out.println("Waiting for data...");
                msg = netIn.readLine();
                System.out.println("msg: " + msg);

                if (msg == null)
                    msg = "";

                // check if the message is a chat message
                if (msg.startsWith(NetworkConstants.MESSAGE_PREFIX)) {
                    end.getChat().receive(msg);
                } else if (msg.startsWith(NetworkConstants.CHESS_MOVE_PREFIX)) {
                    end.getPlay().receiveMoveMessage(msg);
                } else if (msg.equals(NetworkConstants.CLIENT_END_ORDER)) {
                    //Client close connection
                    System.out.println("Receive client end");
                    ChessServer.getInstance().sendEndMessage();
                    NetworkEndPoint.OnNetworkUpdateListener listener = end.getOnNetworkUpdateListener();
                    if (listener != null)
                        listener.onStatusUpdate(NetworkEndPoint.STATUS_CLIENT_CLOSED, "Client has disconnected.");
                    break;
                } else if (msg.equals(NetworkConstants.SERVER_END_ORDER)) {
                    System.out.println("Receive server end");
                    //Server close connection
                    ChessClient.getInstance().sendEndMessage();
                    NetworkEndPoint.OnNetworkUpdateListener listener = end.getOnNetworkUpdateListener();
                    if (listener != null)
                        listener.onStatusUpdate(NetworkEndPoint.STATUS_SERVER_CLOSED,"Server has disconnected.");
                    break;
                } else {
                    System.out.println("Error: malformed packet " + msg + " in MessageDispatcher!!!!!");
                }
            } while (true);
            NetworkManager.getInstance().closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("!!!!!Error: I/O problem in MessageDispatcher!!!!!");
        }
    }

    public void start() {
        new Thread(this).start();
    }
}