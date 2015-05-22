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

public class NotificationDialog extends JDialog{
	private JLabel headingLabel;
	private JLabel messageLabel;

	public NotificationDialog() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			Main.myLog(ex.toString());
		}

		this.setSize(200, 50);
		this.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();

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
		this.add(headingLabel, constraints);
		this.setUndecorated(true);

		// Bottone
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;

		JButton xButton = new JButton("X");
		xButton.setMargin(new Insets(1, 4, 1, 4));
		xButton.setFocusable(false);
		this.add(xButton, constraints);

		// Messaggio
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth = 2;

		messageLabel = new JLabel();
		this.add(messageLabel, constraints);

		xButton.addActionListener(e -> dispose());

		// Click nel JDialog per mandare all'url
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					Desktop.getDesktop().browse(new URI(messageLabel.getText()));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
					Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
				}
			}
		});

	}

	private void autoPosition() {
		// Per il posizionamento in basso a destra
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		// altezza taskbar
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
		this.setLocation(scrSize.width - 5 - this.getWidth(), scrSize.height - 5 - toolHeight.bottom
				- this.getHeight());
	}

	public void show(String header, String message) {
		this.setFocusableWindowState(false);
		this.setVisible(true);
		headingLabel.setText(header);
		messageLabel.setText(message);
		this.setAlwaysOnTop(true);

		if (message.length() > 30) {
			int width = (message.length() * 6);
			this.setSize(width, 50);
			this.setShape(new RoundRectangle2D.Double(1, 1, width, 50, 20, 20));
		} else {
			this.setSize(200, 50);
			this.setShape(new RoundRectangle2D.Double(1, 1, 200, 50, 20, 20));
		}

		autoPosition();

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					for (float i = 1.00f; i >= 0; i -= 0.01f) {
						setOpacity(i);
						Thread.sleep(15);
					}
					setVisible(false);
					setOpacity(1.00f);
				} catch (InterruptedException e) {
					e.printStackTrace();
                    Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
				}
			}
		}.start();

	}

	public void wrongPassword() {
		show("Wrong password", "Correct the password server!");
	}
    
    public void serverFull(){
        show("Server full!", ":(");
    }
    
    public void fileTooLarge(){
        show("Server: file too large!", "This file exceeds the maximum size allowed");
    }
    
    public void connectionError(){
        show("Connection Error!", "Check your connection or credential");
    }

}
