package main.java.com.iceteaviet.chess.network;

import javafx.scene.control.Tab;
import main.java.com.iceteaviet.chess.gui.Table;

/**
 * Created by Genius Doan on 6/17/2017.
 */
public class NetworkManager {
    private static NetworkManager mInstance = null;
    private boolean isConnected = false;
    private boolean isHost = false;

    private NetworkManager() {

    }

    public static NetworkManager getInstance() {
        if (mInstance == null)
            mInstance = new NetworkManager();

        return mInstance;
    }

    public void sendEndMessages() {
        if (isConnected) {
            if (NetworkEndPoint.server) {
                ChessServer.getInstance().sendEndMessage();
            } else {
                ChessClient.getInstance().sendEndMessage();
            }
        }
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
        if (isHost) {
            Table.getInstance().setMainPlayer(Table.getInstance().getGameBoard().whitePlayer());
        }
        else {
            Table.getInstance().setMainPlayer(Table.getInstance().getGameBoard().blackPlayer());
        }
    }

    public boolean isHost() {
        return isHost;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    public void closeConnection() {
        if (isConnected) {
            if (NetworkEndPoint.server)
                ChessServer.getInstance().closeConnection();
            else
                ChessClient.getInstance().closeConnection();
            isConnected = false;
        }
    }

    public void sendMoveMessages(int currCoord, int desCoord) {
        if (isHost) {
            ChessServer.getInstance().getPlay().sendMoveMessage(currCoord, desCoord);
        }
        else {
            ChessClient.getInstance().getPlay().sendMoveMessage(currCoord, desCoord);
        }
    }
}
