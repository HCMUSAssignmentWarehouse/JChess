package main.java.com.iceteaviet.chess.network;

import main.java.com.iceteaviet.chess.gui.ChessGameWatcher;

import java.io.*;
import java.net.ServerSocket;


/**
 * ChessServer is an implementation of chess server
 * <p>
 * Created by Genius Doan on 6/15/2017.
 */
public final class ChessServer extends NetworkEndPoint {

    private static ChessServer mInstance = null;

    // a server socket that listens on a certain port
    private static ServerSocket ss;
    MessageDispatcher dispatcher;
    private int listenPort = -1;

    private ChessServer(String name) {
        super(name);
    }

    public static ChessServer getInstance() {
        if (mInstance == null)
            mInstance = new ChessServer("CHESS_SERVER");
        return mInstance;
    }

    @Override
    public void run() {
        super.run();
        if (NetworkUtils.isValidPort(listenPort)) {
            listen(listenPort);
        } else {
            listen();
        }
        startChat(ChessGameWatcher.getInstance().getRightMenuPanel().getChatPanel().getHtmlPane(),
                ChessGameWatcher.getInstance().getRightMenuPanel().getChatPanel().getTextField(),
                "Server");
        startGame();
    }

    /**
     * Listen on the default port for incoming connections.
     *
     * @throws IOException of any i/o errors occur
     * @see <listen>
     */
    public void listen() {
        listen(DEFAULT_PORT);
    }

    /**
     * Listen on the specified port for incoming connections. If
     * a client connects and is not a Chess client, it drops
     * the connection and continues listening until a connection
     * with a Chess client is established.
     *
     * @param port the port to listen on
     * @throws IOException if any i/o errors occur
     */
    public void listen(int port) {
        try {
            if (!NetworkUtils.isValidPort(port))
                port = DEFAULT_PORT;

            this.listenPort = port;
            ss = new ServerSocket(listenPort);
            // loop until the connected client is a Chess client
            while (true) {
                System.out.println("Waiting for incoming connections on port " + String.valueOf(listenPort) + "...");
                if (mListener != null)
                    mListener.onStatusUpdate(STATUS_SERVER_STARTED, "Waiting for incoming connections...");
                waitForConnection();

                getStreams();

                netOut.write("ChessServer HELLO\n");
                netOut.flush();

                if (!netIn.readLine().equals("ChessClient HELLO")) {
                    System.out.println("close server");
                    netIn.close();
                    netOut.close();
                    opponent.close();
                } else {
                    ss.close(); //Only accept 1 connection
                    break;
                }
            }
            super.server = true;
            // start new master thread
            processIncomingData();
        } catch (IOException e) {
            System.out.println("Error: I/O problem in ChessServer");
        }
    }

    private void waitForConnection() throws IOException {
        opponent = ss.accept();
    }

    public void setListenPort(int port) {
        this.listenPort = port;
    }

    @Override
    public void processIncomingData() {
        dispatcher = new MessageDispatcher(opponent, this);
        dispatcher.start();
    }

    @Override
    public void getStreams() throws IOException {
        System.out.println("get streams server");
        //opponent.setTcpNoDelay(true);
        netOut = new BufferedWriter(new OutputStreamWriter(opponent.getOutputStream()));
        netIn = new BufferedReader(new InputStreamReader(opponent.getInputStream()));
    }

    @Override
    public void sendEndMessage() {
        try {
            if (netOut != null) {
                netOut.write(NetworkConstants.SERVER_END_ORDER + "\n");
                netOut.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        super.closeConnection();
    }
}