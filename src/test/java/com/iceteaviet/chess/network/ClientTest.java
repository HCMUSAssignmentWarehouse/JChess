package test.java.com.iceteaviet.chess.network;

import main.java.com.iceteaviet.chess.network.ChessClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/14/2017.
 */
public class ClientTest extends JFrame {
    Container cp;
    private JEditorPane htmlPane;
    private JScrollPane scrollPane;
    private JTextField textField;

    public ClientTest() {
        setSize(300, 400);
        setTitle("Client Chat");

        htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        scrollPane = new JScrollPane(htmlPane);
        textField = new JTextField(20);

        cp = getContentPane();

        cp.add(textField, BorderLayout.NORTH);
        cp.add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws IOException {
        ChessClient client = ChessClient.getInstance();
        client.connectToServer("127.0.0.1");
        ClientTest t = new ClientTest();
        t.setVisible(true);
        client.startChat(t.htmlPane, t.textField, "Client");
    }
}
