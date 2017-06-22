package main.java.com.iceteaviet.chess.gui.dialog;

import main.java.com.iceteaviet.chess.gui.ChessGameWatcher;
import main.java.com.iceteaviet.chess.gui.UIConstants;
import main.java.com.iceteaviet.chess.gui.UIUtils;
import main.java.com.iceteaviet.chess.network.*;
import main.java.com.iceteaviet.chess.properties.values.string;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Genius Doan on 6/16/2017.
 */
public class NetworkDialog extends BaseDialog {
    private static NetworkDialog mInstance = null;
    ChessServer server;
    ChessClient client;
    JTextField txtIP;
    JTextField txtPort;
    JLabel lblLoad;
    JButton btnConnect;
    JLabel lblIPInfo;
    JLabel lblPortInfo;
    private boolean isHost = false;
    private boolean isBoardFlipped = false;

    public NetworkDialog(JFrame parent, boolean isHost) {
        super(parent, false);
        this.width = 440;
        this.height = 240;
        setLocationRelativeTo(null);
        setLocation(UIConstants.DEFAULT_WIDTH, 0);
        setSize(new Dimension(width, height));
        this.isHost = isHost;

        ChessGameWatcher.getInstance().renewGame();
        ChessGameWatcher.getInstance().getGameSetupDialog().setBlackPlayerType(ChessGameWatcher.PlayerType.HUMAN);
        ChessGameWatcher.getInstance().getGameSetupDialog().setWhitePlayerType(ChessGameWatcher.PlayerType.HUMAN);
        ChessGameWatcher.getInstance().setNetPlay(true);
        NetworkManager.getInstance().setIsHost(isHost);
        setResizable(false);
        initLayoutView();

        if (isHost)
            setupHost();
        else
            setupClient();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (NetworkManager.getInstance().isConnected())
                    NetworkManager.getInstance().sendEndMessages();
                else
                    NetworkManager.getInstance().closeConnection();

                //TODO: Stop thread of server/client

                ChessGameWatcher.getInstance().setNetPlay(false);

                if (!isHost && isBoardFlipped) {
                    ChessGameWatcher.getInstance().flipBoard();
                }
                mInstance = null;
                super.windowClosing(e);
            }
        });
    }

    public static NetworkDialog getInstance(JFrame parent, boolean isHost) {
        if (mInstance == null) {
            mInstance = new NetworkDialog(parent, isHost);
            return mInstance;
        }

        if (isHost != mInstance.isHost)
            MessageBox.showError(string.error_server_client_same_time, string.chess_network_play);
        return mInstance;
    }

    public void inflateHostLayout() {
        setTitle("Chess Server");
        setLayout(new BorderLayout());
        setShowToolbar(true, true);

        toolbar.setTitle(string.toolbar_server_title);

        lblLoad = new JLabel("Loading...", JLabel.CENTER);
        lblLoad.setBorder(new EmptyBorder(12, 8, 8, 8));
        try {
            ImageIcon icon = UIUtils.getGifIconFromResource(this.getClass(), "loader.gif", 64, 64);
            lblLoad.setIcon(icon);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JPanel hostInfoPanel = new JPanel(new GridLayout(3, 2));
        hostInfoPanel.setBorder(new EmptyBorder(4, 32, 8, 32));
        JLabel lblHost = new JLabel("Host Information");
        lblHost.setSize(new Dimension(width, 32));
        lblHost.setBorder(new EmptyBorder(4, 8, 4, 8));
        lblHost.setBackground(UIConstants.PRIMARY_BG_COLOR);
        lblHost.setForeground(Color.WHITE);
        lblHost.setOpaque(true);
        JLabel fuck = new JLabel();
        fuck.setBackground(UIConstants.PRIMARY_BG_COLOR);
        fuck.setOpaque(true);
        lblIPInfo = new JLabel("127.0.0.1");
        lblIPInfo.setFont(new Font("San-serif", Font.BOLD, 14));
        lblPortInfo = new JLabel(String.valueOf(NetworkConstants.DEFAULT_PORT));
        lblPortInfo.setFont(new Font("San-serif", Font.BOLD, 14));

        try {
            lblIPInfo.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        hostInfoPanel.add(lblHost);
        hostInfoPanel.add(fuck);
        hostInfoPanel.add(new JLabel("Host address"));
        hostInfoPanel.add(lblIPInfo);
        hostInfoPanel.add(new JLabel("Host port number"));
        hostInfoPanel.add(lblPortInfo);

        this.add(lblLoad, BorderLayout.CENTER);
        this.add(hostInfoPanel, BorderLayout.SOUTH);
    }

    public void inflateClientLayout() {
        setTitle("Chess Client");
        setLayout(null);
        setShowToolbar(true, false);
        toolbar.setTitle("Connect to chess host");

        JLabel lblIP = new JLabel("IP Address");
        lblIP.setBounds(MARGIN, MARGIN_TOP, 140, 24);

        JLabel lblPort = new JLabel("Port (default: " + NetworkConstants.DEFAULT_PORT + ")");
        lblPort.setBounds(MARGIN, MARGIN_TOP + LINE_HEIGHT, 140, 24);

        txtIP = new JTextField();
        txtIP.setBounds(MARGIN + 140, MARGIN_TOP, 160, 24);
        txtIP.setPreferredSize(new Dimension(160, 24));

        txtPort = new JTextField();
        txtPort.setBounds(MARGIN + 140, MARGIN_TOP + LINE_HEIGHT, 160, 24);
        txtPort.setPreferredSize(new Dimension(160, 24));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(null);
        inputPanel.setBounds(16, 16, width - 16 * 2, height - 100);
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
        UIUtils.setEmptyBorder(btnConnect, 4, 20, 4, 20);
        try {
            ImageIcon icon = UIUtils.getScaledIconFromResources(this.getClass(), "lan-connect.png", 24, 24);
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
            public void onStatusUpdate(int statusCode, String statusMessage) {
                if (lblLoad != null)
                    lblLoad.setText(statusMessage);
            }
        });
        if (!server.isAlive())
            server.start();
    }

    public void setupClient() {
        this.btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChessGameWatcher.getInstance().flipBoard();
                isBoardFlipped = true;
                client = ChessClient.getInstance();
                String ip = txtIP.getText();
                int port = txtPort.getText().isEmpty() ? 0 : Integer.valueOf(txtPort.getText());

                if (ip == null || ip.isEmpty()) {
                    ip = InetAddress.getLoopbackAddress().getHostAddress();
                    txtIP.setText(ip);
                }
                if (!NetworkUtils.isValidPort(port)) {
                    port = NetworkConstants.DEFAULT_PORT;
                    txtPort.setText(String.valueOf(port));
                }

                client.setIP(ip);
                client.setServerPort(port);
                client.setOnNetworkUpdateListener(new NetworkEndPoint.OnNetworkUpdateListener() {
                    @Override
                    public void onStatusUpdate(int statusCode, String statusMessage) {
                        if (statusCode == NetworkEndPoint.STATUS_DISCONNECTED) {
                            txtPort.setEditable(true);
                            txtIP.setEditable(true);
                            btnConnect.setEnabled(true);
                        }
                    }
                });

                if (!client.isAlive())
                    client.start();
                txtPort.setEditable(false);
                txtIP.setEditable(false);
                btnConnect.setEnabled(false);
            }
        });
    }

    @Override
    public void initLayoutView() {
        //super.initLayoutView();
        if (isHost) {
            inflateHostLayout();
        } else {
            inflateClientLayout();
        }
    }
}
