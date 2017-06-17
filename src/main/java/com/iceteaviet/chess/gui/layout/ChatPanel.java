package main.java.com.iceteaviet.chess.gui.layout;

import main.java.com.iceteaviet.chess.gui.UIConstants;
import main.java.com.iceteaviet.chess.gui.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/16/2017.
 */
public class ChatPanel extends JPanel {
    private JEditorPane htmlPane;
    private JScrollPane listScroller;
    private JTextField textField;
    private JButton btnSend;

    public ChatPanel() {
        super();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Double.valueOf(UIConstants.RIGHT_GAME_PANEL_DIMENSION.getWidth()).intValue(), 120));

        htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);

        listScroller = new JScrollPane(htmlPane);

        JPanel btnHolderPanel = new JPanel();
        btnSend = new JButton("Send");
        btnSend.setHorizontalAlignment(SwingConstants.CENTER);
        btnSend.setBackground(UIConstants.PRIMARY_BG_COLOR);
        btnSend.setForeground(Color.WHITE);
        UIUtils.setEmptyBorder(btnSend, 4, 8, 4, 8);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("send.png", 24, 24);
            btnSend.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        textField = new JTextField(11);
        textField.setPreferredSize(new Dimension(100, 30));
        btnHolderPanel.add(textField);
        btnHolderPanel.add(btnSend);

        add(listScroller, BorderLayout.CENTER);
        add(btnHolderPanel, BorderLayout.SOUTH);
    }

    public JEditorPane getHtmlPane() {
        return htmlPane;
    }

    public JTextField getTextField() {
        return textField;
    }
}
