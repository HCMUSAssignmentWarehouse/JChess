package main.java.com.iceteaviet.chess.gui.dialog;

import main.java.com.iceteaviet.chess.gui.Table;
import main.java.com.iceteaviet.chess.gui.UIConstants;
import main.java.com.iceteaviet.chess.gui.UIUtils;
import main.java.com.iceteaviet.chess.network.ChessClient;
import main.java.com.iceteaviet.chess.network.ChessServer;
import main.java.com.iceteaviet.chess.network.NetworkEndPoint;
import main.java.com.iceteaviet.chess.network.NetworkManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/16/2017.
 */
public class NetworkDialog extends BaseDialog{
    private boolean isHost = false;
    ChessServer server;
    ChessClient client;
    JTextField txtIP;
    JTextField txtPort;
    JLabel lblLoad;
    JButton btnConnect;

    public NetworkDialog(JFrame parent, boolean isHost) {
        super(parent, false);
        this.width = 400;
        this.height = 200;
        setSize(new Dimension(width, height));
        this.isHost = isHost;
        setResizable(false);
        initLayoutView();

        if (isHost)
            setupHost();
        else
            setupClient();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                NetworkManager.getInstance().sendEndMessages();
                super.windowClosing(e);
            }
        });
    }

    public void inflateHostLayout() {
        setTitle("Chess Server");
        setLayout(new BorderLayout());
        JLabel lblStatus = new JLabel("Finding chess opponent");
        lblStatus.setBorder(new EmptyBorder(16,0,16, 0));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

        lblLoad = new JLabel("Loading...", JLabel.CENTER);
        try {
            ImageIcon icon = UIUtils.getGifIconFromResource(this.getClass(), "loader.gif", 64, 64);
            lblLoad.setIcon(icon);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.add(lblStatus, BorderLayout.NORTH);
        this.add(lblLoad, BorderLayout.CENTER);
    }

    public void inflateClientLayout() {
        setTitle("Chess Client");
        setLayout(null);
        JLabel lblIP = new JLabel("IP Address");
        lblIP.setBounds(MARGIN, MARGIN, 140, 24);

        JLabel lblPort = new JLabel("Port (default: 2212)");
        lblPort.setBounds(MARGIN, MARGIN + LINE_HEIGHT, 140, 24);

        txtIP = new JTextField();
        txtIP.setBounds(MARGIN + 140, MARGIN, 160, 24);
        txtIP.setPreferredSize(new Dimension(160,24));

        txtPort = new JTextField();
        txtPort.setBounds(MARGIN + 140, MARGIN + LINE_HEIGHT, 160, 24);
        txtPort.setPreferredSize(new Dimension(160,24));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(null);
        inputPanel.setBounds(16,16, width - 16*2, height - 100);
        inputPanel.add(lblIP);
        inputPanel.add(lblPort);
        inputPanel.add(txtIP);
        inputPanel.add(txtPort);

        btnConnect = new JButton("Connect");
        btnConnect.setBounds(0, height - 68, width, 40);
        btnConnect.setIconTextGap(16);
        btnConnect.setHorizontalAlignment(SwingConstants.CENTER);
        btnConnect.setBackground(UIConstants.PRIMARY_BG_COLOR);
        btnConnect.setForeground(Color.WHITE);
        UIUtils.setEmptyBorder(btnConnect, 4 ,20 ,4 ,20);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources("lan-connect.png", 24, 24);
            btnConnect.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.add(inputPanel);
        this.add(btnConnect);
    }

    public void setupHost() {
        server = ChessServer.getInstance();
        server.setOnNetworkUpdateListener(new NetworkEndPoint.OnNetworkUpdateListener() {
            @Override
            public void onStatusUpdate(String statusMessage) {
                if (lblLoad != null)
                    lblLoad.setText(statusMessage);
            }
        });
        server.start();
    }

    public void setupClient() {
        this.btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client = ChessClient.getInstance();
                client.setIP(txtIP.getText());
                client.setServerPort(txtPort.getText().isEmpty() ? 0 : Integer.valueOf(txtPort.getText()));
                client.start();
            }
        });
    }

    @Override
    public void initLayoutView() {
        //super.initLayoutView();
        if (isHost)
        {
            inflateHostLayout();
        }
        else {
            inflateClientLayout();
        }
    }
}
