package main.java.com.iceteaviet.chess.gui;

import main.java.com.iceteaviet.chess.gui.dialog.MessageBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Genius Doan on 6/11/2017.
 */
public class UIUtils {
    public static ImageIcon getScaledIconFromResources(String imgName, int width, int height) throws IOException {
        Image img = getImageFromResources(imgName);
        img = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static void setEmptyBorder(JButton button, int paddingTop, int paddingLeft,
                                      int paddingBottom, int paddingRight) {
        if (button != null) {
            Border emptyBorder = BorderFactory.createEmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight);
            button.setBorder(emptyBorder);
        }
    }

    public static Image getImageFromResources(String fileName) throws IOException {
        File f = new File(UIConstants.DEFAULT_ICON_RESOURCE_PATH + fileName);

        if (!f.exists() || !f.isFile()) {
            MessageBox.showError("Can not load image " + fileName + " from resources!", "Can't read input file");
            f = new File(UIConstants.DEFAULT_ICON_RESOURCE_PATH + "placeholder.png");
        }

        return ImageIO.read(f);
    }

    public static ImageIcon getIconFromResources(String fileName) throws IOException {
        File f = new File(UIConstants.DEFAULT_ICON_RESOURCE_PATH + fileName);

        if (!f.exists() || !f.isFile()) {
            MessageBox.showError("Can not load icon " + fileName + " from resources!", "Can't read input file");
            f = new File(UIConstants.DEFAULT_ICON_RESOURCE_PATH + "placeholder.png");
        }

        final BufferedImage image = ImageIO.read(f);
        return new ImageIcon(image);
    }

    public static ImageIcon getGifIconFromResource(Class<?> context, String fileName, int width, int height) throws IOException {
        ImageIcon icon = new ImageIcon(context.getClassLoader().getResource(UIConstants.DEFAULT_GIF_RESOURCE_PATH + fileName));

        icon = new ImageIcon(icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT));

        return icon;
    }

    public static void setCellTextAllignment(JTable table, int alignment) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);

        TableModel tableModel = table.getModel();

        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }
}
