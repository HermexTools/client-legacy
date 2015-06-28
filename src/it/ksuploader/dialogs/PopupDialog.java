package it.ksuploader.dialogs;

import it.ksuploader.main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Observable;

public class PopupDialog extends Observable {
    private JDialog dialogFrame;
    private JLabel headingLabel;
    private JProgressBar progressBar;
    private JLabel messageLabel;
    private MouseAdapter m;

    public PopupDialog() {
        this.dialogFrame = new JDialog();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
                Main.myLog(ex.toString());
        }

        dialogFrame.setSize(200, 50);
        dialogFrame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        //heading
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(5, 5, 5, 5);

        headingLabel = new JLabel();
        Font f = headingLabel.getFont();
        f = new Font(f.getFontName(), Font.BOLD, f.getSize());
        headingLabel.setFont(f);
        headingLabel.setOpaque(false);
        dialogFrame.add(headingLabel, constraints);
        dialogFrame.setUndecorated(true);

        // Bottone
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;

        JButton xButton = new JButton("X");
        xButton.setMargin(new Insets(1, 4, 1, 4));
        xButton.setFocusable(false);
        dialogFrame.add(xButton, constraints);

        // Messaggio
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridwidth = 2;

        messageLabel = new JLabel();
        dialogFrame.add(messageLabel, constraints);

        progressBar = new JProgressBar();
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        Dimension dim = new Dimension();
        dim.width = 130;
        dim.height = 20;
        progressBar.setMinimumSize(dim);
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(true);
        dialogFrame.add(progressBar, constraints);

        xButton.addActionListener(e -> {
            this.setChanged();
            this.notifyObservers();
            this.destroy();
        });

        // Click nel JDialog per mandare all'url
        this.m = new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI(messageLabel.getText()));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
                }
            }
        };
        this.autoPosition();
    }

    public void cleanObservers() {
        this.deleteObservers();
    }

    private void autoPosition() {
        // Per il posizionamento in basso a destra
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        // altezza taskbar
        Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(dialogFrame.getGraphicsConfiguration());
        dialogFrame.setLocation(scrSize.width - 10 - dialogFrame.getWidth(), scrSize.height - 5 - toolHeight.bottom
                - dialogFrame.getHeight());
    }

    private void gridProgressBar(){
        this.messageLabel.setVisible(false);
        this.progressBar.setVisible(true);
    }

    private void gridLabel(){
        this.progressBar.setVisible(false);
        this.messageLabel.setVisible(true);
    }

    private void setClickable(boolean b){
        if(b){
            dialogFrame.removeMouseListener(m);
            dialogFrame.addMouseListener(m);
        } else {
            dialogFrame.removeMouseListener(m);
        }

    }

    public void show(String header, String message, boolean cliclable) {
        this.setClickable(cliclable);
        this.gridLabel();
        dialogFrame.setFocusableWindowState(false);
        dialogFrame.setVisible(true);
        headingLabel.setText(header);
        messageLabel.setText(message);
        dialogFrame.setAlwaysOnTop(true);

        if (message.length() > 30) {
            int width = (message.length() * 6);
            dialogFrame.setSize(width, 50);
            dialogFrame.setShape(new RoundRectangle2D.Double(1, 1, width, 50, 20, 20));
        } else {
            dialogFrame.setSize(200, 50);
            dialogFrame.setShape(new RoundRectangle2D.Double(1, 1, 200, 50, 20, 20));
        }
    }

    public void set(int n) {
        gridProgressBar();
        progressBar.setValue(n);
        progressBar.setString(n + " %");
    }

    public void setWait() {
        show("Waiting link...", "", false);
    }

    public void wrongPassword() {
        show("Wrong password", "Correct the password server!", false);
    }

    public void serverFull(){
        show("Server full!", ":(", false);
    }

    public void fileTooLarge(){
        show("Server: file too large!", "This file exceeds the maximum size allowed", false);
    }

    public void connectionError(){
        show("Connection Error!", "Check your connection or credential", false);
    }

    public void destroy() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    for (float i = 1.00f; i >= 0; i -= 0.01f) {
                        dialogFrame.setOpacity(i);
                        Thread.sleep(15);
                    }
                    dialogFrame.setVisible(false);
                    dialogFrame.setOpacity(1.00f);
                    dialogFrame.setAlwaysOnTop(false);
                    gridProgressBar();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
                }
            }
        }.start();
    }
}
