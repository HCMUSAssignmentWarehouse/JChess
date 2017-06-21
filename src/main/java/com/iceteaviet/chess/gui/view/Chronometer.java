package main.java.com.iceteaviet.chess.gui.view;

import main.java.com.iceteaviet.chess.core.player.Alliance;
import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Genius Doan on 6/14/2017.
 */
public class Chronometer extends JPanel implements BaseView {

    private static final int DEFAULT_REMAINING_SECOND = 300;
    OnTimeOutListener mListener;
    private long remainingSecond = DEFAULT_REMAINING_SECOND; //Default is 5 mins
    private JLabel lblName;
    private JLabel lblClock;
    private String name;
    private Alliance alliance;
    private Timer timer;
    private int offset = -1;


    public Chronometer(String name) {
        super();
        this.name = name;
        initLayoutView();

        // Setups a timer which fires events every 1 of second (1000 milliseconds).
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateClock();
            }
        });
    }

    public void setOnTimeOutListener(OnTimeOutListener listener) {
        this.mListener = listener;
    }

    public void start() {
        if (timer != null && !timer.isRunning())
            timer.start();
    }

    public void stop() {
        if (timer != null && timer.isRunning())
            timer.stop();
    }

    public void pause() {
        if (timer != null && timer.isRunning()) {
            //Is running
            offset = 0;
        }
    }

    public void resume() {
        if (timer != null && timer.isRunning()) {
            //Is running
            offset = -1;
        }
    }

    public void startOrResume() {
        if (timer != null) {
            if (timer.isRunning()) {
                offset = -1;
            } else {
                timer.start();
            }
        }
    }


    public Alliance getAlliance() {
        return alliance;
    }

    public void setAlliance(Alliance alliance) {
        this.alliance = alliance;
    }

    public long getRemainingSecond() {
        return remainingSecond;
    }

    public void setRemainingSecond(int remainingSecond) {
        if (remainingSecond < 0)
            remainingSecond = DEFAULT_REMAINING_SECOND;
        this.remainingSecond = remainingSecond;
    }

    public void setClockBorder(Border border) {
        lblClock.setBorder(border);
    }

    public void setClockBackground(Color background) {
        lblClock.setBackground(background);
    }

    public void setClockForeground(Color foreground) {
        lblClock.setForeground(foreground);
    }

    public void setClockFont(Font font) {
        lblClock.setFont(font);
    }

    private void updateClock() {
        remainingSecond += offset;
        lblClock.setText(getClockDisplayText(remainingSecond));

        if (remainingSecond <= 0) {
            //Time's up!
            timer.stop();
            MessageBox.showInfo("Time's up! " + alliance.name() + " has lost the game!", "Chess Timer");
        }
    }

    private String getClockDisplayText(long second) {
        int seconds = 0;
        int minutes = 0;

        seconds = (int) (second % 60); //Get the seconds
        second -= seconds;

        minutes = (int) (second / 60);

        String res = "";

        res += (minutes < 10) ? "0" + String.valueOf(minutes) : String.valueOf(minutes);
        res += " : ";
        res += (seconds < 10) ? "0" + String.valueOf(seconds) : String.valueOf(seconds);

        return res;
    }

    @Override
    public void initLayoutView() {
        setLayout(new BorderLayout());
        lblName = new JLabel(name);
        lblName.setBorder(new EmptyBorder(8, 8, 8, 8));
        lblName.setOpaque(true);
        lblName.setBackground(Color.BLACK);
        lblName.setForeground(Color.WHITE);

        lblClock = new JLabel(getClockDisplayText(remainingSecond));
        lblClock.setBorder(new EmptyBorder(8, 8, 8, 8));
        lblClock.setFont(new Font("San-serif", Font.BOLD, 16));
        lblClock.setHorizontalAlignment(SwingConstants.CENTER);
        lblClock.setBackground(Color.WHITE);
        lblClock.setOpaque(true);
        lblClock.setForeground(Color.BLACK);

        add(lblName, BorderLayout.NORTH);
        add(lblClock, BorderLayout.SOUTH);
    }

    @Override
    public void initData() {

    }

    public interface OnTimeOutListener {
        public void onTimedOut(Alliance alliance);
    }
}