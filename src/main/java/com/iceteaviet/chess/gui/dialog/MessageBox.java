package main.java.com.iceteaviet.chess.gui.dialog;

import javax.swing.*;
import java.security.spec.ECField;

/**
 * Created by Genius Doan on 6/10/2017.
 */
public class MessageBox {

    private static Exception currException;

    public static void showInfo(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.WARNING_MESSAGE);
    }

    public static void showErrorByException(String errorContent, Exception ex) {
        if (currException == null
                || !currException.getMessage().equals(ex.getMessage())
                || !currException.getClass().equals(ex.getClass())) {
            showError(errorContent, ex.getMessage());
            currException = ex;
        }
    }

    public static void showError(String errorContent, String errorTitle) {
        JOptionPane.showMessageDialog(null, errorContent, errorTitle, JOptionPane.ERROR_MESSAGE);
    }

    public static void shoQuestionBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.QUESTION_MESSAGE);
    }
}
