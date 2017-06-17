package test.java.com.iceteaviet.chess.network;

import main.java.com.iceteaviet.chess.network.ChessServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/14/2017.
 */
public class ServerTest  extends JFrame implements ActionListener {

    private JEditorPane  htmlPane;
    private JScrollPane  scrollPane;
    private JTextField 	 textField;

    private ChessServer server;

    Container cp;

    public ServerTest() {
        setSize(300,400);
        setTitle("Server Chat");
        server = ChessServer.getInstance();
        this.addWindowListener(new WindowAdapter() {
            public void WindowClosing(WindowEvent e) {
                server.closeConnection();
            }
        });

        htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        scrollPane = new JScrollPane(htmlPane);

        textField = new JTextField(20);

        cp = getContentPane();

        cp.add(textField, BorderLayout.NORTH);
        textField.addActionListener(this);
        cp.add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {

    }
    public static void main(String[] args) throws IOException {
        ServerTest t = new ServerTest();
        t.server.listen();
        t.setVisible(true);
        t.server.startChat(t.htmlPane, t.textField, "Server");
    }
}