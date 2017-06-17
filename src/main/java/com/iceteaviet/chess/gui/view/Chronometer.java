package main.java.com.iceteaviet.chess.gui.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Genius Doan on 6/14/2017.
 */
public class Chronometer extends JPanel implements BaseView {

    private static final int DEFAULT_REMAINING_SECOND = 300;
    private long remainingSecond = DEFAULT_REMAINING_SECOND; //Default is 5 mins
    private JLabel lblName;
    private JLabel lblClock;

    private String name;
    private Timer timer;


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

    public void start() {
        if (timer != null && !timer.isRunning())
            timer.start();
    }

    public void setRemainingSecond(int remainingSecond) {
        if (remainingSecond < 0)
            remainingSecond = DEFAULT_REMAINING_SECOND;
        this.remainingSecond = remainingSecond;
    }

    public long getRemainingSecond() {
        return remainingSecond;
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
        remainingSecond--;
        lblClock.setText(getClockDisplayText(remainingSecond));

        if (remainingSecond <= 0) {
            //Time's up!

        }
    }

    private String getClockDisplayText(long second) {
        int seconds = 0;
        int minutes = 0;

        seconds = (int) (second%60); //Get the seconds
        second -= seconds;

        minutes = (int) (second/60);

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
        lblName.setBorder(new EmptyBorder(8,8,8,8));
        lblName.setOpaque(true);
        lblName.setBackground(Color.BLACK);
        lblName.setForeground(Color.WHITE);

        lblClock = new JLabel(getClockDisplayText(remainingSecond));
        lblClock.setBorder(new EmptyBorder(8,8,8,8));
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
}