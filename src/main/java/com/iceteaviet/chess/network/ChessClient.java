package main.java.com.iceteaviet.chess.network;

import main.java.com.iceteaviet.chess.gui.Table;
import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;

import java.io.*;
import java.net.Socket;

public final class ChessClient extends NetworkEndPoint {
    private static ChessClient mInstance = null;
    private String mIP;
    private int serverPort;
    private MessageDispatcher dispatcher;

    private ChessClient(String name) {
        super(name);
    }

    public static ChessClient getInstance() {
        if (mInstance == null)
            mInstance = new ChessClient("CHESS_CLIENT");
        return mInstance;
    }

    @Override
    public void run() {
        super.run();
        try {
            if (NetworkUtils.isValidIP(mIP)) {
                if (NetworkUtils.isValidPort(serverPort))
                    connectToServer(mIP, serverPort);
                else
                    connectToServer(mIP);
            } else {
                connectToServer(DEFAULT_IP);
            }
            startChat(Table.getInstance().getRightMenuPanel().getChatPanel().getHtmlPane(),
                    Table.getInstance().getRightMenuPanel().getChatPanel().getTextField(), "Client");
            startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to the specified ip on the default port.
     *
     * @param ip the ip of the opponent's computer
     * @return true if connected; false otherwise
     * @throws IOException if any i/o errors occur
     */
    public boolean connectToServer(String ip) throws IOException {
        return connectToServer(ip, DEFAULT_PORT);
    }

    /**
     * Connect to the specified ip on the specified port.
     *
     * @param ip   the ip of the opponent's computer
     * @param port the port number to connect to
     * @return true if connected; false otherwise
     * @throws IOException if any i/o errors occur
     */
    public boolean connectToServer(String ip, int port) throws IOException {
        try {
            //connectionHandle = new Socket(InetAddress.getByName(hostName), serverPort);
            this.mIP = ip;
            this.serverPort = port;
            opponent = new Socket(ip, port);

            getStreams();

            // check if the server is a ChessServer
            if (!netIn.readLine().equals("ChessServer HELLO")) {
                System.out.println(netIn.readLine());
                netIn.close();
                netOut.close();
                opponent.close();
                return false;
            }

            netOut.write("ChessClient HELLO\n");
            netOut.flush();

            super.server = false;
            processIncomingData();
            System.out.println("Connected to: " + serverPort);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            MessageBox.showError("Cannot connect to server: " + mIP + ":" + String.valueOf(port) + "\n" + e.getMessage(), "Internal server error");
            return false;
        }
    }

    @Override
    public void processIncomingData() {
        // start new msg redirector
        dispatcher = new MessageDispatcher(opponent, this);
        dispatcher.start();
    }

    public void setIP(String ip) {
        this.mIP = ip;
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }

    @Override
    public void getStreams() throws IOException {
        //opponent.setTcpNoDelay(true);
        netIn = new BufferedReader(new InputStreamReader(opponent.getInputStream()));
        netOut = new BufferedWriter(new OutputStreamWriter(opponent.getOutputStream()));
    }

    @Override
    public void sendEndMessage() {
        try {
            if (netOut != null) {
                netOut.write(NetworkConstants.CLIENT_END_ORDER + "\n");
                netOut.flush();
                System.out.println(NetworkConstants.CLIENT_END_ORDER);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        this.sendEndMessage();
        super.closeConnection();
    }
}