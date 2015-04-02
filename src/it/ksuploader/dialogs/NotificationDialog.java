package it.ksuploader.dialogs;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class NotificationDialog {
	private final JDialog dialogFrame;
	private JLabel headingLabel;
	private JLabel messageLabel;

	public NotificationDialog() {

		dialogFrame = new JDialog();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			System.err.println(ex.toString());
		}

		dialogFrame.setSize(200, 50);
		dialogFrame.setLayout(new GridBagLayout());

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

		xButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogFrame.dispose();
			}
		});

		// Click nel JDialog per mandare all'url
		dialogFrame.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					Desktop.getDesktop().browse(new URI(messageLabel.getText()));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void autoPosition() {
		// Per il posizionamento in basso a destra
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		// altezza taskbar
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(dialogFrame.getGraphicsConfiguration());
		dialogFrame.setLocation(scrSize.width - 5 - dialogFrame.getWidth(), scrSize.height - 5 - toolHeight.bottom
				- dialogFrame.getHeight());
	}

	public void show(String header, String message) {
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

		autoPosition();

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
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
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

}
