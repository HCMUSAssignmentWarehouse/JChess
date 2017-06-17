package main.java.com.iceteaviet.chess.network;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * Created by Genius Doan on 6/15/2017.
 * This class handles the Chess chat session between the
 * user and the opponent.
 * <p>
 * TODO - be able to type a hyperlink
 */
public class ChessChat implements ActionListener {

    /**
     * A socket connection with the opponent to send the chat messages
     */
    private Socket sock;

    /**
     * A buffered input stream from the opponent
     */
    private BufferedReader netIn;

    /**
     * A buffered output stream to the opponent
     */
    private BufferedWriter netOut;

    /**
     * The pane that contains the chat messages in html
     */
    private JEditorPane htmlPane;

    /**
     * The text field that contains the user chat input
     */
    private JTextField chatInput;

    /**
     * The handle name the user wants to use when chatting
     */
    private String userHandle;

    /**
     * The color to use for the local user's handle
     */
    private String localColor;

    /**
     * The color to use for the opponent's handle
     */
    private String opponentColor;

    /**
     * The font to use to display the chat messages in the chat history
     */
    private String font;

    /**
     * Create a new ChessChat session.
     *
     * @param opponent  the socket connection with the opponent.
     * @param chatPane  the chat history pane.
     * @param textInput the chat input text field.
     * @param userName  the handle to use for the user.
     */
    public ChessChat(Socket opponent, JEditorPane chatPane,
                     JTextField textInput, String userName) {

        sock = opponent;
        htmlPane = chatPane;
        chatInput = textInput;
        userHandle = userName;

        // setup i/o streams
        try {
            netIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            netOut = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: I/O in ChessChat constructor");
        }

        if (!htmlPane.getContentType().equals("text/html"))
            htmlPane.setContentType("text/html");
        if (htmlPane.isEditable())
            htmlPane.setEditable(false);

        chatInput.addActionListener(this);

        // setup default colors and font
        localColor = "ff0066";
        opponentColor = "336699";
        font = "Arial";

        htmlPane.setText("<pre><font face=\"" + font + "\">Welcome to Chess Chat"
                + "</font><pre>");
    }

    /**
     * Get the color of the local user's handle.
     *
     * @return a String representing the color in html
     */
    public String getLocalColor() {
        return localColor;
    }

    /**
     * Set the color of the local user's handle.
     *
     * @param color a String with the html color
     */
    public void setLocalColor(String color) {
        localColor = color;
    }

    /**
     * Get the color of the opponent's handle.
     *
     * @return a String representing the color in html
     */
    public String getOpponentColor() {
        return opponentColor;
    }

    /**
     * Set the color of the opponent's handle.
     *
     * @param color a String with the html color
     */
    public void setOpponentColor(String color) {
        opponentColor = color;
    }

    /**
     * The method called by MsgRedirector...
     */
    public void receive(String msg) {
        msg = msg.substring(NetworkConstants.MESSAGE_PREFIX.length());

        String username = msg.substring(0, msg.indexOf(":"));
        String message = msg.substring(msg.indexOf(":") + 1);

        String last = htmlPane.getText();

        last = last.substring(last.indexOf("<pre>") + 5,
                last.lastIndexOf("</pre>"));

        // update the html pane with the message (some clever html parsing)
        htmlPane.setText("<pre>" + last + "\n<font color=\"" + opponentColor + "\" face=\""
                + font + "\">" +
                username + ":</font><font face=\"" + font + "\">" + message + "</font></pre>");
    }

    /**
     * This method is called when the user hits the enter button when focused
     * on the chat input text field or clicks the "Send" button. It sends the
     * message to the opponent and updates the local chat history pane.
     */
    public void actionPerformed(ActionEvent e) {

        // send the message to the opponent
        send(chatInput.getText());

        //update the user's html pane to display his/her message
        String last = htmlPane.getText();
        last = last.substring(last.indexOf("<pre>") + 5,
                last.lastIndexOf("</pre>")).trim();

        // some clever html parsing...
        String fontTag = "<font face=\"" + font + "\">";

        htmlPane.setText("<pre>" + last + "\n<font color=\"" + localColor + "\" face=\"" +
                font + "\">" +
                userHandle + ":</font><font face=\"" + font + "\">" + chatInput.getText() + "</font></pre>");

        System.out.println(htmlPane.getText());

        // clear the chat input text field
        chatInput.setText("");
    }

    /**
     * This method constructs a chess chat message containing the local user's
     * handle and the message to the opponent.
     */
    private void send(String message) {
        // construct packet
        try {
            netOut.write(NetworkConstants.MESSAGE_PREFIX + userHandle + ":" + message + "\n");
            netOut.flush();
        } catch (IOException e) {
            // inform the user of this error
            // implement this....
        }
    }
}