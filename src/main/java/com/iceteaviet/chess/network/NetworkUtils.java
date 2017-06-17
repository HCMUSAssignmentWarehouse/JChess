package main.java.com.iceteaviet.chess.network;

import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;

/**
 * Created by Genius Doan on 6/17/2017.
 */
public class NetworkUtils {
    public static boolean isValidPort(int port) {
        if (port < 1 || port > 65535)
            return false;

        if (port < 1024) {
            MessageBox.showWarning("You are using reversed system port number!", "Invalid port number");
        }

        return true;
    }

    public static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty())
            return false;

        return true;
    }
}
